package com.bonc.assetservice.apiserver.server.assetquery.parser;

import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.parser.parsers.*;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.vo.AsyncAssetQueryReqVO_v2;
import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryReqVO_v2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/15
 * @Description: 解析管理器的实现。
 * 从DataModel中各数据项的维度，进行解析器的拆分。
 */

@Slf4j
@Component
public class ParserManager_v2 {
    @Autowired
    QueryFieldsParser_v2 queryFieldsParser;
    @Autowired
    ConditionsParser_v2 conditionsParser;
    @Autowired
    QueryTableParser_v2 queryTableParser;
    @Autowired
    SortFieldsParser_v2 sortFieldsParser;

    @Autowired
    CustomizedParser_v2 customizedParser;


    @Autowired
    PageInfoParser pageInfoParser;

    @Autowired
    SimpleInfoParser simpleInfoParser;

    /**
     * <p>
     *
     * </p>
     * 同步请求转换
     * @author zhaozesheng
     * @since 2023-01-17 11:11:13
     */
    @Component
    public static class SyncParseReq extends ParserManager_v2 implements IParserManager<SyncAssetQueryReqVO_v2, DataModel_v2>{

        @Override
        public DataModel_v2 parser(SyncAssetQueryReqVO_v2 req) throws ParseException {
            DataModel_v2 dataModel = new DataModel_v2();

            simpleInfoParser.parse(req, dataModel);
            queryFieldsParser.parse(req.getQueryList(),dataModel);
            conditionsParser.parse(req.getConditionList(),dataModel);
            queryTableParser.parse(dataModel);
            sortFieldsParser.parse(req.getQueryList(),dataModel);
            customizedParser.parse(req.getAggregation(),dataModel);

            // 解析个性数据：分页数据
            pageInfoParser.parse(req.getPageInfo(), dataModel);

            return dataModel;
        }
    }


    /**
     * <p>
     *
     * </p>
     * 同步请求转换
     * @author zhaozesheng
     * @since 2023-01-17 11:11:13
     */
    @Component
    public static class AsyncParseReq extends ParserManager_v2 implements IParserManager<AsyncAssetQueryReqVO_v2, DataModel_v2>{

        @Override
        public DataModel_v2 parser(AsyncAssetQueryReqVO_v2 req) throws ParseException {
            DataModel_v2 dataModel = new DataModel_v2();

            simpleInfoParser.parse(req, dataModel);
            queryFieldsParser.parse(req.getQueryList(),dataModel);
            conditionsParser.parse(req.getConditionList(),dataModel);
            queryTableParser.parse(dataModel);
            sortFieldsParser.parse(req.getQueryList(),dataModel);
            customizedParser.parse(req.getAggregation(),dataModel);

            return dataModel;
        }
    }


}
