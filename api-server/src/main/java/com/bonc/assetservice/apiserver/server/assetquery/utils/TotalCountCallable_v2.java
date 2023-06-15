package com.bonc.assetservice.apiserver.server.assetquery.utils;

import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @ClassName TotalCountCallable_v1
 * @Author 李维帅
 * @Date 2022/7/1 11:22
 * @Version 1.0
 **/
@Slf4j
public class TotalCountCallable_v2 implements Callable<Integer> {

    /**
     * 查询执行器
     **/
    private final QueryExecutor queryExecutor;

    /**
     * 数据模型
     **/
    private final DataModel_v2 dataModel;


    public TotalCountCallable_v2(QueryExecutor queryExecutor, DataModel_v2 dataModel) {
        this.queryExecutor = queryExecutor;
        this.dataModel = dataModel;
    }


    @Override
    public Integer call() {
        return queryExecutor.queryTotalCount_v2_0(dataModel);
    }

}
