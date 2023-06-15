package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.Arrays;

public abstract class Builder {



    /*
     *  宽表模式
     */
   public  static String MODEL = "WIDE_TABLE_MODE";

    /**
     * 分隔符
     **/
    public final static String SEPARATOR = ",";

    /**
     * 引用使用符号
     **/
    public final static String QUOTATION_MARK = "\"";

    /**
     * 关联查询默认别名
     **/
    public final static String ALIAS = "T";


    /**
     * sqlCondition 默认查询字段user_id
     */
    public final static String USER_ID = "user_id";

    /**
     * sqlCondition 默认查询字段acct
     */
    public final static String ACCT = "acct";







    /**
     * 构建Column对象
     *
     * @param code       字段编码
     * @param tableAlias 字段所属表或子查询别名
     * @return net.sf.jsqlparser.schema.Column
     * @Author 李维帅
     * @Date 2022/6/15 15:38
     **/
    public Column buildColumn(String code, String tableAlias) {
        Table table = StrUtil.isNotBlank(tableAlias) ? new Table(tableAlias) : null;
        return new Column(table, code);
    }


    /**
     * 构建条件表达式
     *
     * @param column          字段
     * @param value           表达式操作值 多个值时逗号分隔
     * @param compareOperator 比较操作符
     * @return net.sf.jsqlparser.expression.Expression 条件表达式
     * @Author 李维帅
     * @Date 2022/6/15 14:23
     **/
    public Expression buildCompareExpression(Column column, Object value, Enums.CompareOperator compareOperator) {

        switch (compareOperator) {
            case EQUALS:
                return new EqualsTo(column, getExpression(value));
            case NOT_EQUALS:
                return new NotEqualsTo(column, getExpression(value));
            case GREATER_THAN:
                return new GreaterThan().withLeftExpression(column).withRightExpression(getExpression(value));
            case GERATER_EQUALS_THAN:
                return new GreaterThanEquals().withLeftExpression(column).withRightExpression(getExpression(value));
            case LESS_THAN:
                return new MinorThan().withLeftExpression(column).withRightExpression(getExpression(value));
            case LESS_EQUALS_THAN:
                return new MinorThanEquals().withLeftExpression(column).withRightExpression(getExpression(value));
            case IN:
                return new InExpression(column, getExpressionList(value));
            case NOT_IN:
                return new InExpression().withNot(true).withLeftExpression(column).withRightItemsList(getExpressionList(value));
            case LIKE:
            case CONTAINS:
                return new LikeExpression().withLeftExpression(column).withRightExpression(new StringValue("%" + value + "%"));
            case NOT_LIKE:
            case NOT_CONTAINS:
                return new LikeExpression().withNot(true).withLeftExpression(column).withRightExpression(new StringValue("%" + value + "%"));
            case BETWEEN_AND:
                ExpressionList expressionListBtnAnd = getExpressionList(value);
                Assert.isTrue(expressionListBtnAnd.getExpressions().size() > 1, "比较操作符为between, 查询值异常：" + value);
                return new Between().withLeftExpression(column).withBetweenExpressionStart(expressionListBtnAnd.getExpressions().get(0))
                        .withBetweenExpressionEnd(expressionListBtnAnd.getExpressions().get(1));
            case NOT_BETWEEN_AND:
                ExpressionList expressionListNotBtnAnd = getExpressionList(value);
                Assert.isTrue(expressionListNotBtnAnd.getExpressions().size() > 1, "比较操作符为not between, 查询值异常：" + value);
                return new Between().withNot(true).withLeftExpression(column).withBetweenExpressionStart(expressionListNotBtnAnd.getExpressions().get(0))
                        .withBetweenExpressionEnd(expressionListNotBtnAnd.getExpressions().get(1));
            case IS:
                boolean isNotNull = "NOTNULL".equalsIgnoreCase(value.toString().replaceAll(" ", ""));
                Assert.isTrue("NULL".equalsIgnoreCase(value.toString()) || isNotNull, "比较操作符为is, 查询值异常：" + value);
                return new IsNullExpression().withNot(isNotNull).withLeftExpression(column);
            default:
                return null;
        }
    }

    private ExpressionList getExpressionList(Object value) {
        ExpressionList expressionList = new ExpressionList();
        Arrays.stream(value.toString().split(",")).forEach(val -> expressionList.addExpressions(new StringValue(val)));
        return expressionList;
    }

    private Expression getExpression(Object value) {
        Expression expression;
        if (value instanceof Integer) {
            expression = new LongValue(value.toString());
        } else if (value instanceof Double) {
            expression = new DoubleValue(value.toString());
        } else {
            expression = new StringValue(value.toString());
        }
        return expression;
    }


    /**
     * <p>
     * 封装sql连接符
     * </p>
     *
     * @param compareOperator 连接符  =  > 等
     * @param left            标签code
     * @param right           标签值
     * @param value           标签值
     * @return SetOperation
     * @author zhaozesheng
     * @since 2022-11-09 10:59:32
     */
    public Expression createComparisonOperator(Enums.CompareOperator compareOperator, Object value, Expression left, Expression right, ExpressionList expressionList) {
        Expression comparisonOperator;
        //判断操作符，封装对应的对象
        switch (compareOperator) {
            case EQUALS:
                return new EqualsTo(left, right);

            case NOT_EQUALS:
                return new NotEqualsTo(left, right);

            case GREATER_THAN:
                return new GreaterThan().withLeftExpression(left).withRightExpression(right);

            case GERATER_EQUALS_THAN:
                return new GreaterThanEquals().withLeftExpression(left).withRightExpression(right);

            case LESS_THAN:
                return new MinorThan().withLeftExpression(left).withRightExpression(right);
            case LESS_EQUALS_THAN:
                return new MinorThanEquals().withLeftExpression(left).withRightExpression(right);
            case IN:
                return new InExpression(left, expressionList);

            case NOT_IN:
                return new InExpression().withNot(true).withLeftExpression(left).withRightItemsList(expressionList);
            case LIKE:
            case CONTAINS:
                return new LikeExpression().withLeftExpression(left).withRightExpression(new StringValue("%" + value + "%"));
            case NOT_LIKE:
            case NOT_CONTAINS:
                return new LikeExpression().withNot(true).withLeftExpression(left).withRightExpression(new StringValue("%" + value + "%"));

            case BETWEEN_AND:
                Assert.isTrue(expressionList.getExpressions().size() > 1, "比较操作符为between, 查询值异常：" + value);
                return new Between().withLeftExpression(left).withBetweenExpressionStart(expressionList.getExpressions().get(0))
                        .withBetweenExpressionEnd(expressionList.getExpressions().get(1));
            case NOT_BETWEEN_AND:
                Assert.isTrue(expressionList.getExpressions().size() > 1, "比较操作符为not between, 查询值异常：" + value);
                return new Between().withNot(true).withLeftExpression(left).withBetweenExpressionStart(expressionList.getExpressions().get(0))
                        .withBetweenExpressionEnd(expressionList.getExpressions().get(1));
                default:
                return null;
        }
    }



}
