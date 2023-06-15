package com.bonc.assetservice.apiserver.server.assetquery.validator.exception;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description: 校验失败异常
 */

public class ValidateException extends Exception{

    private static final long serialVersionUID = 8247002973165794042L;

    public ValidateException(String msg) {
        super(msg);
    }

    public ValidateException(String msg, Throwable e) {
        super(msg, e);
    }
}
