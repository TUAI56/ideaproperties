package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v1;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaAssetDateService;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaAssetInfoService;
import com.bonc.assetservice.metadata.appmodel.AssetCodeAndAcct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/16
 * @Description: DataModel中查询字段列表queryFields的解析器
 */

@Slf4j
@Component
public class QueryFieldsParser_v1 {

    @Autowired
    IMetaAssetInfoService metaAssetInfoService;


    @Autowired
    IMetaAssetDateService metaAssetDateService;
    /**
     * 解析req，生成DataModel中的queryFields
     * @param queryList 查询的请求报文中的queryList
     * @param dataModelV1 目标内部数据结构
     * @throws ParseException
     * QueryField中的tableAlia，是跟queryTable相关联的逻辑，所以在QueryTableParser中处理
     */
    public void parse(List<QueryAssetVO_v1> queryList, DataModel_v1 dataModelV1) throws ParseException {

        if (queryList == null || queryList.size() == 0) {
            String msg = "异常：请求中的queryList为空";
            log.info(msg);
            throw new ParseException(msg);
        }

        //Step1：获取req的queryList中的所有标签id
        List<String> assetIds = getAssetIds(queryList);

        //Step2：获取所有标签的最新账期信息和colCode信息
        HashMap<Long, AssetCodeAndAcct> dbAssetinfos = metaAssetInfoService.getAssetCodeAndAcct(assetIds);

        //Step3：根据req中queryList生成queryField，并把最新最新账期信息和colCode补充全
        List<QueryField_v1> queryFieldV1s = getQueryFields(queryList, dbAssetinfos);

        //Step4：赋值给dataModel
        dataModelV1.setQueryFieldV1s(queryFieldV1s);
    }

    private List<String> getAssetIds(List<QueryAssetVO_v1> queryList) {
        //查询标签可能重复，所以使用set进行虑重
        Set<String> tmp = new HashSet<>();
        for (QueryAssetVO_v1 one : queryList) {
            tmp.add(one.getAssetId());
        }

        return new ArrayList<>(tmp);
    }

    private List<QueryField_v1> getQueryFields(List<QueryAssetVO_v1> queryList, HashMap<Long, AssetCodeAndAcct> dbAssetinfos) throws ParseException{

        List<QueryField_v1> ret = new ArrayList<>();
        for (QueryAssetVO_v1 one : queryList) {

            QueryField_v1 queryFieldV1 = getQueryField(dbAssetinfos, one);
            ret.add(queryFieldV1);
        }

        return ret;
    }

    @NotNull
    private QueryField_v1 getQueryField(HashMap<Long, AssetCodeAndAcct> dbAssetinfos, QueryAssetVO_v1 one) throws ParseException {
        AssetCodeAndAcct dbAsset = dbAssetinfos.get(Long.parseLong(one.getAssetId()));
        if (dbAsset == null) {
            String msg = "待查询标签不存在，assetId:" + one.getAssetId();
            log.error(msg);
            throw new ParseException(msg);
        }

        QueryField_v1 queryFieldV1 = new QueryField_v1();
        queryFieldV1.setAssetId(Long.parseLong(one.getAssetId()));
        queryFieldV1.setAssetCode(dbAsset.getAssetCode());
        queryFieldV1.setDisplayName(one.getDisplayName());
        queryFieldV1.setTableCode(dbAsset.getTableCode());
        queryFieldV1.setAggrType(one.getAggrType());

        //req的acct为空，则使用dbAsset中的默认账期，否则使用req中的账期
        if (StringUtils.isBlank(one.getAcct())) {
            queryFieldV1.setAcct(dbAsset.getDefaultAcct());
        } else {
            //判断req中的账期是否在meta_asset_date中
            List<String> acctList = metaAssetDateService.getAssetAcctList(dbAsset.getAssetId());
            if (null == acctList || !acctList.contains(one.getAcct())){
                throw new ParseException("待查询标签账期不存在，assetId："+ one.getAssetId());
            }
            queryFieldV1.setAcct(one.getAcct());
        }


           /*
            排序
         */
        if (!StringUtils.isBlank(one.getSortType())) {

            queryFieldV1.setSortType(one.getSortType());
            if ("DESC".equalsIgnoreCase(one.getSortType())) {
                queryFieldV1.setDesc(true);
            } else {
                queryFieldV1.setDesc(false);
            }
        }



        return queryFieldV1;
    }
}
