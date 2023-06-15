package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaAssetDateService;
import com.bonc.assetservice.metadata.entity.MetaAssetDate;
import com.bonc.assetservice.metadata.mapper.MetaAssetDateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: meta_asset_date
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaAssetDateServiceImpl extends ServiceImpl<MetaAssetDateMapper, MetaAssetDate> implements IMetaAssetDateService {

    @Override
    public List<String> getAssetAcctList(Long assetId) {
        return baseMapper.getAssetAcctList(assetId);
    }
}
