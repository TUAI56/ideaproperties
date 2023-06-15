package com.bonc.framework.common.util;

import com.alibaba.druid.util.JdbcUtils;
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

@Slf4j
public class QueryExecutorCom {

    private final Connection connection;

    public QueryExecutorCom(Connection connection){
        this.connection = connection;
    }

    /**
     * 通过sql查询
     * @Author 李维帅
     * @Date 2022/6/20 11:40
     * @param sql SQL
     * @return java.util.List<java.util.List<java.lang.String>>
     **/
    public  List<List<String>> query(String sql) {
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
