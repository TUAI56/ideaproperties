package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bonc.assetservice.apiserver.data.model.DataSetCodeAndColum;
import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.Condition_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModelBase;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetService;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaAssetInfoService;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaEntityTableService;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaModelTableService;
import com.bonc.assetservice.metadata.appmodel.ModelAndEntityTableInfo;
import com.bonc.assetservice.metadata.constant.MetaDataConst;
import com.bonc.assetservice.metadata.entity.MetaEntityTable;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/16
 * @Description: 根据queryList中的标签列表，获取要查询的表信息
 */

@Slf4j
@Component
public class QueryTableParser_v2 {

    @Autowired
    IMetaAssetInfoService metaAssetInfoService;

    @Autowired
    IMetaEntityTableService metaEntityTableService;

    @Autowired
    IMetaModelTableService metaModelTableService;

    @Autowired
    IUserDatasetService userDatasetService;


    /**
     * 根据dataMode中的queryList中的标签列表，获取要查询的表信息，生成modelData中的queryTables
     *
     * @param dataModel_v2_ 目标内部数据结构
     * @throws ParseException 说明：此方法中使用了DataModel_v2_0中QueryField_v2_0s中的所有QueryField_v2_0引用。
     *                        在方法中，处理QueryField_v2_0s的值，即可相应的调整QueryField_v2_0s中的QueryField_v2_0值
     */

    public void parse(DataModel_v2 dataModel_v2_) throws ParseException {

        //Step1：获取DataModel_v2_0中的QueryField_v2_0s中所有的QueryField_v2_0的引用
        List<QueryField_v2> allQueryField_v2_s = getAllQueryField_v2_0s(dataModel_v2_);
        if (allQueryField_v2_s.size() == 0) {
            String msg = "请求中的queryList和Condition_v2_0List中没有有效标签";
            log.error(msg);
            throw new ParseException(msg);
        }

        //Step2：获取查询中用到的表及其最新账期
        Map<String, List<DataModelBase.QueryTable>> queryTables = getQueryTables(allQueryField_v2_s);
        List<DataModelBase.QueryTable> asset = queryTables.get("asset");
        List<DataModelBase.QueryTable> dataset = queryTables.get("dataset");
        if (!asset.isEmpty()){

            dataModel_v2_.setQueryTables(asset);

            //Step3：queryTables中需要包含域主表
            handleDomainMainTable(asset);

            //Step4：校验QueryField_v2_0s中所有的字段的acct不能大于tableCode表的最新acct
            checkQueryField_v2_0Acct(dataModel_v2_);

            //Step5：横纵表组合筛选的实现逻辑：最多一个纵表
            if (null != queryTables && !queryTables.isEmpty() && queryTables.size() > 1) checkQueryTable(asset);
        }
        List<DataModelBase.QueryTable> queryTablesRes = new ArrayList<>(asset);

        queryTablesRes.addAll(dataset);
        dataModel_v2_.setQueryTables(queryTablesRes);

    }

    /**
     * Step1：获取QueryField_v2_0s和Condition_v2_0s中所有的QueryField_v2_0的引用
     *
     * @param dataModel_v2_ DataModel_v2
     * @return QueryField_v2_0的列表
     */
    private List<QueryField_v2> getAllQueryField_v2_0s(DataModel_v2 dataModel_v2_) {
        //添加QueryField_v2_0s中的QueryField_v2_0引用
        List<QueryField_v2> ret = new ArrayList<>(dataModel_v2_.getQueryFields());

        //添加Condition_v2_0s中的QueryField_v2_0引用
        addQueryField_v2_0Recursive(ret, dataModel_v2_.getConditions());
        return ret;
    }

    /**
     * 递归处理Condition_v2_0s
     *
     * @param ret            QueryField_v2_0的列表
     * @param condition_v2_ 待处理的Condition_v2_0列表
     */
    private void addQueryField_v2_0Recursive(List<QueryField_v2> ret, List<Condition_v2> condition_v2_) {
        if (condition_v2_ == null) {
            log.info("Condition_v2_0s为空，无需解析");
            return;
        }

        //遍历所有的Condition_v2_0s
        for (Condition_v2 one : condition_v2_) {
            if (one.getQueryField() != null) {
                ret.add(one.getQueryField());
            }

            //递归处理subCondition_v2_0s
            if (one.getSubConditions() != null && one.getSubConditions().size() > 0) {
                addQueryField_v2_0Recursive(ret, one.getSubConditions());
            }
        }
    }

//    /**
//     * Step2：生成queryTables
//     *
//     * @return meta中需要的queryTables列表
//     */
//    private List<DataModel_v2.QueryTable> getQueryTables(List<QueryField_v2> allQueryField_v2_0s) throws ParseException {
//        //StepA:获取所有标签的tableCode。有可能有重复tableCode，所以使用Set
//        Set<String> allTableCodesSet = new HashSet<>();
//        //定义dataset需要处理的表的集合
//        Set<String> datasetTables = new HashSet<>();
//
//
//        for (QueryField_v2 one : allQueryField_v2_0s) {
//            if ("dataset".equals(one.getQueryType())) {
//                datasetTables.add(one.getTableCode());
//            }
//            if ("asset".equals(one.getQueryType())) {
//                allTableCodesSet.add(one.getTableCode());
//            }
//        }
//        List<String> allTableCodesList = new ArrayList<>(allTableCodesSet);
//
//        List<ModelAndEntityTableInfo> tablesInfos = metaAssetInfoService.getTableInfosByTableCodes(allTableCodesList);
//
//        List<DataSetCodeAndColum> dataSetByDatasetTable = userDatasetService.getDataSetByDatasetTable(new ArrayList<>(datasetTables));
//
//
//        //查询出来asset所涉及到的表
//        List<DataModelBase.QueryTable> queryTables = genQueryTablesByTableInfos(tablesInfos);
//        //将自定义数据集的表增加到queryTables
//        if (!dataSetByDatasetTable.isEmpty()) {
//            for (DataSetCodeAndColum datasetTable : dataSetByDatasetTable) {
//                DataModelBase.QueryTable queryTable = new DataModelBase.QueryTable();
//                queryTable.setAcct("");
//                queryTable.setAcctFieldCode("");
//                //请求获取数据集的关联字段
//                queryTable.setRelationFieldCode(datasetTable.getJoinColumnCode());
//                queryTable.setTableCode(datasetTable.getTableCode());
//                queryTables.add(queryTable);
//            }
//        }
//        return queryTables;
//
//
//    }



    /**
     * Step2：生成queryTables
     *
     * @return meta中需要的queryTables列表
     */
    private Map<String,List<DataModel_v2.QueryTable>> getQueryTables(List<QueryField_v2> allQueryField_v2_s) throws ParseException {
        //StepA:获取所有标签的tableCode。有可能有重复tableCode，所以使用Set
        Set<String> allTableCodesSet = new HashSet<>();
        //定义dataset需要处理的表的集合
        Set<String> datasetTables = new HashSet<>();


        for (QueryField_v2 one : allQueryField_v2_s) {
            if ("dataset".equals(one.getQueryType())) {
                datasetTables.add(one.getTableCode());
            }
            if ("asset".equals(one.getQueryType())) {
                allTableCodesSet.add(one.getTableCode());
            }
        }
        List<String> allTableCodesList = new ArrayList<>(allTableCodesSet);

        List<ModelAndEntityTableInfo> tablesInfos = metaAssetInfoService.getTableInfosByTableCodes(allTableCodesList);

        List<DataSetCodeAndColum> dataSetByDatasetTable = userDatasetService.getDataSetByDatasetTable(new ArrayList<>(datasetTables));


        //查询出来asset所涉及到的表
        List<DataModelBase.QueryTable> queryTablesAsset = genQueryTablesByTableInfos(tablesInfos);
        List<DataModelBase.QueryTable> queryTablesDataSet = new ArrayList<>();
        //将自定义数据集的表增加到queryTables
        if ( null !=dataSetByDatasetTable &&  !dataSetByDatasetTable.isEmpty()) {
            for (DataSetCodeAndColum datasetTable : dataSetByDatasetTable) {
                DataModelBase.QueryTable queryTable = new DataModelBase.QueryTable();
                queryTable.setAcct("");
                queryTable.setAcctFieldCode("");
                //请求获取数据集的关联字段
                queryTable.setRelationFieldCode(datasetTable.getJoinColumnCode());
                queryTable.setTableCode(datasetTable.getTableCode());
                queryTablesDataSet.add(queryTable);
            }
        }

        Map<String,List<DataModel_v2.QueryTable>> res = new HashMap<>();
        res.put("asset",queryTablesAsset);
        res.put("dataset",queryTablesDataSet);
        return res;


    }



    /**
     * Step3：queryTables中需要包含域主表
     *
     * @param queryTables DataModel_v2_0中的queryTables
     */
    private void handleDomainMainTable(List<DataModel_v2.QueryTable> queryTables) throws ParseException {
        //获取所有不是域主表的tableCode列表
        List<String> noMainTableList = getNoMainTableList(queryTables);
        if (noMainTableList.size() == 0) {
            //全是域主表，无需额外处理
            return;
        }

        //根据不是域主表的tableCodeList，获取其与主表信息
        List<ModelAndEntityTableInfo> mainTableInfos = metaAssetInfoService.getDomainMasterTableInfosByTableCodes(noMainTableList);
        List<DataModel_v2.QueryTable> mainQueryTables = genQueryTablesByTableInfos(mainTableInfos);

        //判定一下mainQueryTables域主表是否已经存在在DataModel_v2_0的queryTables中
        List<DataModel_v2.QueryTable> adds = new ArrayList<>();
        for (DataModel_v2.QueryTable one : mainQueryTables) {
            String tmpTableCode = one.getTableCode();
            boolean foundInQueryTables = false;
            for (DataModel_v2.QueryTable queryTable : queryTables) {
                if (queryTable.getTableCode().equals(tmpTableCode)) {
                    //处理下一个域主表
                    foundInQueryTables = true;
                    break;
                }
            }

            //queryTables中没有，则需要增加到queryTables中
            if (!foundInQueryTables) {
                adds.add(one);
            }
        }

        if (adds.size() > 0) {
            queryTables.addAll(mainQueryTables);
        }
    }

    /**
     * 获取所有不是域主表的tableCode列表
     *
     * @param queryTables
     * @return 不是域主表的tableCode列表
     */
    private List<String> getNoMainTableList(List<DataModel_v2.QueryTable> queryTables) {
        Set<String> noMainTableList = new HashSet<>();
        for (DataModel_v2.QueryTable one : queryTables) {
            if (!one.isMain()) {
                noMainTableList.add(one.getTableCode());
            }
        }

        return new ArrayList<>(noMainTableList);
    }


    /**
     * modelAndEntityTableInfos，来生成queryTable相关信息
     *
     * @param modelAndEntityTableInfos DB中查询的表信息
     * @return queryTable信息
     */
    private List<DataModel_v2.QueryTable> genQueryTablesByTableInfos(List<ModelAndEntityTableInfo> modelAndEntityTableInfos) throws ParseException {
        if (modelAndEntityTableInfos == null || modelAndEntityTableInfos.size() == 0) {
            return null;
        }

        //根据表信息，生成DataModel_v2_0中需要的QueryTable列表
        List<DataModel_v2.QueryTable> ret = new ArrayList<>();
        for (ModelAndEntityTableInfo one : modelAndEntityTableInfos) {
            DataModel_v2.QueryTable queryTable = new DataModel_v2.QueryTable();
            queryTable.setMain("1".equals(one.getIsMaster()));
            queryTable.setTableCode(one.getTableCode());
            queryTable.setAcct(one.getAcct());
            //TODO 根据域信息，获取其对应的域主键和域账期字段信息
            //如果是用户域
            if (one.getDomainGrpId().equals(MetaDataConst.USER_DOMAIN_ID) || one.getDomainGrpId().equals(MetaDataConst.FAMILY_DOMAIN_ID)) {
                queryTable.setRelationFieldCode(MetaDataConst.USER_FAMILY_DOMAIN_MASTER_KEY);
            } else if (one.getDomainGrpId().equals(MetaDataConst.ENTERPRICES_DOMAIN_ID)) {
                queryTable.setRelationFieldCode(MetaDataConst.ENTERPRICES_DOMAIN_MASTER_KEY);
            } else {
                //默认写空
                throw new ParseException("要查询的表不属于指定域");
            }
            queryTable.setAcctFieldCode(MetaDataConst.USER_FAMILY_DOMAIN_ACCT_KEY);

            ret.add(queryTable);
        }

        return ret;
    }


    /**
     * Step4：校验QueryField_v2_0s中所有的字段的acct不能大于tableCode表的最新acct
     *
     * @param dataModel_v2_ DataModel_v2
     * @throws ParseException 校验异常
     */
    private void checkQueryField_v2_0Acct(DataModel_v2 dataModel_v2_) throws ParseException {
        List<QueryField_v2> queryField_v2_ = dataModel_v2_.getQueryFields();

        // 将DataModel_v2_0中QueryTables 转为tableMap 方便使用 key: tableCode value: QueryTable
        Map<String, DataModel_v2.QueryTable> tableMap = dataModel_v2_.getQueryTables().stream()
                .collect(Collectors.toMap(DataModel_v2.QueryTable::getTableCode, table -> table, (v1, v2) -> v1));

        for (QueryField_v2 one : queryField_v2_) {
            DataModel_v2.QueryTable table = tableMap.get(one.getTableCode());

            if (!"asset".equals(one.getQueryType())) continue;
            if (null == table) {
                throw new ParseException("QueryTables找不到tableCode为" + one.getTableCode() + "的table");
            }
            if (Integer.parseInt(one.getAsset().getAcct()) > Integer.parseInt(table.getAcct())) {
                throw new ParseException("标签id：" + one.getAsset().getAssetId() + "的输入账期" + one.getAsset().getAcct()
                        + "大于所属表：" + one.getTableCode() + "的最新账期：" + table.getAcct());
            }
        }
    }


    /**
     * <p>
     * Step4：横纵表组合筛选的实现逻辑：最多一个纵表
     * </p>
     *
     * @param queryTables 所有要查询的表信息
     * @author zhaozesheng
     * @since 2022-11-15 16:17:55
     */
    private void checkQueryTable(List<DataModel_v2.QueryTable> queryTables) throws ParseException {

        if (!queryTables.isEmpty()) {

            List<String> tableCodes = queryTables.stream().map(DataModel_v2.QueryTable::getTableCode).collect(Collectors.toList());
            QueryWrapper<MetaEntityTable> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("table_code", tableCodes);
            List<MetaEntityTable> metaEntityTablelist = metaEntityTableService.list(queryWrapper);
            if (!metaEntityTablelist.isEmpty()) {
                // List<MetaEntityTable> zb = list.stream().filter(item -> item.getTableType().equals(ZB)).collect(Collectors.toList());
                List<Long> modelIds = metaEntityTablelist.stream().map(MetaEntityTable::getModelTableId).collect(Collectors.toList());
                List<MetaModelTable> metaModelTables = metaModelTableService.list(new QueryWrapper<MetaModelTable>().in("id", modelIds));
                //判断纵表
                List<MetaModelTable> zb = metaModelTables.stream().filter(item -> item.getModelType().equals("2")).collect(Collectors.toList());
                if (!zb.isEmpty() && zb.size() > 1) {
                    throw new ParseException("查询的表格中最多只能有一个纵表,当前查询有：" + zb.size() + " 个纵表");
                }

                Set<String> domainCodes = metaModelTables.stream().map(MetaModelTable::getDomainGrpId).collect(Collectors.toSet());
                if (!domainCodes.isEmpty() && domainCodes.size() > 1)
                    throw new ParseException("查询的所有标签只能属于一个域,当前查询标签属于：" + domainCodes.size() + " 个域");

            }
        }
    }
}
