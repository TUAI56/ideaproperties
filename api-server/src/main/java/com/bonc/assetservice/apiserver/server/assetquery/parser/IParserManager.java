package com.bonc.assetservice.apiserver.server.assetquery.parser;


import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/15
 * @Description: 解析管理器的接口
 */


public interface IParserManager<T,R> {


    /**
     * <p>
     * 转换
     * </p>
     * @param t 接口入参
     * @return R 不同版本的dataModel
     * @author zhaozesheng
     * @since 2023-01-17 11:01:58
     */
    R parser(T t) throws ParseException;

}
