package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;

import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class AbstracValidatorBase<T> {



    /**
     * 检查某个字段的值是否在列表中
     * @param fieldName 字段名
     * @param fieldValue 字段值
     * @param required  是否必填
     * @param validValues 字段的有效值列表
     * @throws ValidateException 校验失败异常
     * TODO：使用注解进行枚举值校验
     */
    protected void checkValue(String fieldName, String fieldValue, Boolean required, List<String> validValues) throws ValidateException {

        //必填且字段值为空，抛出异常
        if (required && StringUtils.isBlank(fieldValue)) {
            throw new ValidateException(fieldName + "字段不能为空");
        }

        //只要字段有值，并且validValues不为空，就需要校验
        if (!StringUtils.isBlank(fieldValue) && validValues != null && validValues.size() > 0) {
            if (!validValues.contains(fieldValue.toUpperCase())) {
                throw new ValidateException("不支持的" + fieldName  + "：" + fieldValue);
            }
        }

        //其他情况直接返回即可
    }


    /**
     * 校验请求报文中的acct（账期）字段
     * @param acct 账期字段。格式为yyyyMM、yyyyMMdd
     * @throws ValidateException 校验失败异常
     */
    protected void validateAcct(String acct) throws ValidateException {

        //validateAcct字段可以为空。在解析时，再替换成默认账期
        if (StringUtils.isBlank(acct)) {
            return;
        }

        //不能转为数字，说明包含字母
        try {
            Integer.parseInt(acct);
        } catch (NumberFormatException e) {
            throw new ValidateException("不支持的账期格式acct:" + acct);
        }

        //检验长度
        String strFormat;
        if(acct.length() == 6) {
            //yyyyMM
            strFormat = "yyyyMM";
        } else if (acct.length() == 8) {
            strFormat = "yyyyMMdd";
        } else {
            throw new ValidateException("不支持的账期格式acct:" + acct);
        }

        try {
            //此处只进行转换进行校验，不需要实际的值
            SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
            sdf.parse(acct);
        } catch (ParseException e) {
            throw new ValidateException("不支持的账期格式acct:" + acct);
        }
    }


    public abstract void validate(T t) throws ValidateException;
}
