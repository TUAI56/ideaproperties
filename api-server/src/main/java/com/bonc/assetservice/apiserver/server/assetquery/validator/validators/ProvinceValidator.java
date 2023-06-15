package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;

import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.metadata.constant.ProvinceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 对请求req中的proviceId进行校验
 * </p>
 *
 * @author zhaozesheng
 * @since 2022-11-14 14:41:53
 */

@Slf4j
@Component
public class ProvinceValidator extends AbstracValidatorBase<String>{



    public void validate(String provId) throws ValidateException {
         //如果不包含在列表内 则抛出异常
        ProvinceEnum byCode = ProvinceEnum.getByCode(provId);
        if (null == byCode){
             throw new ValidateException("provId:" + provId +"，,不正确");
         }

    }
}
