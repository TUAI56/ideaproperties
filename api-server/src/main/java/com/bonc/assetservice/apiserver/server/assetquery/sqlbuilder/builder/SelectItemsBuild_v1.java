package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v1;
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
public class SelectItemsBuild_v1 extends Builder{

    public void build(List<QueryField_v1> queryFieldV1s,
                      boolean aggregation,
                      Map<String,Map<Long, String>> fieldAndQueryFromRelation,
                      boolean isAlias,
                      PlainSelect plainSelect) {

        Assert.notEmpty(queryFieldV1s, "DataModel中QueryField为空");
        List<SelectItem> selectItems = new ArrayList<>();
        queryFieldV1s.forEach(queryField -> {
            // 当字段别名是ALIAS时， 在字段前拼接表名和账期
            String code = ALIAS.equals(fieldAndQueryFromRelation.get("asset").get(queryField.getAssetId())) ?
                    queryField.getTableCode() + "_" + queryField.getAcct() + "_" + queryField.getAssetCode() : queryField.getAssetCode();
            Expression expression = buildColumn(code, fieldAndQueryFromRelation.get("asset").get(queryField.getAssetId()));
            // 汇总时字段添加聚合函数
            if (aggregation && StrUtil.isNotBlank(queryField.getAggrType())) {
                expression = new Function().withName(queryField.getAggrType()).withParameters(new ExpressionList().addExpressions(expression));
            }
            SelectExpressionItem selectItem = new SelectExpressionItem().withExpression(expression);
            // 设置字段别名
            if (isAlias && StrUtil.isNotBlank(queryField.getDisplayName())) {
                selectItem.setAlias(new Alias(QUOTATION_MARK + queryField.getDisplayName() + QUOTATION_MARK));
            }
            selectItems.add(selectItem);
        });
        plainSelect.setSelectItems(selectItems);
    }


}
