package com.bonc.assetservice.apiserver.consumer.service.apiserver.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.data.entity.ResourceInfo;
import com.bonc.assetservice.apiserver.data.mapper.ResourceInfoMapper;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IResourceInfoService;
import org.springframework.stereotype.Service;

/**
 * @Description: resource_info
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Service
@DS("apiserver")
public class ResourceInfoServiceImpl extends ServiceImpl<ResourceInfoMapper, ResourceInfo> implements IResourceInfoService {

}
