package com.bonc.assetservice.apiserver.consumer.consumers;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.bonc.assetservice.apiserver.consumer.config.ImportParams;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IResourceInfoService;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IUserDatasetService;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IUserDatasetUpdReqService;
import com.bonc.assetservice.apiserver.consumer.util.ExcelToCSV;
import com.bonc.assetservice.apiserver.data.entity.ResourceInfo;
import com.bonc.assetservice.apiserver.data.entity.UserDataset;
import com.bonc.assetservice.apiserver.data.entity.UserDatasetUpdReq;
import com.bonc.filesystem.FileSystemException;
import com.bonc.filesystem.sftp.SftpFileSystem;
import com.bonc.filesystem.util.IOUtils;
import com.bonc.framework.common.enums.Enums;
import com.bonc.framework.common.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DownFileConsumer
 * @Author 李维帅
 * @Date 2022/7/6 16:49
 * @Version 1.0
 **/
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.upddataConsumerGroup}", topic = "${rocketmq.datasetupddatatopic}", consumeMode = ConsumeMode.CONCURRENTLY)
@DS("adb")
public class UpdDatasetConsumer implements RocketMQListener<String> {


    @Value("${rocketmq.upddataset-query-status-topic}")
    private String upddatasetQueryStatusTopic;
    @Resource
    private DefaultMQProducer defaultMQProducer;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private IResourceInfoService resourceInfoService;
    @Resource
    private IUserDatasetUpdReqService userDatasetUpdReqService;
    @Resource
    private IUserDatasetService userDatasetService;
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;


    @Value("${aes.key}")
    private String aesKey;
    @Resource
    private ImportParams importParams;


    @Override
    public void onMessage(String message) {

        Long id = 0L;
        String tqAppUUId = "";
        SqlSession sqlSession = null;
        Connection connection = null;
        Statement statement = null;
        SftpFileSystem sftpFileSystem = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        String datasetId = "";
        InputStream is = null;
        FileOutputStream fos = null;

        log.info("接收到的消息：{}", message);
        try {
            Map map = JSONUtil.toBean(message, Map.class);
            // 更新数据库记录状态
            id = Long.parseLong(map.get("id").toString());
            tqAppUUId = map.get("tqAppUUId").toString();
            String key = "upddataset-sftp2adb-" + String.valueOf(id);
            if (redisTemplate.hasKey(key)) {
                log.info("该消息已在消费中，略过:" + key);
                return;
            }

            redisTemplate.opsForValue().set(key, message, 1, TimeUnit.DAYS);
            log.info("写入redis消息:{}", key);
            //查询下发参数
            UserDatasetUpdReq req = userDatasetUpdReqService.getById(id);
            //查询自定义数据集信息
            UserDataset userDataset = userDatasetService.getById(req.getDatasetId());
            datasetId = userDataset.getDatasetId();
            log.info("获取数据集信息:" + userDataset);

            //跟新任务状态 执行中
            boolean result = userDatasetUpdReqService.updateState(id, UserDatasetUpdReq.STATE_PROCESSING);
            log.info("更新数据库记录【{}】状态{}", id, result ? "成功" : "失败");
            //清空自定义数据集表中的数据
            sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
            connection = sqlSession.getConnection();
            boolean rs;

            String truncateSql = "TRUNCATE table " + userDataset.getEntityTableCode();
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = statement.execute(truncateSql);
            log.info("执行清空自定义数据集表数据sql:{}成功！", truncateSql);

            //查询ftp信息
            ResourceInfo resourceInfo = resourceInfoService.getById(req.getResourceId());
            log.info("获取到sftp信息，IP:{},PORT:{}", resourceInfo.getIp(), resourceInfo.getPort());

            //sftp
            log.info("开始获取sftpFileSystem");
            sftpFileSystem = new SftpFileSystem(resourceInfo.getIp(), resourceInfo.getPort(), resourceInfo.getUserName(),
                    AESUtil.decrypt(resourceInfo.getUserPwd(), aesKey), resourceInfo.getPath());
            inputStream = sftpFileSystem.open(req.getFileName());
            // csv走下面的，sxs走工具类
            log.info("读取sftp数据文件获取文件流{}", inputStream);
            if(inputStream == null){
                log.error("未读取到sftp数据文件！{}", resourceInfo.getPath()+ req.getFilePath()+ req.getFileName());
                updateStatusAndSendMsg(id, datasetId, UserDatasetUpdReq.STATE_FILE_PATH_EXCEPTION, "数据文件名称或路径有误", tqAppUUId);
                redisTemplate.delete(key);
                return;
            }
            //自定义数据集数据写入本地文件
            String filePathAndName = getJarFilePath() + key + ".csv";
            log.info("创建本地数据文件名称:{}", filePathAndName);
            // 添加内容 start
            // 判断文件类型 csv\txt sxel\sxs
            if (! req.getFileName().replaceAll(".xls","").equals(req.getFileName())
                    ||! req.getFileName().replaceAll(".xlsx","").equals(req.getFileName())){
                // 将 字节输入流转换为字节输入流
                ExcelToCSV excelToCSV = new ExcelToCSV();
                excelToCSV.excelToCsv(req.getFileName(),inputStream,filePathAndName);
            }else {
                outputStream = new FileOutputStream(new File(filePathAndName));
                int c;
                while ((c = inputStream.read()) != -1) {
                    outputStream.write(c);
                }
            }
            // end
            log.info("本地数据文件写入完成！");
            if (StringUtils.isBlank(importParams.getJar_path())) {
                importParams.setJar_path(getJarFilePath() + "adb-import-tool.jar");
            }

            String sh = "java -Xmx12G -Xms12G -jar " + importParams.getJar_path() + " -h 10.177.64.78 -u" + importParams.getUser() +
                    " -p" + importParams.getPassword() + " -P" + importParams.getPort() + " -D" + importParams.getDatabase() + " --lineSeparator " + importParams.getLineSeparator() +
                    " --delimiter " + importParams.getDelimiter() + " --encoding " + importParams.getEncoding() + " --nullAsQuotes " + importParams.getNullAsQuotes() +
                    " --dataFile " + filePathAndName + " --tableName " + userDataset.getEntityTableCode() + " --concurrency " + importParams.getConcurrency() +
                    " --batchSize " + importParams.getBatchSize() + " --printRowCount " + importParams.getPrintRowCount() + " --maxConcurrentNumOfFilesToImport " + importParams.getMaxConcurrentNumOfFilesToImport() +
                    " --windowSize " + importParams.getWindowSize() + " --failureSqlPrintLengthLimit " + importParams.getFailureSqlPrintLengthLimit() + " --disableInsertOnlyPrintSql " + importParams.getDisableInsertOnlyPrintSql() +
                    " --skipHeader " + importParams.getSkipHeader() + " --encryptPassword " + importParams.getEncryptPassword() + " --escapeSlashAndSingleQuote " + importParams.getEscapeSlashAndSingleQuote() +
                    " --ignoreErrors " + importParams.getIgnoreErrors() + " --printErrorSql " + importParams.getPrintErrorSql() + " --printErrorStackTrace " + importParams.getPrintErrorStackTrace() +
                    " --insertWithColumnNames " + importParams.getInsertWithColumnNames();
            log.info("开始执行文件导入脚本:" + "java -Xmx12G -Xms12G -jar " + importParams.getJar_path() + " -h" + importParams.getHost() + " -u" + importParams.getUser() +
                    " -p********" + " -P" + importParams.getPort() + " -D" + importParams.getDatabase() + " --lineSeparator " + importParams.getLineSeparator() +
                    " --delimiter " + importParams.getDelimiter() + " --encoding " + importParams.getEncoding() + " --nullAsQuotes " + importParams.getNullAsQuotes() +
                    " --dataFile " + filePathAndName + " --tableName " + userDataset.getEntityTableCode() + " --concurrency " + importParams.getConcurrency() +
                    " --batchSize " + importParams.getBatchSize() + " --printRowCount " + importParams.getPrintRowCount() + " --maxConcurrentNumOfFilesToImport " + importParams.getMaxConcurrentNumOfFilesToImport() +
                    " --windowSize " + importParams.getWindowSize() + " --failureSqlPrintLengthLimit " + importParams.getFailureSqlPrintLengthLimit() + " --disableInsertOnlyPrintSql " + importParams.getDisableInsertOnlyPrintSql() +
                    " --skipHeader " + importParams.getSkipHeader() + " --encryptPassword " + importParams.getEncryptPassword() + " --escapeSlashAndSingleQuote " + importParams.getEscapeSlashAndSingleQuote() +
                    " --ignoreErrors " + importParams.getIgnoreErrors() + " --printErrorSql " + importParams.getPrintErrorSql() + " --printErrorStackTrace " + importParams.getPrintErrorStackTrace() +
                    " --insertWithColumnNames " + importParams.getInsertWithColumnNames());
            //执行导入脚本，并将执行日志输出到文件
            is = Runtime.getRuntime().exec(sh).getInputStream();
            fos = new FileOutputStream(getJarFilePath() + "adbimporttools-" + key + "info.log");
            byte[] b = new byte[1024];
            while ((is.read(b)) != -1) {
                fos.write(b);// 写入数据
            }

            /*// 保存数据
            int re = Runtime.getRuntime().exec(sh).waitFor();*/
            ResultSet resultSet = statement.executeQuery("select *  from " + userDataset.getEntityTableCode() + " LIMIT 5");
            if (!resultSet.next()) {
                log.error("工具包导入失败！");
                updateStatusAndSendMsg(id, datasetId, UserDatasetUpdReq.STATE_EXEC_FAIL, "失败", tqAppUUId);
                redisTemplate.delete(key);
                return;
            }
            log.info("工具包导入数据完成！");
            //mq写消息\更新数据库记录状态
            // 将下发状态放入消息队列
            SendResult sendResult = updateStatusAndSendMsg(id, datasetId, UserDatasetUpdReq.STATE_SUCCESS, "成功", tqAppUUId);

            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                log.info("[自定义数据集] 更新数据集 发送消息队列完成");
            }
            // 业务处理完成，删除redis记录
            redisTemplate.delete(key);
        } catch (FileSystemException e) {
            log.error("[自定义数据集] 捕获网络异常"
            );
            e.printStackTrace();
            try {
                updateStatusAndSendMsg(id, datasetId, UserDatasetUpdReq.STATE_SFTPCONNECT_EXCEPTION, "sftp连接失败", tqAppUUId);
            } catch (Exception ex) {
                log.info("[自定义数据集] 捕获业务异常后 更新请求状态再次捕获异常");
                ex.printStackTrace();
            }
        }  catch (Exception e) {
            log.error("[自定义数据集] 捕获业务异常");
            e.printStackTrace();

            try {
                updateStatusAndSendMsg(id, datasetId, UserDatasetUpdReq.STATE_EXEC_FAIL, "失败", tqAppUUId);
            } catch (Exception ex) {
                log.info("[自定义数据集] 捕获业务异常后 更新请求状态再次捕获异常");
                ex.printStackTrace();
            }


        } finally {
            try {
                if (inputStream != null) {
                    IOUtils.close(inputStream);
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
                if (sqlSession != null) {
                    sqlSession.close();
                }
            } catch (Exception e) {
                log.info("[自定义数据集] finally关闭输入输出流、数据库连接时 捕获异常");
                e.printStackTrace();

            }
        }
    }

    //linux和windows下通用
    private String getJarFilePath() {
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        String path = jarFile.getParentFile().toString();
        if ("/".equals(path)) {
            return path;
        }
        return path + "/";
    }

    public static void main(String[] args) {
//        String sh = "java -Xmx12G -Xms12G -jar " + importParams.getJar_path() + " -h" + importParams.getHost() + " -u" + importParams.getUser() +
//                " -p" + importParams.getPassword() + " -P" + importParams.getPort() + " -D" + importParams.getDatabase() + " --lineSeparator " + importParams.getLineSeparator() +
//                " --delimiter " + importParams.getDelimiter() + " --encoding " + importParams.getEncoding() + " --nullAsQuotes " + importParams.getNullAsQuotes() +
//                " --dataFile " + filePathAndName + " --tableName " + userDataset.getEntityTableCode() + " --concurrency " + importParams.getConcurrency() +
//                " --batchSize " + importParams.getBatchSize() + " --printRowCount " + importParams.getPrintRowCount() + " --maxConcurrentNumOfFilesToImport " + importParams.getMaxConcurrentNumOfFilesToImport() +
//                " --windowSize " + importParams.getWindowSize() + " --failureSqlPrintLengthLimit " + importParams.getFailureSqlPrintLengthLimit() + " --disableInsertOnlyPrintSql " + importParams.getDisableInsertOnlyPrintSql() +
//                " --skipHeader " + importParams.getSkipHeader() + " --encryptPassword " + importParams.getEncryptPassword() + " --escapeSlashAndSingleQuote " + importParams.getEscapeSlashAndSingleQuote() +
//                " --ignoreErrors " + importParams.getIgnoreErrors() + " --printErrorSql " + importParams.getPrintErrorSql() + " --printErrorStackTrace " + importParams.getPrintErrorStackTrace() +
//                " --insertWithColumnNames " + importParams.getInsertWithColumnNames();
        System.out.println();
        String s = "java -Xmx12G -Xms12G -jar ./adb-import-tool.jar -h 10.177.64.78 -u labeldata -p rSFytFbveV5HPa2wIFe. -P 3000 -D dc_hh_ser_prod_000_tag --lineSeparator  '\\n' --delimiter '\\,' --encoding UTF-8 --nullAsQuotes false  --dataFile C:\\Users\\000\\Desktop\\test.csv --tableName  a_partiton_test --concurrency 40 --batchSize 2048 --printRowCount true --maxConcurrentNumOfFilesToImport 64 --windowSize 128 --failureSqlPrintLengthLimit 2000 --disableInsertOnlyPrintSql false --skipHeader true --encryptPassword false --escapeSlashAndSingleQuote true --ignoreErrors false --printErrorSql true --printErrorStackTrace true --insertWithColumnNames true";
        try {
            Process exec = Runtime.getRuntime().exec(s);
            InputStream outfile = exec.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private SendResult updateStatusAndSendMsg(Long reqId, String datasetId, Integer status, String msg, String tqAppUUId) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {

        boolean resu = userDatasetUpdReqService.updateState(reqId, status);
        log.info("更新数据库记录【{}】状态{}", reqId, resu ? "成功" : "失败");
        //下发状态写入mq
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("reqId", String.valueOf(reqId));
        msgMap.put("datasetId", datasetId);
        msgMap.put("status", Enums.UpdDatasetStatus.getNameByValue(status));
        msgMap.put("msg", msg);

        Message sendfileMsg = new Message(upddatasetQueryStatusTopic, tqAppUUId, String.valueOf(reqId), JSONObject.toJSONBytes(msgMap));

        return defaultMQProducer.send(sendfileMsg);


    }

}
