package com.bonc.assetservice.apiserver.server.assetquery.parser;

import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.parser.parsers.*;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.AsyncAssetQueryReqVO_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.SyncAssetQueryReqVO_v1;
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
public abstract class ParserManager_v1 {
    @Autowired
    QueryFieldsParser_v1 queryFieldsParserV1;
    @Autowired
    ConditionsParser_v1 conditionsParserV1;
    @Autowired
    QueryTableParser_v1 queryTableParserV1;
    @Autowired
    SortFieldsParser_v1 sortFieldsParserV1;

    @Autowired
    CustomizedParser_v1 customizedParserV1;

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
    public static class SyncParseReq extends ParserManager_v1 implements IParserManager<SyncAssetQueryReqVO_v1, DataModel_v1>{

        @Override
        public DataModel_v1 parser(SyncAssetQueryReqVO_v1 req) throws ParseException {
            DataModel_v1 dataModelV1 = new DataModel_v1();

            simpleInfoParser.parse(req, dataModelV1);
            queryFieldsParserV1.parse(req.getQueryList(), dataModelV1);
            conditionsParserV1.parse(req.getConditionList(), dataModelV1);
            queryTableParserV1.parse(dataModelV1);
            sortFieldsParserV1.parse(req.getQueryList(), dataModelV1);
            customizedParserV1.parse(req.getAggregation(), dataModelV1);

            // 解析个性数据：分页数据
            pageInfoParser.parse(req.getPageInfo(), dataModelV1);

            return dataModelV1;
        }
    }


    /**
     * <p>
     *
     * </p>
     * 异步请求转换
     * @author zhaozesheng
     * @since 2023-01-17 11:11:13
     */
    @Component
    public static class AsyncParseReq extends ParserManager_v1 implements IParserManager<AsyncAssetQueryReqVO_v1, DataModel_v1>{

        @Override
        public DataModel_v1 parser(AsyncAssetQueryReqVO_v1 req) throws ParseException {
            DataModel_v1 dataModelV1 = new DataModel_v1();

            simpleInfoParser.parse(req, dataModelV1);
            queryFieldsParserV1.parse(req.getQueryList(), dataModelV1);
            conditionsParserV1.parse(req.getConditionList(), dataModelV1);
            queryTableParserV1.parse(dataModelV1);
            sortFieldsParserV1.parse(req.getQueryList(), dataModelV1);
            customizedParserV1.parse(req.getAggregation(), dataModelV1);

            return dataModelV1;
        }
    }
    

}
