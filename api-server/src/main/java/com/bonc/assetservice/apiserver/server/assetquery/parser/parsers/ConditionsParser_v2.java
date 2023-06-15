package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.bonc.assetservice.apiserver.data.model.DataSetCodeAndColum;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.*;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.ConditionAssetVO_v2;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetService;
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
public class ConditionsParser_v2 {
    @Autowired
    IMetaAssetInfoService metaAssetInfoService;

    @Autowired
    IMetaAssetDateService metaAssetDateService;

    @Autowired
    IUserDatasetService userDatasetService;

    private final static Integer CONDITION_NUM = 10;

    public void parse(List<ConditionAssetVO_v2> reqCondition, DataModel_v2 dataModel) throws ParseException {

        if (reqCondition == null || reqCondition.size() == 0) {
            log.info("请求中的conditionList为空，无需解析");
            return;
        }

        //Step1：获取req的conditionList中的所有标签id
        Set<String> assetIdsSet = new HashSet<>();
        List<String> assetIdsList = new ArrayList<>();
        getAssetIdsRecursive(reqCondition, assetIdsSet, assetIdsList);
/*        if (assetIdsList.size() > CONDITION_NUM) {
            throw new ParseException("ConditionList中标签数量大于" + CONDITION_NUM);
        }*/

        Set<String> dataSetIdsSet = new HashSet<>();
        List<String> dataSetIdsList = new ArrayList<>();
        getDatasetIdsRecursive(reqCondition, dataSetIdsSet, dataSetIdsList);
        if (dataSetIdsList.size() > CONDITION_NUM) {
            throw new ParseException("ConditionList中自定义标签数量大于" + CONDITION_NUM);
        }


        //Step2：获取所有标签的最新账期信息和assetCode信息
        List<String> assetIds = new ArrayList<>(assetIdsSet);
        HashMap<Long, AssetCodeAndAcct> dbAssetinfos = metaAssetInfoService.getAssetCodeAndAcct(assetIds);


        HashMap<String, DataSetCodeAndColum> dataSetCodeAndColum = userDatasetService.getDataSetCodeAndColum(new ArrayList<>(dataSetIdsSet));


        //Step3：根据req中queryList生成queryField，并把最新最新账期信息和assetCode补充全
        List<Condition_v2> conditionsAsset = getQueryFieldsRecursive4Asset(reqCondition, dbAssetinfos, dataSetCodeAndColum);


       // List<Condition_v2> conditionsDataset = getQueryFieldsRecursive4DataSet(reqCondition, dataSetCodeAndColum);


        //Step4：赋值给dataModel
        dataModel.setConditions(conditionsAsset);
    }

    private void getAssetIdsRecursive(List<ConditionAssetVO_v2> conditionList, Set<String> assetIdsSet, List<String> assetIdsList) {
        for (ConditionAssetVO_v2 one : conditionList) {
            if (!"asset".equals(one.getConditionType()) && !"subConditionList".equals(one.getConditionType())) continue;

            //如果是标签
            if ("asset".equals(one.getConditionType())) {
                if (!StringUtils.isBlank(one.getAsset().getAssetId())) {
                    assetIdsSet.add(one.getAsset().getAssetId());
                    assetIdsList.add(one.getAsset().getAssetId());
                }
            }
            //递归获取subConditionList中的标签id
            if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                getAssetIdsRecursive(one.getSubConditionList(), assetIdsSet, assetIdsList);
            }
        }
    }


    private void getDatasetIdsRecursive(List<ConditionAssetVO_v2> conditionList, Set<String> dataSetIdsSet, List<String> dataSetIdsList) {
        for (ConditionAssetVO_v2 one : conditionList) {
            if (!"dataset".equals(one.getConditionType()) &&  !"subConditionList".equals(one.getConditionType()))
                continue;
            if ("dataset".equals(one.getConditionType())) {
                if (!StringUtils.isBlank(one.getDataset().getDatasetId())) {
                    dataSetIdsSet.add(one.getDataset().getDatasetId());
                    dataSetIdsList.add(one.getDataset().getDatasetId());
                }
            }
            //递归获取subConditionList中的标签id
            if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                getDatasetIdsRecursive(one.getSubConditionList(), dataSetIdsSet, dataSetIdsList);
            }
        }
    }


    private List<Condition_v2> getQueryFieldsRecursive4Asset(List<ConditionAssetVO_v2> conditionList,
                                                             HashMap<Long, AssetCodeAndAcct> dbAssetinfos,
                                                             HashMap<String, DataSetCodeAndColum> dataSetCodeAndColum) throws ParseException {

        List<Condition_v2> ret = new ArrayList<>();
        for (ConditionAssetVO_v2 one : conditionList) {
            //如果是sub
            Condition_v2 tmp = new Condition_v2();
            if ("asset".equals(one.getConditionType())) {
                //设置QueryField
                if (!StringUtils.isBlank(one.getAsset().getAssetId())) {
                    QueryField_v2 queryField = getQueryField(dbAssetinfos, one);
                    tmp.setQueryField(queryField);
                }
                //设置比较符号字符串
                if (!StringUtils.isBlank(one.getAsset().getOperator())) {
                    tmp.setCompareOperator(Enums.CompareOperator.markValueOf(one.getAsset().getOperator()));
                }
                //设置操作数
                if (!StringUtils.isBlank(one.getAsset().getParams())) {
                    tmp.setOperand(one.getAsset().getParams());
                }
                //设置逻辑操作符
                if (!StringUtils.isBlank(one.getLogicalOperator())) {
                    tmp.setLogicalOp(Enums.LogicalOperator.valueOf(one.getLogicalOperator().toUpperCase()));
                }
                //递归生成子查询条件
                if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                    List<Condition_v2> subConditionList = getQueryFieldsRecursive4Asset(one.getSubConditionList(), dbAssetinfos,dataSetCodeAndColum);
                    tmp.setSubConditions(subConditionList);
                }
                tmp.setConditionType("asset");
            }else if ("dataset".equals(one.getConditionType())){
                tmp.setConditionType("dataset");
                //设置QueryField
                if (!StringUtils.isBlank(one.getDataset().getDatasetId())) {
                    QueryField_v2 queryField = getQueryField4DataSet(dataSetCodeAndColum, one);
                    tmp.setQueryField(queryField);
                }

                //设置比较符号字符串
//                if (!StringUtils.isBlank(one.getDataset().getLogicalOperator())) {
//                    tmp.setCompareOperator(Enums.CompareOperator.markValueOf(one.getDataset().getLogicalOperator()));
//                }

                //设置操作数
//            if (!StringUtils.isBlank(one.getAsset().getParams())) {
//                tmp.setOperand(one.getAsset().getParams());
//            }

                //设置逻辑操作符
                if (!StringUtils.isBlank(one.getLogicalOperator())) {
                    tmp.setLogicalOp(Enums.LogicalOperator.valueOf(one.getLogicalOperator().toUpperCase()));
                }

            }else if ("subConditionList".equals(one.getConditionType())){
                //递归生成子查询条件
                if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {

                    tmp.setConditionType("subConditionList");
                    tmp.setLogicalOp(Enums.LogicalOperator.valueOf(one.getLogicalOperator().toUpperCase()));

                    List<Condition_v2> subConditionList = getQueryFieldsRecursive4Asset(one.getSubConditionList(), dbAssetinfos,dataSetCodeAndColum);
                    tmp.setSubConditions(subConditionList);
                }
            }

            ret.add(tmp);
        }

        return ret;
    }


    private List<Condition_v2> getQueryFieldsRecursive4DataSet(List<ConditionAssetVO_v2> conditionList,
                                                               HashMap<String, DataSetCodeAndColum> columHashMap) throws ParseException {

        List<Condition_v2> ret = new ArrayList<>();
        for (ConditionAssetVO_v2 one : conditionList) {

            if (!"dataset".equals(one.getConditionType())) {
                continue;
            }
            Condition_v2 tmp = new Condition_v2();
            tmp.setConditionType("dataset");
            //设置QueryField
            if (!StringUtils.isBlank(one.getDataset().getDatasetId())) {
                QueryField_v2 queryField = getQueryField4DataSet(columHashMap, one);
                tmp.setQueryField(queryField);
            }

            //设置比较符号字符串
            if (!StringUtils.isBlank(one.getDataset().getLogicalOperator())) {
                tmp.setCompareOperator(Enums.CompareOperator.markValueOf(one.getDataset().getLogicalOperator()));
            }

            //设置操作数
//            if (!StringUtils.isBlank(one.getAsset().getParams())) {
//                tmp.setOperand(one.getAsset().getParams());
//            }

            //设置逻辑操作符
            if (!StringUtils.isBlank(one.getLogicalOperator())) {
                tmp.setLogicalOp(Enums.LogicalOperator.valueOf(one.getLogicalOperator().toUpperCase()));
            }

            //递归生成子查询条件
            if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                List<Condition_v2> subConditionList = getQueryFieldsRecursive4DataSet(one.getSubConditionList(), columHashMap);
                tmp.setSubConditions(subConditionList);
            }

            ret.add(tmp);
        }

        return ret;
    }


    @NotNull
    private QueryField_v2 getQueryField(HashMap<Long, AssetCodeAndAcct> dbAssetinfos, ConditionAssetVO_v2 one) throws ParseException {
        QueryField_v2 queryField = new QueryField_v2();
        queryField.setQueryType("asset");
        AssetCodeAndAcct dbAsset = dbAssetinfos.get(Long.parseLong(one.getAsset().getAssetId()));
        if (dbAsset == null) {
            String msg = "待查询标签不存在，assetId:" + one.getAsset().getAssetId();
            log.error(msg);
            throw new ParseException(msg);
        }
        QueryField_v2.Asset asset = new QueryField_v2.Asset();
        asset.setAssetId(dbAsset.getAssetId());
        asset.setAssetCode(dbAsset.getAssetCode());
        queryField.setTableCode(dbAsset.getTableCode());
        //req的acct为空，则使用dbAsset中的默认账期，否则使用req中的账期
        if (StringUtils.isBlank(one.getAsset().getAcct())) {
            asset.setAcct(dbAsset.getDefaultAcct());
        } else {
            //判断req中的账期是否在meta_asset_date中
            List<String> acctList = metaAssetDateService.getAssetAcctList(dbAsset.getAssetId());
            if (null == acctList || !acctList.contains(one.getAsset().getAcct())) {
                throw new ParseException("待查询标签账期不存在，assetId：" + one.getAsset().getAssetId());
            }
            asset.setAcct(one.getAsset().getAcct());
        }
        queryField.setAsset(asset);
        return queryField;
    }


    @NotNull
    private QueryField_v2 getQueryField4DataSet(HashMap<String, DataSetCodeAndColum> codeAndColumHashMap, ConditionAssetVO_v2 one) throws ParseException {
        QueryField_v2 queryField = new QueryField_v2();
        queryField.setQueryType("dataset");
        DataSetCodeAndColum dbdataSet = codeAndColumHashMap.get(one.getDataset().getDatasetId());
        if (dbdataSet == null) {
            String msg = "待查询自定义标签不存在，datasetId:" + one.getDataset().getDatasetId();
            log.error(msg);
            throw new ParseException(msg);
        }
        QueryField_v2.DataSet dataSet = new QueryField_v2.DataSet();


        dataSet.setDatasetId(dbdataSet.getDataSetId());
        List<ConditionAssetVO_v2.Dataset.Column> columns = one.getDataset().getColumns();
        List<QueryField_v2.Column> columnList = new ArrayList<>();

        for (ConditionAssetVO_v2.Dataset.Column column : columns) {
            QueryField_v2.Column col = new QueryField_v2.Column();
            col.setCode(column.getCode());
            col.setOperator(Enums.CompareOperator.markValueOf(column.getOperator()));
            col.setParams(column.getParams());
            columnList.add(col);
        }
        dataSet.setColumns(columnList);

        queryField.setTableCode(dbdataSet.getTableCode());
        queryField.setDataSet(dataSet);
        return queryField;
    }


}
