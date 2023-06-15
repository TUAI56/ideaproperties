package com.bonc.assetservice.apiserver.server.service.metadata.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaDbInfoService;
import com.bonc.assetservice.metadata.entity.MetaDbInfo;
import com.bonc.assetservice.metadata.mapper.MetaDbInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_db_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaDbInfoServiceImpl extends ServiceImpl<MetaDbInfoMapper, MetaDbInfo> implements IMetaDbInfoService {

}
