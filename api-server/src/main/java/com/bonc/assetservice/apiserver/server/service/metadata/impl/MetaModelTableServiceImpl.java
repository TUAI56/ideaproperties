package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaModelTableService;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_model_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaModelTableServiceImpl extends ServiceImpl<MetaModelTableMapper, MetaModelTable> implements IMetaModelTableService {

}
