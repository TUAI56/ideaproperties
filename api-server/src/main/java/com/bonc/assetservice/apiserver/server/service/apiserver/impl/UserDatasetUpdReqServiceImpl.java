package com.bonc.assetservice.apiserver.server.service.apiserver.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.data.entity.UserDatasetUpdReq;
import com.bonc.assetservice.apiserver.data.mapper.UserDatasetUpdReqMapper;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetUpdReqService;
import org.springframework.stereotype.Service;

/**
 * @Description: user_dataset_upd_req
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Service
@DS("apiserver")
public class UserDatasetUpdReqServiceImpl extends ServiceImpl<UserDatasetUpdReqMapper, UserDatasetUpdReq> implements IUserDatasetUpdReqService {

}
