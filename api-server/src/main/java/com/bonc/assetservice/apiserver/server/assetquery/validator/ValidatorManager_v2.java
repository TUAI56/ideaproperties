package com.bonc.assetservice.apiserver.server.assetquery.validator;

import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.validator.validators.*;
import com.bonc.assetservice.apiserver.server.assetquery.vo.*;
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
public class ValidatorManager_v2 extends ValidatorManager_v1 {

    @Autowired
    ProvinceValidator provinceValidator;

    @Autowired
    PageInfoValidator pageInfoValidator;

    @Resource
    FtpValidator ftpValidator;


    @Autowired
    QueryListValidator_v2 queryListValidator_v2_;
    @Autowired
    ConditionListValidator_v2 conditionListValidator_v2_;



    /**
     * <p>
     * 2.0版本同步请求参数校验
     * </p>
     *
     * @author zhaozesheng
     * @since 2023-01-17 09:03:19
     */
    @Component
    public static class ValidateSync2_ extends ValidatorManager_v2 implements IValidatorManager<SyncAssetQueryReqVO_v2>{


        @Override
        public void validate(SyncAssetQueryReqVO_v2 req) throws ValidateException {
            provinceValidator.validate(req.getProvId());
            queryListValidator_v2_.validate(req.getQueryList());
            conditionListValidator_v2_.validate(req.getConditionList());
            // 分页校验
            pageInfoValidator.validate(req.getPageInfo());

        }
    }



    /**
     * <p>
     * 2.0版本异步请求参数校验
     * </p>
     *
     * @author zhaozesheng
     * @since 2023-01-17 09:04:02
     */
    @Component
    public static class ValidateAsync2_ extends ValidatorManager_v2 implements IValidatorManager<AsyncAssetQueryReqVO_v2>{
        @Override
        public void validate(AsyncAssetQueryReqVO_v2 req) throws ValidateException {
            provinceValidator.validate(req.getProvId());
            queryListValidator_v2_.validate(req.getQueryList());
            conditionListValidator_v2_.validate(req.getConditionList());
            // 分页校验
            ftpValidator.validate(req.getDestFtp());
        }
    }



}
