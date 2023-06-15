package com.bonc.assetservice.apiserver.server.service.adb;

import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryRespVO;

/**
 * @ClassName AssetQueryService
 * @Author 李维帅
 * @Date 2022/6/17 17:59
 * @Version 1.0
 **/
public interface AssetQueryService<T> {
    /**
     * 查询数据
     * @Author 李维帅
     * @Date 2022/6/17 18:01
     * @param t 数据模型
     * @return com.bonc.assetservice.assetquery.vo.query.SyncAssetQueryRespVO
     **/
    SyncAssetQueryRespVO getData(T t);


}
