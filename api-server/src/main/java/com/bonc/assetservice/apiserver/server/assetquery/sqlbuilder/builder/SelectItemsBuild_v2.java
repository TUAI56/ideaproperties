package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class SelectItemsBuild_v2 extends Builder{

    public void build(List<QueryField_v2> queryFields , boolean aggregation, PlainSelect plainSelect, ThreadLocal<Map<String, Map<Long,String>>> threadFieldAndQueryFromRelation) {

        //获取要查询的字段
//        List<QueryField_v2> queryFieldV1s = dataModel_v2_0.getQueryFieldV1s();
//        boolean aggregation = dataModel_v2_0.isAggregation();

        Assert.notEmpty(queryFields, "DataModel中QueryField为空");
        List<SelectItem> selectItems = new ArrayList<>();
        queryFields.forEach(queryField -> {
            // 当字段别名是ALIAS时， 在字段前拼接表名和账期
            String code = "";
            Long assetIdOrDataSetId = null;
            if (Enums.LabelType.ASSET.getValue().equals(queryField.getQueryType())) {
                code = ALIAS.equals(threadFieldAndQueryFromRelation.get().get(queryField.getQueryType()).get(queryField.getAsset().getAssetId())) ?
                        queryField.getTableCode() + "_" + queryField.getAsset().getAcct() + "_" + queryField.getAsset().getAssetCode() : queryField.getAsset().getAssetCode();

                assetIdOrDataSetId = queryField.getAsset().getAssetId();
            }

            if (Enums.LabelType.DATASET.getValue().equals(queryField.getQueryType())) {
                code = queryField.getDataSet().getCode();
                assetIdOrDataSetId = Long.valueOf(queryField.getDataSet().getDatasetId());
            }


            Expression expression = buildColumn(code, threadFieldAndQueryFromRelation.get().get(queryField.getQueryType()).get(assetIdOrDataSetId));
            // 汇总时字段添加聚合函数
            if (aggregation && StrUtil.isNotBlank(queryField.getAggrType())) {
                expression = new Function().withName(queryField.getAggrType()).withParameters(new ExpressionList().addExpressions(expression));
            }
            SelectExpressionItem selectItem = new SelectExpressionItem().withExpression(expression);
            // 设置字段别名
            if ( StrUtil.isNotBlank(queryField.getDisplayName())) {
                selectItem.setAlias(new Alias(QUOTATION_MARK + queryField.getDisplayName() + QUOTATION_MARK));
            }
            selectItems.add(selectItem);
        });
        plainSelect.setSelectItems(selectItems);
    }


}
