package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder.*;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName SQLBuilder
 * @Author 李维帅
 * @Date 2022/6/13 10:34
 * @Version 1.0
 **/
@Slf4j
@Component
public class SqlBuilder_v1 extends SqlBuilderBase<DataModel_v1> {




    @Autowired
    private SelectItemsBuild_v1 selectItems;

    @Resource
    private FromItemBuild_v1 fromItem;

    @Resource
    private JoinsBuilder_v1 joins;

    @Resource
    private OrderByBuilder_v1 orderBy;

    @Resource
    private GroupByBuilder_v1 groupBy;

    @Resource
    private LimitBuilder limit;

    @Resource
    private WhereBuild_v1 where;


    private ThreadLocal<Map<String, Map<Long,String>>> threadFieldAndQueryFromRelation = new ThreadLocal<>();
    private ThreadLocal<List<QueryFrom>> threadQueryFroms = new ThreadLocal<>();
    private ThreadLocal<QueryFrom> threadMainTable = new ThreadLocal<>();

    @Override
    public void init(DataModel_v1 dataModelV1) {
        Map<String, Map<Long, String>> fieldAndQueryFromRelation = new HashMap<>();
        fieldAndQueryFromRelation.put(Enums.LabelType.DATASET.getValue(), new HashMap<>());
        fieldAndQueryFromRelation.put(Enums.LabelType.ASSET.getValue(), new HashMap<>());
        threadFieldAndQueryFromRelation.set(fieldAndQueryFromRelation);

        List<QueryFrom> queryFroms =new ArrayList<>();
        threadQueryFroms.set(queryFroms);

        QueryFrom mainTable = new QueryFrom();
        threadMainTable.set(mainTable);

    }

    @Override
    public void setSelectItems(DataModel_v1 dataModelV1, PlainSelect returnSelect) {
        // 1、构建查询字段

        selectItems.build(
                dataModelV1.getQueryFieldV1s(),
                dataModelV1.isAggregation(),
                threadFieldAndQueryFromRelation.get() ,
                true,
                returnSelect );
    }

    @Override
    public void setFromItem(DataModel_v1 dataModel_v1,
                            PlainSelect plainSelect) {
        //2、构建from
        fromItem.build(dataModel_v1.getConditionV1s(),
                dataModel_v1.getQueryTables(),
                dataModel_v1.getQueryFieldV1s(),
                dataModel_v1.getProvCode(),
                dataModel_v1.getProvId(),
                dataModel_v1.getAreaId(),
                dataModel_v1.getAreaCode(),
                plainSelect,
                threadQueryFroms,
                threadMainTable,
                threadFieldAndQueryFromRelation);
    }

    @Override
    public void setJoins(DataModel_v1 dataModel_v1, PlainSelect plainSelect) {
        //3、构建join（其中包括left 和inner join）
        joins.build(dataModel_v1.getQueryTables(), dataModel_v1.getConditionV1s(), dataModel_v1.getProvCode(), dataModel_v1.getProvId(),dataModel_v1.getAreaCode(),dataModel_v1.getAreaId(), plainSelect,threadQueryFroms.get(),threadMainTable.get());
    }


    @Override
    public void setWhere(DataModel_v1 dataModel_v1, PlainSelect plainSelect) {
        //5、构建where
        where.build(dataModel_v1.getConditionV1s(),threadFieldAndQueryFromRelation.get(), plainSelect);
    }


    @Override
    public void setOrderByElements(DataModel_v1 dataModel_v1, PlainSelect plainSelect) {
        //4、构建排序字段
        orderBy.build(dataModel_v1.getSortFields(), plainSelect,threadFieldAndQueryFromRelation.get());
    }

    @Override
    public void setGroupByElement(DataModel_v1 dataModel_v1, PlainSelect returnSelect) {
        //5、构建聚合
        groupBy.build(dataModel_v1.getQueryFieldV1s(), dataModel_v1.isAggregation() ,returnSelect,threadFieldAndQueryFromRelation.get());
    }

    @Override
    public void setLimit(DataModel_v1 dataModel_v1, PlainSelect plainSelect) {
        //6、构建分页条件
        limit.build(dataModel_v1.getPageInfo(), dataModel_v1.getDialect(), plainSelect);
    }

    @Override
    public void clean() {
        threadFieldAndQueryFromRelation.remove();
        threadMainTable.remove();
        threadQueryFroms.remove();
    }


    /**
     * 构建一个查询总数的PlainSelect
     *
     * @param dataModelV1 数据模型
     * @return net.sf.jsqlparser.statement.select.PlainSelect
     * @Author 李维帅
     * @Date 2022/6/23 17:10
     **/
    public PlainSelect buildCountSelect(DataModel_v1 dataModelV1) {
        Assert.notNull(dataModelV1, "DataModel为空");
        long t = System.currentTimeMillis();
        log.info("构建查询总数SQL入参：{}", JSONObject.toJSONString(dataModelV1));
        //select COUNT(1) from (原始SQL) AS oriSelect
        Expression expression = new Function().withName("COUNT")
                .withParameters(new ExpressionList().addExpressions(buildColumn("1", null)));
        PlainSelect returnSelect = new PlainSelect().addSelectItems(new SelectExpressionItem().withExpression(expression))
                .withFromItem(new SubSelect().withSelectBody(buildPlainSelect(dataModelV1)).withAlias(new Alias("oriSelect")));
        log.info("构建查询总数SQL完成：{}", returnSelect);
        log.info("构建查询总数SQL耗时：{}s", (System.currentTimeMillis() - t) / 1000d);
        return returnSelect;
    }



}
