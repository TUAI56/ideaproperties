package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaEntityTableService;
import com.bonc.assetservice.metadata.appmodel.EntityTableAcct;
import com.bonc.assetservice.metadata.entity.MetaEntityTable;
import com.bonc.assetservice.metadata.mapper.MetaEntityTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: meta_entity_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
@DS("metadata")
public class MetaEntityTableServiceImpl extends ServiceImpl<MetaEntityTableMapper, MetaEntityTable> implements IMetaEntityTableService {
    @Autowired
    MetaEntityTableMapper entityTableMapper;

    @Override
    public Map<String, EntityTableAcct> getEntityTableAcct(List<String> tableCodes) {
        List<EntityTableAcct> entityTableInfoList = entityTableMapper.getEntityTableAcct(tableCodes);

        Map<String, EntityTableAcct> ret = new HashMap<>();
        for (EntityTableAcct one : entityTableInfoList) {
            ret.put(one.getTableCode(), one);
        }

        return ret;
    }

}
