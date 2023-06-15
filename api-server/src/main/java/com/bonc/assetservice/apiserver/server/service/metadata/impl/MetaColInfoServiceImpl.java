package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaColInfoService;
import com.bonc.assetservice.metadata.entity.MetaColInfo;
import com.bonc.assetservice.metadata.mapper.MetaColInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_col_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaColInfoServiceImpl extends ServiceImpl<MetaColInfoMapper, MetaColInfo> implements IMetaColInfoService {

}
