package com.bonc.assetservice.apiserver.server.assetquery.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.SqlBuilder_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.SqlBuilder_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.opencsv.ResultSetHelper;
import com.opencsv.ResultSetHelperService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 查询执行器
 * @ClassName QueryExecutor
 * @Author 李维帅
 * @Date 2022/6/17 15:58
 * @Version 1.0
 **/
@Slf4j

public class QueryExecutor {

    private final Connection connection;

    private SqlBuilder_v1 sqlBuilderV1;

    private SqlBuilder_v2 sqlBuilder2;

    public QueryExecutor(Connection connection, SqlBuilder_v1 sqlBuilderV1) {
        this.connection = connection;
        this.sqlBuilderV1 = sqlBuilderV1;
    }

    public QueryExecutor(Connection connection, SqlBuilder_v2 sqlBuilder) {
        this.connection = connection;
        this.sqlBuilder2 = sqlBuilder;
    }

/**
     * 通过DataModel数据模型查询
     * @Author 李维帅
     * @Date 2022/6/20 11:39
     * @param sql 要执行的sql
     * @return java.util.List<java.util.List<java.lang.String>>
     **/
//    public List<List<String>> query(String sql) {
//        return query(sql);
//    }



    /**
     * 通过sql查询
     * @Author 李维帅
     * @Date 2022/6/20 11:40
     * @param sql SQL
     * @return java.util.List<java.util.List<java.lang.String>>
     **/
    public List<List<String>> query(String sql) {
        log.info("开始执行SQL：{}", sql);
        long t = System.currentTimeMillis();
        List<List<String>> data = new ArrayList<>();
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery(sql);
            data = getData(rs);
        } catch (Exception e) {
            log.error("执行查询SQL【{}】出错：{}", sql, e.getMessage());
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(statement);
        }
        log.info("执行SQL耗时：{}s", (System.currentTimeMillis() - t) / 1000d);
        return data;
    }

    /**
     * 查询总数
     * @Author 李维帅
     * @Date 2022/6/22 17:01
     * @param dataModelV1 数据模型
     * @return void
     **/
    public Integer queryTotalCount(DataModel_v1 dataModelV1) {

        return queryTotalCount(sqlBuilderV1.buildCountSelect(dataModelV1).toString());
    }

    /**
     * 查询总数
     * @Author 李维帅
     * @Date 2022/6/22 17:01
     * @param dataModel 数据模型
     * @return void
     **/
    public Integer queryTotalCount_v2_0(DataModel_v2 dataModel) {
        return queryTotalCount(sqlBuilder2.buildCountSelect(dataModel).toString());
    }




    public Integer queryTotalCount(String sql) {
        log.info("开始执行查询总数SQL：{}", sql);
        long t = System.currentTimeMillis();
        int totalCount = 0;
        ResultSet rs = null;
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery(sql);
            rs.next();
            totalCount = rs.getInt(1);
        } catch (Exception e) {
            log.error("执行查询总数SQL【{}】出错：{}", sql, e.getMessage());
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(statement);
        }
        log.info("执行查询总数SQL耗时：{}s", (System.currentTimeMillis() - t) / 1000d);
        return totalCount;
    }

    /**
     * 执行结果转列表数据
     * @Author 李维帅
     * @Date 2022/6/20 11:41
     * @param rs 执行结果
     * @return java.util.List<java.util.List<java.lang.String>>
     **/
    private List<List<String>> getData(ResultSet rs) throws SQLException, IOException {
        List<List<String>> data = new ArrayList<>();
        ResultSetHelper resultService = new ResultSetHelperService();
        while(rs.next()) {
            data.add(Arrays.asList(resultService.getColumnValues(rs, false)));
        }
        return data;
    }

}
