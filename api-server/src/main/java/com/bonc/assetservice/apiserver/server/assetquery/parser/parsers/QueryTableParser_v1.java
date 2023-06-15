package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.Condition_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v1;
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
public class QueryTableParser_v1 {

    @Autowired
    IMetaAssetInfoService metaAssetInfoService;

    @Autowired
    IMetaEntityTableService metaEntityTableService;

    @Autowired
    IMetaModelTableService metaModelTableService;



    /**
     * 根据dataMode中的queryList中的标签列表，获取要查询的表信息，生成modelData中的queryTables
     * @param dataModelV1 目标内部数据结构
     * @throws ParseException
     * 说明：此方法中使用了dataModel中queryFields中的所有QueryField引用。
     *      在方法中，处理QueryFields的值，即可相应的调整queryFields中的QueryField值
     */

    public void parse( DataModel_v1 dataModelV1) throws ParseException {

        //Step1：获取dataModel中的queryFields中所有的QueryField的引用
        List<QueryField_v1> allQueryFieldV1s = getAllQueryFields(dataModelV1);
        if (allQueryFieldV1s.size() == 0) {
            String msg = "请求中的queryList和conditionList中没有有效标签";
            log.error(msg);
            throw new ParseException(msg);
        }

        //Step2：获取查询中用到的表及其最新账期
        List<DataModel_v1.QueryTable> queryTables = getQueryTables(allQueryFieldV1s);
        dataModelV1.setQueryTables(queryTables);

        //Step3：queryTables中需要包含域主表
        handleDomainMainTable(queryTables);

        //Step4：校验queryFields中所有的字段的acct不能大于tableCode表的最新acct
        checkQueryFieldsAcct(dataModelV1);

        //Step5：横纵表组合筛选的实现逻辑：最多一个纵表
        if (null != queryTables && !queryTables.isEmpty() && queryTables.size() >1) checkQueryTable(queryTables);
    }

    /**
     * Step1：获取queryFields和conditions中所有的QueryField的引用
     * @param dataModelV1 dataModelV1
     * @return QueryField的列表
     */
    private List<QueryField_v1> getAllQueryFields(DataModel_v1 dataModelV1) {
        //添加queryFields中的QueryField引用
        List<QueryField_v1> ret = new ArrayList<>(dataModelV1.getQueryFieldV1s());

        //添加conditions中的QueryField引用
        addConditionQueryFieldRecursive(ret, dataModelV1.getConditionV1s());
        return ret;
    }

    /**
     * 递归处理conditions
     * @param ret QueryField的列表
     * @param conditionV1s 待处理的condition列表
     */
    private void addConditionQueryFieldRecursive(List<QueryField_v1> ret, List<Condition_v1> conditionV1s) {
        if (conditionV1s == null) {
            log.info("conditions为空，无需解析");
            return;
        }

        //遍历所有的conditions
        for (Condition_v1 one : conditionV1s) {
            if (one.getQueryFieldV1() != null) {
                ret.add(one.getQueryFieldV1());
            }

            //递归处理subConditions
            if (one.getSubConditionV1s() != null && one.getSubConditionV1s().size() > 0) {
                addConditionQueryFieldRecursive(ret, one.getSubConditionV1s());
            }
        }
    }

    /**
     * Step2：生成queryTables
     * @return meta中需要的queryTables列表
     */
    private List<DataModel_v1.QueryTable> getQueryTables(List<QueryField_v1> allQueryFieldV1s) throws ParseException {
        //StepA:获取所有标签的tableCode。有可能有重复tableCode，所以使用Set
        Set<String> allTableCodesSet = new HashSet<>();
        for (QueryField_v1 one : allQueryFieldV1s) {
            allTableCodesSet.add(one.getTableCode());
        }
        List<String> allTableCodesList = new ArrayList<>(allTableCodesSet);

        List<ModelAndEntityTableInfo> tablesInfos = metaAssetInfoService.getTableInfosByTableCodes(allTableCodesList);
        return genQueryTablesByTableInfos(tablesInfos);
    }

    /**
     * Step3：queryTables中需要包含域主表
     * @param queryTables dataModel中的queryTables
     */
    private void handleDomainMainTable(List<DataModel_v1.QueryTable> queryTables) throws ParseException {
        //获取所有不是域主表的tableCode列表
        List<String> noMainTableList = getNoMainTableList(queryTables);
        if (noMainTableList.size() == 0) {
            //全是域主表，无需额外处理
            return;
        }

        //根据不是域主表的tableCodeList，获取其与主表信息
        List<ModelAndEntityTableInfo> mainTableInfos = metaAssetInfoService.getDomainMasterTableInfosByTableCodes(noMainTableList);
        List<DataModel_v1.QueryTable> mainQueryTables = genQueryTablesByTableInfos(mainTableInfos);

        //判定一下mainQueryTables域主表是否已经存在在dataModel的queryTables中
        List<DataModel_v1.QueryTable> adds = new ArrayList<>();
        for (DataModel_v1.QueryTable one : mainQueryTables) {
            String tmpTableCode = one.getTableCode();
            boolean foundInQueryTables = false;
            for (DataModel_v1.QueryTable queryTable : queryTables) {
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
     * @param queryTables
     * @return 不是域主表的tableCode列表
     */
    private List<String> getNoMainTableList(List<DataModel_v1.QueryTable> queryTables) {
        Set<String> noMainTableList = new HashSet<>();
        for (DataModel_v1.QueryTable one : queryTables) {
            if (!one.isMain()) {
                noMainTableList.add(one.getTableCode());
            }
        }

        return new ArrayList<>(noMainTableList);
    }


    /**
     * modelAndEntityTableInfos，来生成queryTable相关信息
     * @param modelAndEntityTableInfos DB中查询的表信息
     * @return queryTable信息
     */
    private List<DataModel_v1.QueryTable> genQueryTablesByTableInfos(List<ModelAndEntityTableInfo> modelAndEntityTableInfos) throws ParseException {
        if (modelAndEntityTableInfos == null || modelAndEntityTableInfos.size() == 0) {
            return null;
        }

        //根据表信息，生成DataModel中需要的QueryTable列表
        List<DataModel_v1.QueryTable> ret = new ArrayList<>();
        for (ModelAndEntityTableInfo one : modelAndEntityTableInfos) {
            DataModel_v1.QueryTable queryTable = new DataModel_v1.QueryTable();
            queryTable.setMain("1".equals(one.getIsMaster()));
            queryTable.setTableCode(one.getTableCode());
            queryTable.setAcct(one.getAcct());
            //TODO 根据域信息，获取其对应的域主键和域账期字段信息
            //如果是用户域
            if (one.getDomainGrpId().equals(MetaDataConst.USER_DOMAIN_ID) || one.getDomainGrpId().equals(MetaDataConst.FAMILY_DOMAIN_ID)){
                queryTable.setRelationFieldCode(MetaDataConst.USER_FAMILY_DOMAIN_MASTER_KEY);
            }else if (one.getDomainGrpId().equals(MetaDataConst.ENTERPRICES_DOMAIN_ID)){
                queryTable.setRelationFieldCode(MetaDataConst.ENTERPRICES_DOMAIN_MASTER_KEY);
            }else{
                //默认写空
                throw new ParseException("要查询的表不属于指定域");
            }
            queryTable.setAcctFieldCode(MetaDataConst.USER_FAMILY_DOMAIN_ACCT_KEY);

            ret.add(queryTable);
        }

        return ret;
    }


    /**
     * Step4：校验queryFields中所有的字段的acct不能大于tableCode表的最新acct
     * @param dataModelV1 dataModelV1
     * @throws ParseException 校验异常
     */
    private void checkQueryFieldsAcct (DataModel_v1 dataModelV1) throws ParseException{
        List<QueryField_v1> queryFieldV1s = dataModelV1.getQueryFieldV1s();

        // 将DataModel中QueryTables 转为tableMap 方便使用 key: tableCode value: QueryTable
        Map<String, DataModel_v1.QueryTable> tableMap = dataModelV1.getQueryTables().stream()
                .collect(Collectors.toMap(DataModel_v1.QueryTable::getTableCode, table -> table, (v1, v2) -> v1));

        for (QueryField_v1 one : queryFieldV1s) {
            DataModel_v1.QueryTable table = tableMap.get(one.getTableCode());
            if (null == table) {
                throw new ParseException("QueryTables找不到tableCode为" + one.getTableCode() + "的table");
            }
            if (Integer.parseInt(one.getAcct()) > Integer.parseInt(table.getAcct())) {
                throw new ParseException("标签id：" + one.getAssetId() + "的输入账期" + one.getAcct()
                        + "大于所属表：" + one.getTableCode() + "的最新账期：" + table.getAcct() );
            }
        }
    }


    /**
     * <p>
     * Step4：横纵表组合筛选的实现逻辑：最多一个纵表
     * </p>
     * @param queryTables 所有要查询的表信息
     * @author zhaozesheng
     * @since 2022-11-15 16:17:55
     */
    private void checkQueryTable(List<DataModel_v1.QueryTable> queryTables) throws ParseException {

        if (!queryTables.isEmpty()) {

            List<String> tableCodes = queryTables.stream().map(DataModel_v1.QueryTable::getTableCode).collect(Collectors.toList());
            QueryWrapper<MetaEntityTable> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("table_code", tableCodes);
            List<MetaEntityTable> metaEntityTablelist = metaEntityTableService.list(queryWrapper);
            if (!metaEntityTablelist.isEmpty()){
               // List<MetaEntityTable> zb = list.stream().filter(item -> item.getTableType().equals(ZB)).collect(Collectors.toList());
                List<Long> modelIds = metaEntityTablelist.stream().map(MetaEntityTable::getModelTableId).collect(Collectors.toList());
                List<MetaModelTable> metaModelTables = metaModelTableService.list(new QueryWrapper<MetaModelTable>().in("id", modelIds));
                //判断纵表
                //TODO xingyi 临时去掉纵表的校验，性能测试后调教参数
//                List<MetaModelTable> zb = metaModelTables.stream().filter(item -> item.getModelType().equals("2")).collect(Collectors.toList());
//                if (!zb.isEmpty() && zb.size() > 1){
//                    throw new ParseException("查询的表格中最多只能有一个纵表,当前查询有：" + zb.size()+" 个纵表");
//                }

                Set<String> domainCodes = metaModelTables.stream().map(MetaModelTable::getDomainGrpId).collect(Collectors.toSet());
                if (!domainCodes.isEmpty() && domainCodes.size() > 1) throw new ParseException("查询的所有标签只能属于一个域,当前查询标签属于：" + domainCodes.size()+" 个域");

            }
        }
    }
}
