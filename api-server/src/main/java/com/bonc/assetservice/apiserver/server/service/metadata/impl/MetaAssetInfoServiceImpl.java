package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaAssetInfoService;
import com.bonc.assetservice.metadata.appmodel.AssetCodeAndAcct;
import com.bonc.assetservice.metadata.appmodel.ModelAndEntityTableInfo;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;
import com.bonc.assetservice.metadata.mapper.MetaAssetInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: meta_asset_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
@DS("metadata")
public class MetaAssetInfoServiceImpl extends ServiceImpl<MetaAssetInfoMapper, MetaAssetInfo> implements IMetaAssetInfoService {
    @Autowired
    MetaAssetInfoMapper metaAssetInfoMapper;

    @Override
    public HashMap<Long, AssetCodeAndAcct> getAssetCodeAndAcct(List<String> assetIds) {
        if (assetIds == null || assetIds.size() == 0) {
            return null;
        }

        List<AssetCodeAndAcct> infos = metaAssetInfoMapper.getAssetCodeAndAcct(assetIds);
        HashMap<Long, AssetCodeAndAcct> ret = new HashMap<>();
        for (AssetCodeAndAcct one : infos) {
            ret.put(one.getAssetId(), one);
        }
        return ret;
    }

    @Override
    public List<ModelAndEntityTableInfo> getTableInfosByTableCodes(List<String> tableCodes) {
        return metaAssetInfoMapper.getTableInfosByTableCodes(tableCodes);
    }

    @Override
    public List<ModelAndEntityTableInfo> getDomainMasterTableInfosByTableCodes(List<String> tableCodes) {
        return metaAssetInfoMapper.getDomainMasterTableInfosByTableCodes(tableCodes);
    }
}
