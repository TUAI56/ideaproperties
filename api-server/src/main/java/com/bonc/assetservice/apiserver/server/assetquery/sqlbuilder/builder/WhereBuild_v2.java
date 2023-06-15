package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.Condition_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WhereBuild_v2 extends Builder {

    public void build(List<Condition_v2> conditions, Map<String, Map<Long, String>> fieldAndQueryFromRelation, PlainSelect plainSelect) {
        plainSelect.setWhere(buildWhereExpression(conditions, fieldAndQueryFromRelation));
    }


    /**
     * 构建where表达式
     *
     * @param conditions                查询条件列表
     * @param fieldAndQueryFromRelation 字段与QueryFrom关系
     * @return net.sf.jsqlparser.expression.Expression
     * @Author 李维帅
     * @Date 2022/6/15 17:00
     **/
    private Expression buildWhereExpression(List<Condition_v2> conditions, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (conditions != null && conditions.size() > 0) {
            // 先构建第一个条件表达式 第二个条件之后的根据逻辑操作符 构建and或or表达式
            Expression expression = buildWhereExpressionByCondition(conditions.get(0), fieldAndQueryFromRelation);
            for (int i = 1; i < conditions.size(); i++) {
                Expression rightExpression = buildWhereExpressionByCondition(conditions.get(i), fieldAndQueryFromRelation);
                expression = Enums.LogicalOperator.OR.equals(conditions.get(i).getLogicalOp()) ?
                        new OrExpression().withLeftExpression(expression).withRightExpression(rightExpression) :
                        new AndExpression().withLeftExpression(expression).withRightExpression(rightExpression);
            }
            return expression;
        }
        return null;
    }

    /**
     * 构建where表达式
     *
     * @param condition                 查询条件
     * @param fieldAndQueryFromRelation 字段与QueryFrom关系
     * @return net.sf.jsqlparser.expression.Expression
     * @Author 李维帅
     * @Date 2022/6/15 18:49
     **/
    private Expression buildWhereExpressionByCondition(Condition_v2 condition, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (condition.getSubConditions() != null && condition.getSubConditions().size() > 0) {
            // Parenthesis 是将条件用括号括起来
            return new Parenthesis().withExpression(buildWhereExpression(condition.getSubConditions(), fieldAndQueryFromRelation));
        } else {
            // 当字段别名是ALIAS时， 在字段前拼接表名和账期

            if (Enums.LabelType.ASSET.getValue().equals(condition.getConditionType())) {
                String code = ALIAS.equals(fieldAndQueryFromRelation.get(condition.getConditionType()).get(condition.getQueryField().getAsset().getAssetId())) ?
                        condition.getQueryField().getTableCode() + "_" + condition.getQueryField().getAsset().getAcct() + "_" + condition.getQueryField().getAsset().getAssetCode()
                        : condition.getQueryField().getAsset().getAssetCode();
                return buildCompareExpression(buildColumn(code, fieldAndQueryFromRelation.get(Enums.LabelType.ASSET.getValue()).get(condition.getQueryField().getAsset().getAssetId())),
                        condition.getOperand(), condition.getCompareOperator());
            }

            if (Enums.LabelType.DATASET.getValue().equals(condition.getConditionType())) {
                AndExpression andExpression = new AndExpression();
                String datasetTableName = fieldAndQueryFromRelation.get(Enums.LabelType.DATASET.getValue()).get(Long.parseLong(condition.getQueryField().getDataSet().getDatasetId()));

                return getDataSetConditionWhereExecpermission(condition.getQueryField().getDataSet().getColumns(),andExpression,datasetTableName);
            }



            }

            return null;


    }


    /**
     * <p>
     *
     * </p>
     *
     * @param columns 条件
     * @author zhaozesheng
     * @since 2023-01-06 10:12:26
     */
    public Expression getDataSetConditionWhereExecpermission(List<QueryField_v2.Column> columns, Expression expression, String tableName) {


        //从倒数第一个开始
        for (int i = 0; i < columns.size(); i++) {

            String value = columns.get(i).getParams();
            Expression right;
            ExpressionList expressionList = new ExpressionList();
            if (value.toString().contains(SEPARATOR)) {
                Arrays.stream(value.toString().split(",")).forEach(val -> expressionList.addExpressions(new StringValue(val)));
                right = new StringValue(value.toString());
            } else {

                right = new StringValue(value.toString());

            }
            Expression comparisonOperator = createComparisonOperator(columns.get(i).getOperator(), columns.get(i).getParams(), new Column(new Table(tableName),columns.get(i).getCode()), right, expressionList);
            //如果只有一个column
            if (columns.size() == 1) {
                return comparisonOperator;
            } else {
                List<QueryField_v2.Column> columnsChildren = columns;
                columnsChildren.remove(i);

                expression = Enums.LogicalOperator.OR.equals(columns.get(i).getLogicalOperator())
                        ? new OrExpression(comparisonOperator, getDataSetConditionWhereExecpermission(columnsChildren, expression,tableName))
                        : new AndExpression(comparisonOperator, getDataSetConditionWhereExecpermission(columnsChildren, expression,tableName));
                return expression;
            }

        }
        return expression;
    }

}
