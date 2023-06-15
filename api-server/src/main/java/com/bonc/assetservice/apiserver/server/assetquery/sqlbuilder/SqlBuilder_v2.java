package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder.*;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryFrom;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
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
public class SqlBuilder_v2 extends SqlBuilderBase<DataModel_v2> {


    @Autowired
    private SelectItemsBuild_v2 selectItems;

    @Resource
    private FromItemBuild_v2 fromItem;

    @Resource
    private JoinsBuilder_v2 joins;

    @Resource
    private OrderByBuilder_v2 orderBy;

    @Resource
    private GroupByBuilder_v2 groupBy;

    @Resource
    private LimitBuilder limit;
    @Resource
    private WhereBuild_v2 where;


    private ThreadLocal<Map<String, Map<Long,String>>> threadFieldAndQueryFromRelation = new ThreadLocal<>();
    private ThreadLocal<List<QueryFrom>> threadQueryFroms = new ThreadLocal<>();
    private ThreadLocal<QueryFrom> threadMainTable = new ThreadLocal<>();


    @Override
    public void init(DataModel_v2 dataModel_v2_) {
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
    public void setSelectItems(DataModel_v2 dataModel_v2_,
                               PlainSelect plainSelect) {
        // 1、构建查询字段
        selectItems.build(dataModel_v2_.getQueryFields(),
                dataModel_v2_.isAggregation(),
                plainSelect,
                threadFieldAndQueryFromRelation);
    }

    @Override
    public void setFromItem(DataModel_v2 dataModel_v2_,
                            PlainSelect plainSelect) {
        //2、构建from
        fromItem.build(dataModel_v2_.getConditions(),
                dataModel_v2_.getQueryTables(),
                dataModel_v2_.getQueryFields(),
                dataModel_v2_.getProvCode(),
                dataModel_v2_.getProvId(),
                dataModel_v2_.getAreaCode(),
                dataModel_v2_.getAreaId(),
                plainSelect,
                threadFieldAndQueryFromRelation,
                threadQueryFroms,
                threadMainTable);
    }

    @Override
    public void setJoins(DataModel_v2 dataModel_v2_, PlainSelect plainSelect) {
        //3、构建join（其中包括left 和inner join）
        joins.build(dataModel_v2_.getQueryTables(),
                dataModel_v2_.getConditions(),
                dataModel_v2_.getProvCode(),
                dataModel_v2_.getProvId(),
                dataModel_v2_.getAreaCode(),
                dataModel_v2_.getAreaId(),
                plainSelect,threadQueryFroms.get(),
                threadMainTable.get());
    }

    @Override
    public void setWhere(DataModel_v2 dataModel_v2_, PlainSelect returnSelect) {
        where.build(dataModel_v2_.getConditions(), threadFieldAndQueryFromRelation.get(), returnSelect);
    }

    @Override
    public void setOrderByElements(DataModel_v2 dataModel_v2_, PlainSelect plainSelect) {
        //4、构建排序字段
        orderBy.build(dataModel_v2_.getSortFields(), plainSelect,threadFieldAndQueryFromRelation.get());
    }

    @Override
    public void setGroupByElement(DataModel_v2 dataModel_v2_, PlainSelect plainSelect) {
        //5、构建聚合
        groupBy.build(dataModel_v2_.getQueryFields(), dataModel_v2_.isAggregation(), plainSelect,threadFieldAndQueryFromRelation.get());
    }

    @Override
    public void setLimit(DataModel_v2 dataModel_v2_, PlainSelect plainSelect) {
        //6、构建分页条件
        limit.build(dataModel_v2_.getPageInfo(), dataModel_v2_.getDialect(), plainSelect);
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
     * @param dataModel 数据模型
     * @return net.sf.jsqlparser.statement.select.PlainSelect
     * @Author 李维帅
     * @Date 2022/6/23 17:10
     **/
    public PlainSelect buildCountSelect(DataModel_v2 dataModel) {
        Assert.notNull(dataModel, "DataModel为空");
        long t = System.currentTimeMillis();
        log.info("构建查询总数SQL入参：{}", JSONObject.toJSONString(dataModel));
        //select COUNT(1) from (原始SQL) AS oriSelect
        Expression expression = new Function().withName("COUNT")
                .withParameters(new ExpressionList().addExpressions(buildColumn("1", null)));
        PlainSelect returnSelect = new PlainSelect().addSelectItems(new SelectExpressionItem().withExpression(expression))
                .withFromItem(new SubSelect().withSelectBody(buildPlainSelect(dataModel)).withAlias(new Alias("oriSelect")));
        log.info("构建查询总数SQL完成：{}", returnSelect);
        log.info("构建查询总数SQL耗时：{}s", (System.currentTimeMillis() - t) / 1000d);
        return returnSelect;
    }


}





