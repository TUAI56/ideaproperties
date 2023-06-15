package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.bonc.assetservice.apiserver.data.model.DataSetCodeAndColum;
import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v2;
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
 * @Description: DataModel中查询字段列表queryFields的解析器
 */

@Slf4j
@Component
public class QueryFieldsParser_v2 {

    @Autowired
    IMetaAssetInfoService metaAssetInfoService;


    @Autowired
    IMetaAssetDateService metaAssetDateService;


    @Autowired
    IUserDatasetService userDatasetService;


    /**
     * 解析req，生成DataModel中的queryFields
     *
     * @param queryList 查询的请求报文中的queryList
     * @param dataModel 目标内部数据结构
     * @throws ParseException QueryField中的tableAlia，是跟queryTable相关联的逻辑，所以在QueryTableParser中处理
     */

    public void parse(List<QueryAssetVO_v2> queryList, DataModel_v2 dataModel) throws ParseException {

        if (queryList == null || queryList.size() == 0) {
            String msg = "异常：请求中的queryList为空";
            log.info(msg);
            throw new ParseException(msg);
        }


        //Step1：获取req的queryList中的所有标签id和自定义数据集ID
        Map<String, List<String>> assetAndDataSetIds = getAssetAndDataSetIds(queryList);
        List<String> assetIds = assetAndDataSetIds.get("asset");
        List<String> datasetIds = assetAndDataSetIds.get("dataset");

        //setp2:获取自定义数据集的查询表信息 关联字段信息
        HashMap<String, DataSetCodeAndColum> dataSetCodeAndColum = userDatasetService.getDataSetCodeAndColum(datasetIds);
        //处理dataSetCodeAndColum  将统一自定义数据集的不同查询字段进行封装
        createDataSetCodeAndColums(queryList, dataSetCodeAndColum);

        //Step2：获取所有标签的最新账期信息和colCode信息
        HashMap<Long, AssetCodeAndAcct> dbAssetinfos = metaAssetInfoService.getAssetCodeAndAcct(assetIds);

        //Step3：根据req中queryList生成queryField，并把最新最新账期信息和colCode补充全
        List<QueryField_v2> queryFieldsAsset = null;
        if (null != dbAssetinfos ){
            queryFieldsAsset  = getQueryFieldsAsset(queryList, dbAssetinfos);
        }
        List<QueryField_v2> queryFieldsDataSet = null;
        if (null != dataSetCodeAndColum) {
            queryFieldsDataSet = getQueryFieldsDataSet(queryList, dataSetCodeAndColum);

        }

        List<QueryField_v2> res = new ArrayList<>();
       if (null != queryFieldsAsset) res.addAll(queryFieldsAsset);
        if (null != queryFieldsDataSet) res.addAll(queryFieldsDataSet);
        //Step4：赋值给dataModel
        dataModel.setQueryFields(res);
    }


    /**
     * <p>
     *
     * </p>
     *
     * @param queryField_v2_0 入参queryFileds
     * @param map             根据ID查询出的join字段和表明
     * @author zhaozesheng
     * @since 2023-01-04 15:31:49
     */
    private void createDataSetCodeAndColums(List<QueryAssetVO_v2> queryField_v2_0, HashMap<String, DataSetCodeAndColum> map) {

        if (null == queryField_v2_0 || queryField_v2_0.isEmpty()) return;

        for (QueryAssetVO_v2 queryAssetVO_v2_ : queryField_v2_0) {
            //如果是数据集则进行判断
            if ("dataset".equals(queryAssetVO_v2_.getQueryType())) {
                if (null != map.get(queryAssetVO_v2_.getDataset().getDatasetId())) {
                    //获取dataset
                    DataSetCodeAndColum dataSetCodeAndColum = map.get(queryAssetVO_v2_.getDataset().getDatasetId());
                    List<String> colum = dataSetCodeAndColum.getColum();
                    if (null == colum || colum.isEmpty()) {
                        colum = new ArrayList<>();
                    }
                    colum.add(queryAssetVO_v2_.getDataset().getCode());
                }
            }
        }
    }


    private Map<String, List<String>> getAssetAndDataSetIds(List<QueryAssetVO_v2> queryList) {
        //定义Map接收结果
        Map<String, List<String>> map = new HashMap<>();

        //定义标签ID的集合
        Set<String> assetsIds = new HashSet<>();

        //定义自定义数据集的ID
        Set<String> dataSetIds = new HashSet<>();


        for (QueryAssetVO_v2 one : queryList) {
            if ("asset".equals(one.getQueryType())) {
                assetsIds.add(one.getAsset().getAssetId());
            }

            if ("dataset".equals(one.getQueryType())) {
                dataSetIds.add(one.getDataset().getDatasetId());
            }

        }

        map.put("asset", new ArrayList<>(assetsIds));

        map.put("dataset", new ArrayList<>(dataSetIds));

        return map;
    }


    private List<QueryField_v2> getQueryFieldsAsset(List<QueryAssetVO_v2> queryList, HashMap<Long, AssetCodeAndAcct> dbAssetinfos) throws ParseException {

        List<QueryField_v2> ret = new ArrayList<>();
        for (QueryAssetVO_v2 one : queryList) {
            if ("asset".equals(one.getQueryType())){
                QueryField_v2 queryField = getQueryFieldAsset(dbAssetinfos, one);
                ret.add(queryField);
            }
        }

        return ret;
    }

    private List<QueryField_v2> getQueryFieldsDataSet(List<QueryAssetVO_v2> queryList, HashMap<String, DataSetCodeAndColum> dataSetCodeAndColum) throws ParseException {

        List<QueryField_v2> ret = new ArrayList<>();
        for (QueryAssetVO_v2 one : queryList) {
            if ("dataset".equals(one.getQueryType())){
                QueryField_v2 queryField = getQueryFieldDataSet(dataSetCodeAndColum, one);
                ret.add(queryField);
            }
        }

        return ret;
    }


    @NotNull
    private QueryField_v2 getQueryFieldAsset(HashMap<Long, AssetCodeAndAcct> dbAssetinfos, QueryAssetVO_v2 one) throws ParseException {
        AssetCodeAndAcct dbAsset = dbAssetinfos.get(Long.parseLong(one.getAsset().getAssetId()));
        if (dbAsset == null) {
            String msg = "待查询标签不存在，assetId:" + one.getAsset().getAssetId();
            log.error(msg);
            throw new ParseException(msg);
        }

        QueryField_v2 queryField = new QueryField_v2();
        QueryField_v2.Asset asset = new QueryField_v2.Asset();
        asset.setAssetId(Long.parseLong(one.getAsset().getAssetId()));
        asset.setAssetCode(dbAsset.getAssetCode());
        queryField.setDisplayName(one.getDisplayName());
        queryField.setTableCode(dbAsset.getTableCode());
        queryField.setAggrType(one.getAggrType());


        //req的acct为空，则使用dbAsset中的默认账期，否则使用req中的账期
        if (StringUtils.isBlank(one.getAsset().getAcct())) {
            asset.setAcct(dbAsset.getDefaultAcct());
        } else {
            //判断req中的账期是否在meta_asset_date中
            List<String> acctList = metaAssetDateService.getAssetAcctList(dbAsset.getAssetId());
            if (null == acctList || !acctList.contains(one.getAsset().getAcct())) {
                throw new ParseException("待查询标签账期不存在，assetId：" + one.getAsset().getAcct());
            }
            asset.setAcct(one.getAsset().getAcct());
        }
        queryField.setAsset(asset);
        queryField.setQueryType("asset");


        /*
            排序
         */
        if (!StringUtils.isBlank(one.getSortType())) {

            queryField.setSortType(one.getSortType());
            if ("DESC".equalsIgnoreCase(one.getSortType())) {
                queryField.setDesc(true);
            } else {
                queryField.setDesc(false);
            }
        }

        return queryField;
    }


    @NotNull
    private QueryField_v2 getQueryFieldDataSet(HashMap<String, DataSetCodeAndColum> dataSetCodeAndColumMap, QueryAssetVO_v2 one) throws ParseException {
        DataSetCodeAndColum dataSetCodeAndColum = dataSetCodeAndColumMap.get(one.getDataset().getDatasetId());

        if (dataSetCodeAndColum == null) {
            String msg = "待查询自定义数据集不存在，dataSetId:" + one.getDataset().getDatasetId();
            log.error(msg);
            throw new ParseException(msg);
        }

        QueryField_v2 queryField = new QueryField_v2();
        QueryField_v2.DataSet dataSet = new QueryField_v2.DataSet();

        dataSet.setDatasetId(one.getDataset().getDatasetId());
        dataSet.setCode(one.getDataset().getCode());
        queryField.setDisplayName(one.getDisplayName());
        queryField.setTableCode(dataSetCodeAndColum.getTableCode());
        queryField.setAggrType(one.getAggrType());

        queryField.setDataSet(dataSet);
        queryField.setQueryType("dataset");
        return queryField;
    }
}
