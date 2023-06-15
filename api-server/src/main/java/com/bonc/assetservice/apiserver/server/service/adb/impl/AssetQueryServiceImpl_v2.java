package com.bonc.assetservice.apiserver.server.service.adb.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.PageUtil;
import com.alibaba.druid.util.JdbcUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.SqlBuilder_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import com.bonc.assetservice.apiserver.server.assetquery.utils.QueryExecutor;
import com.bonc.assetservice.apiserver.server.assetquery.utils.TotalCountCallable_v2;
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
public class AssetQueryServiceImpl_v2 implements AssetQueryService<DataModel_v2> {

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    SqlBuilder_v2 sqlBuilder_v2_;



    @Override
    public SyncAssetQueryRespVO getData(DataModel_v2 dataModel) {
        //初始化返回数据的格式
        SyncAssetQueryRespVO syncAssetQueryRespVO = new SyncAssetQueryRespVO();
        //初始化返回字段
        List<SyncAssetQueryRespVO.ReturnField> fieldList = new ArrayList<>();
        //定于Integer
        AtomicInteger index = new AtomicInteger();

        dataModel.getQueryFields().forEach(queryField ->{
            fieldList.add(new SyncAssetQueryRespVO.ReturnField(String.valueOf("asset".equals(queryField.getQueryType())?queryField.getAsset().getAssetId():queryField.getDataSet().getDatasetId()), queryField.getDisplayName(), "asset".equals(queryField.getQueryType())?queryField.getAsset().getAssetCode():queryField.getDataSet().getCode()));
            //获取deviceNumber列的index
            if("asset".equals(queryField.getQueryType()) && queryField.getAsset().getAssetCode().equals("deviceNumber")){
                index.set(dataModel.getQueryFields().indexOf(queryField));
            }
        });
        syncAssetQueryRespVO.setFields(fieldList);
        syncAssetQueryRespVO.setSize(dataModel.getPageInfo().getLimit());
        syncAssetQueryRespVO.setCurrent(dataModel.getPageInfo().getOffset());
        List<QueryField_v2> fields = dataModel.getQueryFields();
        SqlSession sqlSession = null;
        Connection connection = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
            connection = sqlSession.getConnection();
            QueryExecutor queryExecutor = new QueryExecutor(connection, sqlBuilder_v2_);
            // 查询总数异步方法
            Future<Integer> future = asyncQueryTotalCount_v2_0(queryExecutor, dataModel);

            List<SyncAssetQueryRespVO.Row> result = new ArrayList<SyncAssetQueryRespVO.Row>() ;

           // String sql = SQLUtils.getsqlV2(dataModel);

            queryExecutor.query(sqlBuilder_v2_.buildPlainSelect(dataModel).toString()).forEach(queryResult-> {
                SyncAssetQueryRespVO.Row row = new SyncAssetQueryRespVO.Row();
                List<SyncAssetQueryRespVO.Column> clos = new ArrayList<SyncAssetQueryRespVO.Column>();

                for(int i=0; i < queryResult.size(); i++){
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
            if (dataModel.isGroupCount()) {
                // 当异步查询总数失败时，再次查询总数
                if (syncAssetQueryRespVO.getTotal() == null) {
                    DataModel_v2 countDataModel = dataModel.clone();
                    countDataModel.setPageInfo(null);
                    countDataModel.setSortFields(null);
                    syncAssetQueryRespVO.setTotal(queryExecutor.queryTotalCount_v2_0(countDataModel));
                }
                syncAssetQueryRespVO.setPages(PageUtil.totalPage(syncAssetQueryRespVO.getTotal(), syncAssetQueryRespVO.getSize()));
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        } catch (CloneNotSupportedException e) {
            log.error("复制DataModel时异常：{}", e.getMessage());
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
     * @param dataModel 数据模型
     * @return java.util.concurrent.Future<java.lang.Integer>
     **/
    private Future<Integer> asyncQueryTotalCount_v2_0(QueryExecutor queryExecutor, DataModel_v2 dataModel) throws CloneNotSupportedException {
        if (dataModel.isGroupCount()) {
            DataModel_v2 countDataModel = dataModel.clone();
            countDataModel.setPageInfo(null);
            countDataModel.setSortFields(null);
            TotalCountCallable_v2 totalCountCallable = new TotalCountCallable_v2(queryExecutor, countDataModel);
            return ThreadUtil.execAsync(totalCountCallable);
        }
        return null;
    }



}
