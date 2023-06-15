package com.bonc.assetservice.apiserver.consumer.service.apiserver.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.data.entity.UserDataset;
import com.bonc.assetservice.apiserver.data.mapper.UserDatasetMapper;
import com.bonc.assetservice.apiserver.consumer.service.apiserver.IUserDatasetService;
import org.springframework.stereotype.Service;

/**
 * @Description: user_dataset
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Service
@DS("apiserver")
public class UserDatasetServiceImpl extends ServiceImpl<UserDatasetMapper, UserDataset> implements IUserDatasetService {

}
