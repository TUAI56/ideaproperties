package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;

import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.common.PageInfo;
import org.springframework.stereotype.Component;

/**
 * 分页参数验证器
 * @ClassName PageInfoValidator
 * @Author 李维帅
 * @Date 2022/7/7 16:51
 * @Version 1.0
 **/
@Component
public class PageInfoValidator extends AbstracValidatorBase<PageInfo>{
    public void validate(PageInfo pageInfo) throws ValidateException {
            //分页参数验证
            if (pageInfo== null) {
                throw new ValidateException("pageInfo不能为空");
            }
            if (pageInfo.getLimit() == null) {
                throw new ValidateException("limit字段不能为空");
            }
            if (pageInfo.getOffset() == null) {
                throw new ValidateException("offset字段不能为空");
            }

    }
}
