package com;
import cn.hutool.json.JSONUtil;
import com.bonc.assetservice.apiserver.consumer.ConsumerApplication;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IAsyncAssetqueryReqService;
import com.bonc.assetservice.apiserver.data.entity.AsyncAssetqueryReq;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/10/21
 * @Description:
 */


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerApplication.class)
public class TestTe {

    @Resource
    private IAsyncAssetqueryReqService asyncAssetqueryReqService;

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;
    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.user}")
    private String hdfsUser;

    //外表文件路径
    @Value("${hdfs.path}")
    private String hdfsPath;
    @Test
    public void testFun() {

        //Map map = JSONUtil.toBean(message, Map.class);
        // 更新数据库记录状态
        String id = "1544244980931768322";
        String querySql = "select * from LABEL_TEST ";
        String tqAppUUId = "tq_appuuid_1111112";
        //查询下发参数
        AsyncAssetqueryReq req = asyncAssetqueryReqService.getById(id);

        //跟新任务状态 执行中
        //boolean result = asyncAssetqueryReqService.updateState(id, AsyncAssetqueryReq.STATE_PROCESSING);
        //log.info("更新数据库记录【{}】状态{}", id, result ? "成功" : "失败");
        // 将下发状态放入消息队列
        try {
            //创建外表sql
            String ctSql = "create table wb_" + tqAppUUId + "_" + id + " as " + querySql + " where 1=2 " + "ENGINE='HDFS'\n" +
                    "TABLE_PROPERTIES='{\n" +
                    "    \"format\":\"" + req.getFileType() + "\",\n" +
                    "    \"delimiter\":\"" + req.getSeparateChar() + "\",\n" +
                    "    \"hdfs_user\":\"" + hdfsUser + "\",\n" +
                    "    \"hdfs_url\":\"" + hdfsUri + hdfsPath + tqAppUUId + "_" + id + "\"\n" +
                    "}'";
            PlainSelect plainSelect = null;
            log.info("查询SQL：{}", plainSelect);
            SqlSession sqlSession = null;
            Connection connection = null;
            sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
            connection = sqlSession.getConnection();
            boolean rs;
            Statement statement = null;

            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            //创建外表
            rs = statement.execute(ctSql);
            log.info("执行创建外表sql:{}", ctSql);
            //执行插入语句
            statement.execute(" /*+task_writer_count=10,hash_partition_count_table_writer_ratio=0.01,oss_max_write_file_size=1024,output_file_max_line=10000000, output_filename=" + tqAppUUId + id + " */  insert into wb_" + tqAppUUId + id + " " + querySql);
            log.info("执行插入外表sql:{}", "/*+task_writer_count=10,hash_partition_count_table_writer_ratio=0.01,oss_max_write_file_size=1024,output_file_max_line=10000000, output_filename=" + tqAppUUId + id + " */ insert into wb_" + id + " " + querySql);
            statement.close();
            connection.close();
            sqlSession.close();

        } catch (Exception e) {

        }
    }

}
