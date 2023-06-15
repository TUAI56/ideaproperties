package com.bonc.module.service;

import com.bonc.framework.apilog.core.service.ApiAccessLogFrameworkService;
import com.bonc.framework.apilog.core.service.dto.ApiAccessLogCreateReqDTO;
import org.springframework.stereotype.Service;

import javax.validation.Valid;


@Service
public class ApiAccessLogFrameworkServiceImpl implements ApiAccessLogFrameworkService {

    @Override
    public void createApiAccessLogAsync(@Valid ApiAccessLogCreateReqDTO createDTO) {

    }


}
