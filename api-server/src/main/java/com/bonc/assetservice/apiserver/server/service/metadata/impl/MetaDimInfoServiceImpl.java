package com.bonc.assetservice.apiserver.server.service.metadata.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaDimInfoService;
import com.bonc.assetservice.metadata.entity.MetaDimInfo;
import com.bonc.assetservice.metadata.mapper.MetaDimInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_dim_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaDimInfoServiceImpl extends ServiceImpl<MetaDimInfoMapper, MetaDimInfo> implements IMetaDimInfoService {

}
