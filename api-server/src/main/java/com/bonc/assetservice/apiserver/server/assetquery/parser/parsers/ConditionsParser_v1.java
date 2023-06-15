package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.Condition_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.ConditionAssetVO_v1;
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
 * @Description: DataModel中查询条件conditions的解析器
 */

@Slf4j
@Component
public class ConditionsParser_v1 {
    @Autowired
    IMetaAssetInfoService metaAssetInfoService;

    @Autowired
    IMetaAssetDateService metaAssetDateService;

    private final  static Integer CONDITION_NUM = 10;

    public void parse(List<ConditionAssetVO_v1> reqCondition, DataModel_v1 dataModelV1) throws ParseException {

        if (reqCondition == null || reqCondition.size() == 0) {
            log.info("请求中的conditionList为空，无需解析");
            return;
        }

        //Step1：获取req的conditionList中的所有标签id
        Set<String> assetIdsSet = new HashSet<>();
        List<String> assetIdsList = new ArrayList<>();
        getAssetIdsRecursive(reqCondition, assetIdsSet,assetIdsList);
//        if (assetIdsList.size() > CONDITION_NUM){
//            throw new ParseException("ConditionList中标签数量大于" + CONDITION_NUM);
//        }

        //Step2：获取所有标签的最新账期信息和assetCode信息
        List<String> assetIds = new ArrayList<>(assetIdsSet);
        HashMap<Long, AssetCodeAndAcct> dbAssetinfos = metaAssetInfoService.getAssetCodeAndAcct(assetIds);

        //Step3：根据req中queryList生成queryField，并把最新最新账期信息和assetCode补充全
        List<Condition_v1> conditionV1s = getQueryFieldsRecursive(reqCondition, dbAssetinfos);

        //Step4：赋值给dataModel
        dataModelV1.setConditionV1s(conditionV1s);
    }

    private void getAssetIdsRecursive(List<ConditionAssetVO_v1> conditionList, Set<String> assetIdsSet, List<String> assetIdsList) {
        for (ConditionAssetVO_v1 one : conditionList) {
            if (!StringUtils.isBlank(one.getAssetId())) {
                assetIdsSet.add(one.getAssetId());
                assetIdsList.add(one.getAssetId());
            }

            //递归获取subConditionList中的标签id
            if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                getAssetIdsRecursive(one.getSubConditionList(), assetIdsSet,assetIdsList);
            }
        }
    }

    private List<Condition_v1> getQueryFieldsRecursive(List<ConditionAssetVO_v1> conditionList,
                                                       HashMap<Long, AssetCodeAndAcct> dbAssetinfos) throws ParseException{

        List<Condition_v1> ret = new ArrayList<>();
        for (ConditionAssetVO_v1 one : conditionList) {
            Condition_v1 tmp = new Condition_v1();

            //设置QueryField
            if (!StringUtils.isBlank(one.getAssetId())) {
                QueryField_v1 queryFieldV1 = getQueryField(dbAssetinfos, one);
                tmp.setQueryFieldV1(queryFieldV1);
            }

            //设置比较符号字符串
            if (!StringUtils.isBlank(one.getOperator())) {
                tmp.setCompareOperator(Enums.CompareOperator.markValueOf(one.getOperator()));
            }

            //设置操作数
            if (!StringUtils.isBlank(one.getParams())) {
                tmp.setOperand(one.getParams());
            }

            //设置逻辑操作符
            if (!StringUtils.isBlank(one.getLogicalOperator())) {
                tmp.setLogicalOp(Enums.LogicalOperator.valueOf(one.getLogicalOperator().toUpperCase()));
            }

            //递归生成子查询条件
            if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                List<Condition_v1> subConditionV1List = getQueryFieldsRecursive(one.getSubConditionList(), dbAssetinfos);
                tmp.setSubConditionV1s(subConditionV1List);
            }

            ret.add(tmp);
        }

        return ret;
    }

    @NotNull
    private QueryField_v1 getQueryField(HashMap<Long, AssetCodeAndAcct> dbAssetinfos, ConditionAssetVO_v1 one) throws ParseException {
        QueryField_v1 queryFieldV1 = new QueryField_v1();
        AssetCodeAndAcct dbAsset = dbAssetinfos.get(Long.parseLong(one.getAssetId()));
        if (dbAsset == null) {
            String msg = "待查询标签不存在，assetId:" + one.getAssetId();
            log.error(msg);
            throw new ParseException(msg);
        }
        queryFieldV1.setAssetId(dbAsset.getAssetId());
        queryFieldV1.setAssetCode(dbAsset.getAssetCode());
        queryFieldV1.setTableCode(dbAsset.getTableCode());
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
        return queryFieldV1;
    }
}
