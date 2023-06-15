package com.bonc.assetservice.apiserver.consumer.service.apiserver;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonc.assetservice.apiserver.data.entity.AsyncAssetqueryReq;

/**
 * @Description: async_assetquery_req
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
public interface IAsyncAssetqueryReqService extends IService<AsyncAssetqueryReq> {


    /**
     * 更新状态
     * @Author 李维帅
     * @Date 2022/7/7 15:55
     * @param id 记录id
     * @param state 状态
     * @return 是否成功
     **/
    boolean updateState(Long id, Integer state);
}
