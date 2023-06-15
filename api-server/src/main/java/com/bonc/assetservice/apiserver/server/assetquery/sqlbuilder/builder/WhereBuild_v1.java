package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.Condition_v1;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WhereBuild_v1 extends Builder{

    public void build(List<Condition_v1> conditionV1s, Map<String, Map<Long, String>> fieldAndQueryFromRelation, PlainSelect plainSelect) {
        plainSelect.setWhere(buildWhereExpression(conditionV1s,fieldAndQueryFromRelation));
    }


    /**
     * 构建where表达式
     *
     * @param conditionV1s                查询条件列表
     * @param fieldAndQueryFromRelation 字段与QueryFrom关系
     * @return net.sf.jsqlparser.expression.Expression
     * @Author 李维帅
     * @Date 2022/6/15 17:00
     **/
    private  Expression buildWhereExpression(List<Condition_v1> conditionV1s, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (conditionV1s != null && conditionV1s.size() > 0) {
            // 先构建第一个条件表达式 第二个条件之后的根据逻辑操作符 构建and或or表达式
            Expression expression = buildWhereExpressionByCondition(conditionV1s.get(0), fieldAndQueryFromRelation);
            for (int i = 1; i < conditionV1s.size(); i++) {
                Expression rightExpression = buildWhereExpressionByCondition(conditionV1s.get(i), fieldAndQueryFromRelation);
                expression = Enums.LogicalOperator.OR.equals(conditionV1s.get(i).getLogicalOp()) ?
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
     * @param conditionV1                 查询条件
     * @param fieldAndQueryFromRelation 字段与QueryFrom关系
     * @return net.sf.jsqlparser.expression.Expression
     * @Author 李维帅
     * @Date 2022/6/15 18:49
     **/
    private  Expression buildWhereExpressionByCondition(Condition_v1 conditionV1, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (conditionV1.getSubConditionV1s() != null && conditionV1.getSubConditionV1s().size() > 0) {
            // Parenthesis 是将条件用括号括起来
            return new Parenthesis().withExpression(buildWhereExpression(conditionV1.getSubConditionV1s(), fieldAndQueryFromRelation));
        } else {
            // 当字段别名是ALIAS时， 在字段前拼接表名和账期
            String code = ALIAS.equals(fieldAndQueryFromRelation.get(conditionV1.getQueryFieldV1().getAssetId())) ?
                    conditionV1.getQueryFieldV1().getTableCode() + "_" + conditionV1.getQueryFieldV1().getAcct() + "_" + conditionV1.getQueryFieldV1().getAssetCode()
                    : conditionV1.getQueryFieldV1().getAssetCode();
            return buildCompareExpression(buildColumn(code,fieldAndQueryFromRelation.get(Enums.LabelType.ASSET.getValue()).get(conditionV1.getQueryFieldV1().getAssetId())),
                    conditionV1.getOperand(), conditionV1.getCompareOperator());
        }
    }

}
