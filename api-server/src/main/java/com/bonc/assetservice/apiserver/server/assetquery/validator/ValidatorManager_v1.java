package com.bonc.assetservice.apiserver.server.assetquery.validator;

import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.validator.validators.*;
import com.bonc.assetservice.apiserver.server.assetquery.vo.AsyncAssetQueryReqVO_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryReqVO_v1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description: 校验管理器的实现
 * TODO
 *  1.使用注解进行枚举值校验
 */

@Slf4j
@Component
public class ValidatorManager_v1 {

    @Autowired
    ProvinceValidator provinceValidator;
    @Autowired
    QueryListValidator_v1 queryListValidatorV1;
    @Autowired
    ConditionListValidator_v1 conditionListValidatorV1;
    @Autowired
    PageInfoValidator pageInfoValidator;

    @Resource
    FtpValidator ftpValidator;






    @Component
    public static class ValidateSync extends ValidatorManager_v1 implements IValidatorManager<SyncAssetQueryReqVO_v1>{

        @Override
        public void validate(SyncAssetQueryReqVO_v1 req) throws ValidateException {
            provinceValidator.validate(req.getProvId());
            queryListValidatorV1.validate(req.getQueryList());
            conditionListValidatorV1.validate(req.getConditionList());
            // 分页校验
            pageInfoValidator.validate(req.getPageInfo());

        }
    }



    @Component
    public static class ValidateAsync extends ValidatorManager_v1 implements IValidatorManager<AsyncAssetQueryReqVO_v1>{

        @Override
        public void validate(AsyncAssetQueryReqVO_v1 req) throws ValidateException {
            provinceValidator.validate(req.getProvId());
            queryListValidatorV1.validate(req.getQueryList());
            conditionListValidatorV1.validate(req.getConditionList());
            // 分页校验
            ftpValidator.validate(req.getDestFtp());

        }
    }



}
