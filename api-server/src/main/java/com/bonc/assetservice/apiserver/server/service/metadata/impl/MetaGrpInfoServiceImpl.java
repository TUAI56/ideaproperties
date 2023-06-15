package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaGrpInfoService;
import com.bonc.assetservice.metadata.entity.MetaGrpInfo;
import com.bonc.assetservice.metadata.mapper.MetaGrpInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_grp_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaGrpInfoServiceImpl extends ServiceImpl<MetaGrpInfoMapper, MetaGrpInfo> implements IMetaGrpInfoService {

}
