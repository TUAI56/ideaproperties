package com.bonc.assetservice.apiserver.server.service.apiserver.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.data.entity.AsyncAssetqueryReq;
import com.bonc.assetservice.apiserver.data.mapper.AsyncAssetqueryReqMapper;
import com.bonc.assetservice.apiserver.server.service.apiserver.IAsyncAssetqueryReqService;
import org.springframework.stereotype.Service;

/**
 * @Description: async_assetquery_req
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Service
@DS("apiserver")
public class AsyncAssetqueryReqServiceImpl extends ServiceImpl<AsyncAssetqueryReqMapper, AsyncAssetqueryReq> implements IAsyncAssetqueryReqService {

    @Override
    public boolean updateState(Long id, Integer state) {
        AsyncAssetqueryReq asyncAssetqueryReq = this.getById(id);
        if (asyncAssetqueryReq != null) {
            asyncAssetqueryReq.setState(state);
            return this.updateById(asyncAssetqueryReq);
        }
        return false;
    }
}
