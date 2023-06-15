package com.bonc.assetservice.apiserver.server.service.apiserver.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.data.entity.UserDatasetColInfo;
import com.bonc.assetservice.apiserver.data.mapper.UserDatasetColInfoMapper;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetColInfoService;
import org.springframework.stereotype.Service;

/**
 * @Description: tenant_info
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Service
@DS("apiserver")
public class UserDatasetColInfoServiceImpl extends ServiceImpl<UserDatasetColInfoMapper, UserDatasetColInfo> implements IUserDatasetColInfoService {

}
