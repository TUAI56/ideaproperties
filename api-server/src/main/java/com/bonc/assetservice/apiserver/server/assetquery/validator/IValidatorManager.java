package com.bonc.assetservice.apiserver.server.assetquery.validator;


import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.AbstractAssetQueryBaseReqVO;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description: 校验管理器的接口
 */

public interface IValidatorManager<T> {

    /**
     * 校验同步接口
     * @param req 同步接口入参
     * @throws ValidateException author
     */
    void validate(T req) throws ValidateException;


}
