package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.*;
import com.bonc.assetservice.metadata.constant.ProvinceEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FromItemBuild_v2 extends Builder {

    public void build(List<Condition_v2> consitions,
                      List<DataModelBase.QueryTable> queryTables,
                      List<QueryField_v2> queryFields,
                      String provCode, String provId,
                      String areaCode,
                      String areaId,
                      PlainSelect plainSelect,
                      ThreadLocal<Map<String, Map<Long, String>>> threadFieldAndQueryFromRelation,
                      ThreadLocal<List<QueryFrom>> threadQueryFroms,
                      ThreadLocal<QueryFrom> threadMainTable) {

        Map<String, Map<Long, String>> stringMapMap = threadFieldAndQueryFromRelation.get();


       List<QueryFrom> queryFroms = createQueryFroms(consitions, queryTables, queryFields, provCode, provId,areaId,areaCode, stringMapMap);

        threadQueryFroms.set(queryFroms);
        threadFieldAndQueryFromRelation.set(stringMapMap);
        Optional<QueryFrom> optional = queryFroms.stream().filter(QueryFrom::isMain).max(Comparator.comparingInt(o -> Integer.parseInt(o.getAcct())));
        QueryFrom mainTable = optional.orElse(new QueryFrom());
        threadMainTable.set(mainTable);
        // 域主表放到from中
        plainSelect.setFromItem(new SubSelect().withSelectBody(mainTable.getSelect()).withAlias(new Alias(mainTable.getAlias())));
    }


    /**
     * 解析DataModel中各字段来源，创建对应QueryFrom模型 同时保存各字段id与QueryFrom的关系
     *
     * @param queryTables               需要查询的表
     * @param queryFields               查询字段
     * @param fieldAndQueryFromRelation 各字段与QueryFrom的关系
     * @return key 表别名 value 查询字段列表
     * @Author 李维帅
     * @Date 2022/6/15 10:53
     **/
    private List<QueryFrom> createQueryFroms(List<Condition_v2> consitions,
                                             List<DataModelBase.QueryTable> queryTables,
                                             List<QueryField_v2> queryFields,
                                             String provCode,
                                             String provId,
                                             String areaId,
                                             String areaCode,
                                             Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        Assert.notEmpty(queryTables, "DataModel中QueryTables为空");
        // 将DataModel中QueryTables 转为tableMap 方便使用 key: tableCode value: QueryTable
        Map<String, DataModel_v2.QueryTable> tableMap = queryTables.stream()
                .collect(Collectors.toMap(DataModel_v2.QueryTable::getTableCode, table -> table, (v1, v2) -> v1));

        // fromMap存储 DataModel解析后 所有的from对象 key: QueryFrom别名 value: QueryFrom
        Map<String, QueryFrom> fromMap = new HashMap<>();

        // relationFieldMap存储 DataModel最外层查询字段中使用到的RelationModel内的字段 key: 字段id value: 别名
        Map<Long, String> relationFieldMap = new HashMap<>();


        // 解析查询字段使用的QueryFrom
        Assert.notEmpty(queryFields, "DataModel中QueryFields为空");
        queryFields.forEach(field -> addFieldToFromMap(field, fromMap, tableMap, relationFieldMap, fieldAndQueryFromRelation));

        /*
            如果是大宽表模式则用此
         */
        if (Enums.SqlBuilderModel.WIDE_TABLE_MODE.getValue().equalsIgnoreCase(MODEL)) {
            parseConditionField(consitions, fromMap, tableMap, relationFieldMap, fieldAndQueryFromRelation);
        }

        //处理fromMap，确认包含最新账期的域主表信息
        getDomainMasterTableWithNewestAcct(queryTables, fromMap);


        return queryFromSimpleTransformSelect(new ArrayList<>(fromMap.values()), provCode, provId,areaCode,areaId);
    }


    /**
     * 解析查询条件中的查询字段放到fromMap中
     *
     * @param conditions     查询条件
     * @param tableMap       表中对应字段
     * @param relationFields 关联模型中的字段信息
     * @Author 李维帅
     * @Date 2022/6/16 9:12
     **/
    private void parseConditionField(List<Condition_v2> conditions, Map<String, QueryFrom> fromMap,
                                     Map<String, DataModel_v2.QueryTable> tableMap, Map<Long, String> relationFields,
                                     Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (conditions != null && conditions.size() > 0) {
            conditions.forEach(condition -> {
                if (condition.getSubConditions() != null && condition.getSubConditions().size() > 0) {
                    parseConditionField(condition.getSubConditions(), fromMap, tableMap, relationFields, fieldAndQueryFromRelation);
                } else {
                    addFieldToFromMap(condition.getQueryField(), fromMap, tableMap, relationFields, fieldAndQueryFromRelation);
                }
            });
        }
    }


    /**
     * 处理fromMap，确认包含最新账期的域主表信息
     *
     * @param queryTables
     * @param fromMap
     */
    private void getDomainMasterTableWithNewestAcct(List<DataModelBase.QueryTable> queryTables, Map<String, QueryFrom> fromMap) {
        // 获取fromMap中acct最大的域主表
        Optional<QueryFrom> queryFromOptional = fromMap.values().stream().filter(QueryFrom::isMain).max(Comparator.comparingInt(o -> Integer.parseInt(o.getAcct())));
        // 获取queryTables中记录的域主表的最新账期，有且只有一条记录
        Optional<DataModel_v2.QueryTable> queryTableOptional = queryTables.stream().filter(DataModel_v2.QueryTable::isMain).findFirst();

        //是否需要添加最新域主表
        boolean needDomainMainTable = false;
        if (!queryFromOptional.isPresent()) {
            needDomainMainTable = true;
        } else {
            //检查queryList中此域主表的最新账期是否比queryTables中的最新账期新
            Integer acctInQueryFrom = Integer.parseInt(queryFromOptional.get().getAcct());
            Integer acctInDomainMasterTable = Integer.parseInt(queryTableOptional.get().getAcct());
            needDomainMainTable = acctInQueryFrom < acctInDomainMasterTable;
        }

        if (needDomainMainTable) {
            DataModel_v2.QueryTable domainMasterTable = queryTableOptional.get();
            String alias = domainMasterTable.getTableCode() + "_" + domainMasterTable.getAcct();
            QueryFrom newQueryFrom = new QueryFrom(domainMasterTable, alias, domainMasterTable.getAcct());
            fromMap.put(alias, newQueryFrom);
        }
    }


    /**
     * queryFrom转换为SelectBody
     *
     * @param queryFroms queryFrom列表
     * @return java.util.List<com.bonc.assetservice.assetquery.utils.model.QueryFrom>
     * @Author 李维帅
     * @Date 2022/6/27 11:13
     **/
    private List<QueryFrom> queryFromSimpleTransformSelect(List<QueryFrom> queryFroms,
                                                           String provCode,
                                                           String provId,
                                                           String areaCode,
                                                           String areaId) {
        for (QueryFrom queryFrom : queryFroms) {
            log.info("解析后的字段来源：【{}】-{}", queryFrom.getAlias(), JSONObject.toJSONString(queryFrom.getFields()));
            PlainSelect subSelect = new PlainSelect();
            List<SelectItem> subSelectItems = new ArrayList<>();
            for (String code : queryFrom.getFields()) {
                subSelectItems.add(new SelectExpressionItem().withExpression(buildColumn(code, null)));
            }
            subSelect.setSelectItems(subSelectItems);
            subSelect.setFromItem(new Table(queryFrom.getTableCode()));
            if (StrUtil.isNotEmpty(queryFrom.getAcct()) && StrUtil.isNotEmpty(queryFrom.getAcctFieldCode())) {
                if (StringUtils.isNotEmpty(areaId)){
                    Expression whereExpression_acct = buildCompareExpression(buildColumn(queryFrom.getAcctFieldCode(), null), queryFrom.getAcct(), Enums.CompareOperator.EQUALS);
                    Expression whereExpression_prov = buildCompareExpression(buildColumn(provCode, null), provId, Enums.CompareOperator.EQUALS);
                    Expression whereExpression_area = buildCompareExpression(buildColumn(areaCode, null), areaId, Enums.CompareOperator.EQUALS);
                    AndExpression andExpression = new AndExpression();
                    andExpression.setLeftExpression(whereExpression_prov);
                    andExpression.setRightExpression(whereExpression_area);
                    subSelect.setWhere(new AndExpression(whereExpression_acct, andExpression));

                }else{
                    Expression whereExpression_acct = buildCompareExpression(buildColumn(queryFrom.getAcctFieldCode(), null), queryFrom.getAcct(), Enums.CompareOperator.EQUALS);
                    Expression whereExpression_prov = buildCompareExpression(buildColumn(provCode, null), provId, Enums.CompareOperator.EQUALS);
                    subSelect.setWhere(new AndExpression(whereExpression_acct, whereExpression_prov));
                }





            }
            queryFrom.setSelect(subSelect);
        }
        return queryFroms;
    }


    /**
     * 添加字段到fromMap 同时保存字段与QueryFrom的关系
     *
     * @param field          字段信息
     * @param fromMap        fromMap
     * @param tableMap       tableMap
     * @param relationFields 关联模型中的字段信息
     * @Author 李维帅
     * @Date 2022/6/27 10:48
     **/
    private void addFieldToFromMap(QueryField_v2 field, Map<String, QueryFrom> fromMap,
                                   Map<String, DataModel_v2.QueryTable> tableMap, Map<Long, String> relationFields,
                                   Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        String alias = createFromAlias(field, relationFields);

        String code = null;
        Long id = null;
        String acct = null;

        //自定义数据集时可能code不存在，需要查找colums的code,此处定义是为了是数据集模式的时候查看colums的字段
        List<String> colums = new ArrayList<>();
        if ("asset".equals(field.getQueryType())) {
            code = field.getAsset().getAssetCode();
            id = field.getAsset().getAssetId();
            acct = field.getAsset().getAcct();
        } else {
            code = field.getDataSet().getCode();
            id = Long.valueOf(field.getDataSet().getDatasetId());

            List<QueryField_v2.Column> columns = field.getDataSet().getColumns();
            if (null != columns && !columns.isEmpty()) {
                for (QueryField_v2.Column column : columns) {
                    colums.add(column.getCode());
                }
            }
        }

        if (fromMap.containsKey(alias)) {
            if (StringUtils.isNotEmpty(code)) {
                fromMap.get(alias).getFields().add(code);
            }
            if (!colums.isEmpty()) {
                fromMap.get(alias).getFields().addAll(colums);
            }

        } else {
            Assert.isTrue(tableMap.containsKey(field.getTableCode()), "字段所属数据表【{}】QueryTables中未定义", field.getTableCode());
            QueryFrom queryFrom = new QueryFrom(tableMap.get(field.getTableCode()), alias, acct);
            if (StringUtils.isNotEmpty(code)) {
                queryFrom.getFields().add(code);
            }
            if (!colums.isEmpty()) {
                queryFrom.getFields().addAll(colums);
            }
            fromMap.put(alias, queryFrom);
        }
        Map<Long, String> longStringMap = fieldAndQueryFromRelation.get(field.getQueryType());
        longStringMap.put(id, alias);

    }

    /**
     * 根据QueryField创建from别名
     *
     * @param field          字段信息
     * @param relationFields 关联模型中的字段信息
     * @return java.lang.String
     * @Author 李维帅
     * @Date 2022/6/27 9:47
     **/
    private String createFromAlias(QueryField_v2 field, Map<Long, String> relationFields) {
        if ("asset".equals(field.getQueryType())) {
            // 查询字段在关联模型中 别名取relation的alias
            if (field.getAsset().getAssetId() != null && relationFields.containsKey(field.getAsset().getAssetId())) {
                return relationFields.get(field.getAsset().getAssetId());
            }
            // 其余情况 别名规则：表名_账期
            return field.getTableCode() + (StrUtil.isBlank(field.getAsset().getAcct()) ? "" : "_" + field.getAsset().getAcct());
        }

        if ("dataset".equals(field.getQueryType())) {


            // 查询字段在关联模型中 别名取relation的alias
            if (field.getDataSet().getDatasetId() != null && relationFields.containsKey(field.getDataSet().getDatasetId())) {
                return relationFields.get(Long.valueOf(field.getDataSet().getDatasetId()));
            }
            // 其余情况 别名规则：表名_数据集ID
            return field.getTableCode() + "_" + field.getDataSet().getDatasetId();
        }
        return null;
    }

}
