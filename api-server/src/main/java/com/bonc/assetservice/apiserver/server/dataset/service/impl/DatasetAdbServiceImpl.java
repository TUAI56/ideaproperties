package com.bonc.assetservice.apiserver.server.dataset.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.bonc.assetservice.apiserver.server.dataset.service.IDatasetAdbService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@Slf4j
@DS("adb")
public class DatasetAdbServiceImpl implements IDatasetAdbService {


    @Resource
    private SqlSessionTemplate sqlSessionTemplate;
    @Override
    public boolean exeSqlInAdb(String sql) {

        SqlSession sqlSession = null;
        Connection connection = null;
        sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
        connection = sqlSession.getConnection();
        boolean rs ;
        Statement statement = null;

        try {
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            //执行sql
            rs = statement.execute(sql);
            log.info("adb库执行sql:{}", sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

}
