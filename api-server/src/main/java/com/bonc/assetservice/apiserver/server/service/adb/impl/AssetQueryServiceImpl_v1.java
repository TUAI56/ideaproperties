package com.bonc.assetservice.apiserver.server.service.adb.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.PageUtil;
import com.alibaba.druid.util.JdbcUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.SqlBuilder_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v1;
import com.bonc.assetservice.apiserver.server.assetquery.utils.QueryExecutor;
import com.bonc.assetservice.apiserver.server.assetquery.utils.TotalCountCallable_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryRespVO;
import com.bonc.assetservice.apiserver.server.service.adb.AssetQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName AssetQueryServiceImpl
 * @Author 李维帅
 * @Date 2022/6/17 17:59
 * @Version 1.0
 **/
@Service
@DS("adb")
@Slf4j
public class AssetQueryServiceImpl_v1 implements AssetQueryService<DataModel_v1> {

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    SqlBuilder_v1 sqlBuilderV1;

    @Override
    public SyncAssetQueryRespVO getData(DataModel_v1 dataModelV1) {
        //初始化返回数据的格式
        SyncAssetQueryRespVO syncAssetQueryRespVO = new SyncAssetQueryRespVO();
        //初始化返回字段
        List<SyncAssetQueryRespVO.ReturnField> fieldList = new ArrayList<>();
        //定于Integer
        AtomicInteger index = new AtomicInteger();
        //遍历queryField查询是否有deviceNumber字段
        dataModelV1.getQueryFieldV1s().forEach(queryField ->{
            fieldList.add(new SyncAssetQueryRespVO.ReturnField(String.valueOf(queryField.getAssetId()), queryField.getDisplayName(), queryField.getAssetCode()));
            //获取deviceNumber列的index
            if(queryField.getAssetCode().equals("deviceNumber")){
                index.set(dataModelV1.getQueryFieldV1s().indexOf(queryField));
            }
        });
        syncAssetQueryRespVO.setFields(fieldList);
        syncAssetQueryRespVO.setSize(dataModelV1.getPageInfo().getLimit());
        syncAssetQueryRespVO.setCurrent(dataModelV1.getPageInfo().getOffset());
        List<QueryField_v1> fields = dataModelV1.getQueryFieldV1s();
        SqlSession sqlSession = null;
        Connection connection = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
            connection = sqlSession.getConnection();
            QueryExecutor queryExecutor = new QueryExecutor(connection, sqlBuilderV1);
            // 查询总数异步方法
            Future<Integer> future = asyncQueryTotalCount(queryExecutor, dataModelV1);

            List<SyncAssetQueryRespVO.Row> result = new ArrayList<SyncAssetQueryRespVO.Row>();


            queryExecutor.query(sqlBuilderV1.buildPlainSelect(dataModelV1).toString()).forEach(queryResult-> {

                List<SyncAssetQueryRespVO.Row> rows = new ArrayList<SyncAssetQueryRespVO.Row>();
                SyncAssetQueryRespVO.Row row = new SyncAssetQueryRespVO.Row();
                List<SyncAssetQueryRespVO.Column> clos = new ArrayList<SyncAssetQueryRespVO.Column>();

                for (int i = 0; i < queryResult.size(); i++) {
                    SyncAssetQueryRespVO.Column clo = new SyncAssetQueryRespVO.Column(fieldList.get(i).getAssetId(), fieldList.get(i).getAccsetCode(), queryResult.get(i));
                    clos.add(clo);
                }
                row.setColumns(clos);
                result.add(row);
            });

            syncAssetQueryRespVO.setRows(result);
            // 获取异步查询总数结果
            if (future != null) {
                try {
                    syncAssetQueryRespVO.setTotal(future.get());
                } catch (Exception e) {
                    log.error("获取总数时出错：{}", e.getMessage());
                }
            }
            if (dataModelV1.isGroupCount()) {
                // 当异步查询总数失败时，再次查询总数
                if (syncAssetQueryRespVO.getTotal() == null) {
                    DataModel_v1 countDataModelV1 = dataModelV1.clone();
                    countDataModelV1.setPageInfo(null);
                    countDataModelV1.setSortFields(null);
                    syncAssetQueryRespVO.setTotal(queryExecutor.queryTotalCount(countDataModelV1));
                }
                syncAssetQueryRespVO.setPages(PageUtil.totalPage(syncAssetQueryRespVO.getTotal(), syncAssetQueryRespVO.getSize()));
            }
        } catch (IllegalArgumentException e) {
            log.error("", e);
        } catch (CloneNotSupportedException e) {
            log.error("复制DataModel时异常：", e);
        } catch (Exception e) {
            log.error("实时查询接口获取数据时异常", e);
            throw e;
        } finally {
            JdbcUtils.close(connection);
            JdbcUtils.close(sqlSession);
        }
        return syncAssetQueryRespVO;
    }




    /**
     * 异步查询总数
     * @Author 李维帅
     * @Date 2022/7/1 15:01
     * @param queryExecutor 查询执行器
     * @param dataModelV1 数据模型
     * @return java.util.concurrent.Future<java.lang.Integer>
     **/
    private Future<Integer> asyncQueryTotalCount(QueryExecutor queryExecutor, DataModel_v1 dataModelV1) throws CloneNotSupportedException {
        if (dataModelV1.isGroupCount()) {
            DataModel_v1 countDataModelV1 = dataModelV1.clone();
            countDataModelV1.setPageInfo(null);
            countDataModelV1.setSortFields(null);
            TotalCountCallable_v1 totalCountCallableV1 = new TotalCountCallable_v1(queryExecutor, countDataModelV1);
            return ThreadUtil.execAsync(totalCountCallableV1);
        }
        return null;
    }







}
