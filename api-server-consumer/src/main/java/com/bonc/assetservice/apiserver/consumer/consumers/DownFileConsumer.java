package com.bonc.assetservice.apiserver.consumer.consumers;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IAsyncAssetqueryReqService;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IResourceInfoService;
import com.bonc.assetservice.apiserver.data.entity.AsyncAssetqueryReq;
import com.bonc.assetservice.apiserver.data.entity.ResourceInfo;
import com.bonc.filesystem.FileView;
import com.bonc.filesystem.hdfs.HdfsFileSystem;
import com.bonc.filesystem.sftp.SftpFileSystem;
import com.bonc.filesystem.util.IOUtils;
import com.bonc.framework.common.enums.Enums;
import com.bonc.framework.common.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.ibatis.session.SqlSession;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DownFileConsumer
 * @Author lzk
 * @Date 2022/7/6 16:49
 * @Version 1.0
 **/
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumerGroup}", topic = "${rocketmq.topic}", consumeMode = ConsumeMode.CONCURRENTLY)
@DS("adb")
public class DownFileConsumer implements RocketMQListener<String> {

    @Resource
    private IAsyncAssetqueryReqService asyncAssetqueryReqService;
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @Resource
    private IResourceInfoService resourceInfoService;

    @Value("${aes.key}")
    private String aesKey;

    @Value("${file.dbpath}")
    private String dbPath;
    @Value("${file.serpath}")
    private String serPath;
    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.user}")
    private String hdfsUser;

    //外表文件路径
    @Value("${hdfs.path}")
    private String hdfsPath;
    @Value("${rocketmq.async-query-status-topic}")
    private String asyncQeuryStatusTopic;
    @Resource
    private DefaultMQProducer defaultMQProducer;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public void onMessage(String message) {
        SftpFileSystem sftpFileSystem = null;
        HdfsFileSystem hdfsFileSystem = null;
        Long id = 0L;
        String tqAppUUId = "";
        SqlSession sqlSession = null;
        Connection connection = null;
        Statement statement = null;
        OutputStream sd = null;
        InputStream inputStream = null;
        log.info("接收到的消息：{}", message);
        try {
            Map map = JSONUtil.toBean(message, Map.class);
            // 更新数据库记录状态
            id = Long.parseLong(map.get("id").toString());
            String querySql = map.get("querySql").toString();
            tqAppUUId = map.get("tqAppUUId").toString();
            String key = "async-hdfs2sftp-" + String.valueOf(id);
            if (redisTemplate.hasKey(key)) {
                log.error("该消息已在消费中，略过:" + key);
                return;
            }

            redisTemplate.opsForValue().set(key, message, 1, TimeUnit.DAYS);
            log.info("写入redis消息:" + key);
            //查询下发参数
            AsyncAssetqueryReq req = asyncAssetqueryReqService.getById(id);

            //跟新任务状态 执行中
            boolean result = asyncAssetqueryReqService.updateState(id, AsyncAssetqueryReq.STATE_PROCESSING);
            log.info("更新数据库记录【{}】状态{}", id, result ? "成功" : "失败");
            // 将下发状态放入消息队列
            String hdfsFilePath = hdfsPath + tqAppUUId + "_" + id;
            //创建外表sql
            String ctSql = "create table IF NOT EXISTS wb_" + tqAppUUId + "_" + id + " as (select * from  (" + querySql + " ) where 1=2) " + "ENGINE='HDFS'\n" +
                    "TABLE_PROPERTIES='{\n" +
                    "    \"format\":\"csv\",\n" +
                    "    \"delimiter\":\"" + req.getSeparateChar() + "\",\n" +
                    "    \"hdfs_user\":\"" + hdfsUser + "\",\n" +
                    "    \"hdfs_url\":\"" + hdfsUri + hdfsFilePath + "\"\n" +
                    "}'";
            PlainSelect plainSelect = null;
            log.info("组装创建外部sql：{}", ctSql);

            sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
            connection = sqlSession.getConnection();
            boolean rs;


            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            //创建外表
            rs = statement.execute(ctSql);
            log.info("执行创建外表sql:{}", ctSql);
            //执行插入语句
            statement.execute(" /*+task_writer_count=16,hash_partition_count_table_writer_ratio=0.1,oss_max_write_file_size=1024,output_file_max_line=10000000, output_filename=" + tqAppUUId + "_" + id + " */  insert into wb_" + tqAppUUId + "_" + id + " " + querySql);
            log.info("执行插入外表sql:{}", "/*+task_writer_count=16,hash_partition_count_table_writer_ratio=0.1,oss_max_write_file_size=1024,output_file_max_line=10000000, output_filename=" + tqAppUUId + "_" + id + " */ insert into wb_" + tqAppUUId + "_" + id + " " + querySql);


            //hdfs文件下发到ftp
            //查询ftp信息
            ResourceInfo resourceInfo = resourceInfoService.getById(req.getResourceId());
            log.info("获取sftp信息：" + resourceInfo.getIp() + ":" + resourceInfo.getPort());
            log.info("创建sftpsystem之前，参数打印:");
            log.info("获取sftpFileSystem参数:{}-{}-{}-{}-{}", resourceInfo.getIp(), resourceInfo.getPort(), resourceInfo.getUserName(),
                    resourceInfo.getUserPwd(), req.getFilePath());
            //sftp
            sftpFileSystem = new SftpFileSystem(resourceInfo.getIp(), resourceInfo.getPort(), resourceInfo.getUserName(),
                    AESUtil.decrypt(resourceInfo.getUserPwd(), aesKey), req.getFilePath());

            sd = sftpFileSystem.create(req.getFileName() + "." + req.getFileType(), true);
            log.info("在sftp创建文件，获取到输出流");
            //hdfs
            URI uri = new URI(hdfsUri);
            log.info("hdfs信息:{}", hdfsUri);
            hdfsFileSystem = new HdfsFileSystem(uri);
            FileView[] fileViews = hdfsFileSystem.ls(hdfsFilePath);
            log.info("获取hdfs文件个数:{}", fileViews.length);
            //写入文件
            for (FileView fileView : fileViews) {
                log.info("开始文件下发。");
                inputStream = hdfsFileSystem.open(fileView.getPath());
                IOUtils.flow(inputStream, sd, false, false);
            }

            log.error("写入sftp完成！");
            String delTableSql = "drop table  wb_" + tqAppUUId + "_" + id;
            //删除外表
            statement.execute(delTableSql);
            log.info("执行删除外表sql：" + delTableSql);

            //更新数据库记录状态
            boolean res = asyncAssetqueryReqService.updateState(id, AsyncAssetqueryReq.STATE_SUCCESS);
            log.info("更新数据库记录【{}】状态{}", id, res ? "成功" : "失败");
            //mq写消息
            // 将下发状态放入消息队列
            Map<String, String> msgMap = new HashMap<>();
            msgMap.put("reqId", String.valueOf(id));
            msgMap.put("status", Enums.AsyncStatus.getNameByValue(AsyncAssetqueryReq.STATE_SUCCESS));
            msgMap.put("msg", "下发成功");
            Message sendfileMsg = new Message(asyncQeuryStatusTopic, tqAppUUId, String.valueOf(id), JSONObject.toJSONBytes(msgMap));
            SendResult sendResult = defaultMQProducer.send(sendfileMsg);
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                log.info("[标签取数] 发送文件 发送消息队列完成");
            }
            // 业务处理完成，删除redis记录
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("[标签取数] 捕获业务异常");
            log.error(e.toString());
            //e.printStackTrace();

            try {
                boolean resu = asyncAssetqueryReqService.updateState(id, AsyncAssetqueryReq.STATE_EXEC_FAIL);
                log.info("更新数据库记录【{}】状态{}", id, resu ? "成功" : "失败");
                //下发状态写入mq
                Map<String, String> msgMap = new HashMap<>();
                msgMap.put("reqId", String.valueOf(id));
                msgMap.put("status", Enums.AsyncStatus.getNameByValue(AsyncAssetqueryReq.STATE_EXEC_FAIL));
                msgMap.put("msg", "下载失败");

                Message sendfileMsg = new Message(asyncQeuryStatusTopic, tqAppUUId, String.valueOf(id), JSONObject.toJSONBytes(msgMap));

                SendResult sendResult = null;
                sendResult = defaultMQProducer.send(sendfileMsg);
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    log.info("[标签取数] 发送文件 发送消息队列完成");
                }
            } catch (Exception ex) {
                log.info("[标签取数] 捕获业务异常后 更新请求状态再次捕获异常");
                ex.printStackTrace();
            }


        } finally {
            try {
                if (inputStream != null) {
                    IOUtils.close(inputStream);
                }
                if (sd != null) {
                    IOUtils.close(sd);
                }
                if (hdfsFileSystem != null) {
                    hdfsFileSystem.close();
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
                log.error("finally操作关闭流、数据库连接等报错");
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }
}
