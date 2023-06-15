package com.bonc.assetservice.apiserver.server.service.metadata.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaStaticDimInfoService;
import com.bonc.assetservice.metadata.entity.MetaStaticDimInfo;
import com.bonc.assetservice.metadata.mapper.MetaStaticDimInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_static_dim_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaStaticDimInfoServiceImpl extends ServiceImpl<MetaStaticDimInfoMapper, MetaStaticDimInfo> implements IMetaStaticDimInfoService {

}
