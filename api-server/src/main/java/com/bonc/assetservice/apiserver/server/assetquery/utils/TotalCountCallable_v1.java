package com.bonc.assetservice.apiserver.server.assetquery.utils;

import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @ClassName TotalCountCallable_v1
 * @Author 李维帅
 * @Date 2022/7/1 11:22
 * @Version 1.0
 **/
@Slf4j
public class TotalCountCallable_v1 implements Callable<Integer> {

    /**
     * 查询执行器
     **/
    private final QueryExecutor queryExecutor;

    /**
     * 数据模型
     **/
    private final DataModel_v1 dataModelV1;


    public TotalCountCallable_v1(QueryExecutor queryExecutor, DataModel_v1 dataModelV1) {
        this.queryExecutor = queryExecutor;
        this.dataModelV1 = dataModelV1;
    }


    @Override
    public Integer call() {
        return queryExecutor.queryTotalCount(dataModelV1);
    }

}
