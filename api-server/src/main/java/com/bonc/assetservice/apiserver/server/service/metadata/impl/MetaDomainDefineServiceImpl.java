package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaDomainDefineService;
import com.bonc.assetservice.metadata.entity.MetaDomainDefine;
import com.bonc.assetservice.metadata.mapper.MetaDomainDefineMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_domain_define
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaDomainDefineServiceImpl extends ServiceImpl<MetaDomainDefineMapper, MetaDomainDefine> implements IMetaDomainDefineService {

}
