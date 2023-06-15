package com.bonc.module.service;

import com.bonc.framework.apilog.core.service.ApiErrorLogFrameworkService;
import com.bonc.framework.apilog.core.service.dto.ApiErrorLogCreateReqDTO;
import org.springframework.stereotype.Service;

import javax.validation.Valid;


@Service
public class ApiErrorLogFrameworkServiceImpl  implements ApiErrorLogFrameworkService {

    @Override
    public void createApiErrorLogAsync(@Valid ApiErrorLogCreateReqDTO createDTO) {

    }
}
