package com.bonc.assetservice.apiserver.server.service.apiserver.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.data.entity.ReqInfo;
import com.bonc.assetservice.apiserver.data.mapper.ReqInfoMapper;
import com.bonc.assetservice.apiserver.server.service.apiserver.IReqInfoService;
import org.springframework.stereotype.Service;

@Service
@DS("apiserver")
public class ReqInfoServiceImpl extends ServiceImpl<ReqInfoMapper, ReqInfo> implements IReqInfoService {
}
