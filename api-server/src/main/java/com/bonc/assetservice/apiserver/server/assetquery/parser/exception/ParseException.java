package com.bonc.assetservice.apiserver.server.assetquery.parser.exception;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/15
 * @Description: 解析失败异常
 */

public class ParseException extends Exception{


    private static final long serialVersionUID = -2597334953603533977L;

    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(String msg, Throwable e) {
        super(msg, e);
    }
}
