package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.*;
import com.bonc.assetservice.metadata.constant.ProvinceEnum;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JoinsBuilder_v1 extends Builder {

    public void build(List<DataModelBase.QueryTable> queryTables, List<Condition_v1> conditionV1s, String proCode, String provId,String areaCode, String areaId, PlainSelect plainSelect, List<QueryFrom> queryFroms, QueryFrom mainTable) {

        //如果是inner join模式
        if (Enums.SqlBuilderModel.INNER_MODE.getValue().equalsIgnoreCase(MODEL)) {
            plainSelect.setJoins(buildJoins4Condition(buildJoins(queryFroms, mainTable), mainTable, queryTables, conditionV1s, proCode, provId,areaCode,areaId));

        } else if (Enums.SqlBuilderModel.WIDE_TABLE_MODE.getValue().equalsIgnoreCase(MODEL)) {
            plainSelect.setJoins(buildJoins(queryFroms, mainTable));
        } else {
            plainSelect.setJoins(null);
        }
    }


    /**
     * 构建join表达式
     *
     * @param queryFroms from列表
     * @param mainTable  域主表
     * @return java.util.List<net.sf.jsqlparser.statement.select.Join>
     * @Author 李维帅
     * @Date 2022/6/16 11:38
     **/
    private List<Join> buildJoins(List<QueryFrom> queryFroms, QueryFrom mainTable) {
        List<Join> joins = new ArrayList<>();
        Column mainRelationColumn = buildColumn(mainTable.getRelationFieldCode(), mainTable.getAlias());
        queryFroms.stream().filter(from -> !from.getAlias().equals(mainTable.getAlias())).forEach(from -> {
            Expression expression;

            expression = new EqualsTo().withLeftExpression(mainRelationColumn)
                    .withRightExpression(buildColumn(from.getRelationFieldCode(), from.getAlias()));
            joins.add(new Join().withInner(QueryFrom.JOIN_TYPE_INNER.equals(from.getJoinType()))
                    .withLeft(QueryFrom.JOIN_TYPE_LEFT.equals(from.getJoinType()))
                    .withRightItem(new SubSelect().withSelectBody(from.getSelect()).withAlias(new Alias(from.getAlias()))).addOnExpression(expression));
        });
        return joins;
    }

    /**
     * <p>
     * 构建joins 将ConditionList放进join里面
     * </p>
     *
     * @author zhaozesheng
     * @since 2022-11-10 16:08:07
     */
    private List<Join> buildJoins4Condition(List<Join> joins, QueryFrom mainTable, List<DataModelBase.QueryTable> queryTables, List<Condition_v1> conditionV1s, String provCode, String provId,String areaCode, String areaId) {
        //设置ConditionList
        if (null != conditionV1s && !conditionV1s.isEmpty()) {
            SelectBody consituionSelectBody = buildConditionsExpression(conditionV1s, queryTables, provCode, provId,areaCode,areaId);
            //获取第一个匹配的参数
            if (null != consituionSelectBody) {
                String queryFiledAndConditionRelation = getQueryFiledAndConditionRelation(queryTables, conditionV1s);
                if (joins.isEmpty()) {
                    joins.add(buildCondition4Join(mainTable, consituionSelectBody, queryFiledAndConditionRelation));
                } else {
                    //将join插入到第一个位置
                    joins.add(0, buildCondition4Join(mainTable, consituionSelectBody, queryFiledAndConditionRelation));
                }

            }
        }
        return joins;
    }


    /**
     * <p>
     * 处理 condition
     * </p>
     *
     * @param conditionV1s condition集合
     * @return SelectBody
     * @author zhaozesheng
     * @since 2022-11-09 15:03:59
     */
    public SelectBody buildConditionsExpression(List<Condition_v1> conditionV1s, List<DataModel_v1.QueryTable> queryTables, String provCode, String provId,String areaCode, String areaId) {

        if (null == conditionV1s || conditionV1s.isEmpty()) return null;

        //最外层是一个selctBodu
        SetOperationList selectBody = new SetOperationList();
        //定义selects
        List<SelectBody> selects = new ArrayList<>();

        //定义brackets
        List<Boolean> brackets = new ArrayList<>();

        //sql之间连接符
        List<SetOperation> operation = new ArrayList<>();

        //遍历conditions
        for (int i = 0; i < conditionV1s.size(); i++) {
            //封装brackets
            brackets.add(true);

            //获取子查询
            List<Condition_v1> subConditionV1s = conditionV1s.get(i).getSubConditionV1s();

            //第一个i 不判断操作符，从第二个开始
            if (i > 0) operation.add(createSetOperation(conditionV1s.get(i)));
            //不为空
            if (null == subConditionV1s || subConditionV1s.isEmpty()) {
                //设置planSelect
                selects.add(buidPlanSelect(conditionV1s.get(i), queryTables, provCode, provId,areaCode,areaId));
            } else {
                //递归子查询
                SelectBody selectBody_sub = buildConditionsExpression(subConditionV1s, queryTables, provCode, provId,areaCode, areaId);
                selects.add(selectBody_sub);
            }
        }
        selectBody.setSelects(selects);
        selectBody.setBrackets(brackets);
        selectBody.setOperations(operation);

        return selectBody;
    }


    private String getQueryFiledAndConditionRelation(List<DataModel_v1.QueryTable> queryTables, List<Condition_v1> conditionV1s) {
        for (DataModel_v1.QueryTable queryTable : queryTables) {
            for (Condition_v1 conditionV1 : conditionV1s) {
                if (null != conditionV1.getSubConditionV1s() && !conditionV1.getSubConditionV1s().isEmpty()) {
                    for (Condition_v1 subcConditionV1 : conditionV1.getSubConditionV1s()) {
                        if (subcConditionV1.getQueryFieldV1().getTableCode().equals(queryTable.getTableCode())) {
                            return queryTable.getRelationFieldCode();
                        }
                    }
                }
                if (conditionV1.getQueryFieldV1().getTableCode().equals(queryTable.getTableCode())) {
                    return queryTable.getRelationFieldCode();
                }
            }
        }
        return null;
    }


    /**
     * <p>
     * 构建查询planSelect
     * </p>
     *
     * @param
     * @return PlainSelect
     * @author zhaozesheng
     * @since 2022-11-09 15:10:52
     */
    private PlainSelect buidPlanSelect(Condition_v1 conditionV1, List<DataModel_v1.QueryTable> queryTables, String provCode, String provId,String areaCode, String areaId) {

        PlainSelect plainSelect = new PlainSelect();

        //默认查询的是userId，所以这里默认写死 user_id
        List<SelectItem> selectItems = new ArrayList<>();
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
        //封装默认查询参数 User_ID
        Expression expression = new Column(getReleAndAcct(conditionV1, queryTables).get(USER_ID));
        selectExpressionItem.setExpression(expression);
        selectItems.add(selectExpressionItem);
        plainSelect.setSelectItems(selectItems);

        //封装fromItem
        List<String> tableName = new ArrayList<>();
        tableName.add(conditionV1.getQueryFieldV1().getTableCode());
        FromItem table = new Table(tableName);
        plainSelect.setFromItem(table);

        //封装where  封装账期
        AndExpression andExpression = new AndExpression();
        EqualsTo leftExpression = new EqualsTo();
        leftExpression.setLeftExpression(new Column(getReleAndAcct(conditionV1, queryTables).get(ACCT)));
        leftExpression.setRightExpression(new StringValue(conditionV1.getQueryFieldV1().getAcct()));
        andExpression.setLeftExpression(leftExpression);

        //封装provId和area
        AndExpression andExpression_prov_area = new AndExpression();

        if (StringUtils.isNotEmpty(areaId)){

            AndExpression expression_prov_area = new AndExpression();
            
            EqualsTo leftExpression_prov = new EqualsTo();
            leftExpression_prov.setLeftExpression(new Column(provCode));
            leftExpression_prov.setRightExpression(new StringValue(provId));
            expression_prov_area.setLeftExpression(leftExpression_prov);
            EqualsTo equalsToExpression_area = new EqualsTo();
            equalsToExpression_area.setLeftExpression(new Column(areaCode));
            equalsToExpression_area.setRightExpression(new StringValue(areaId));
            expression_prov_area.setRightExpression(equalsToExpression_area);
            andExpression_prov_area.setLeftExpression(expression_prov_area);
        }else{
            EqualsTo leftExpression_prov = new EqualsTo();
            leftExpression_prov.setLeftExpression(new Column(provCode));
            leftExpression_prov.setRightExpression(new StringValue(provId));
            andExpression_prov_area.setLeftExpression(leftExpression_prov);
        }





        //封装另外的查询条件
        Enums.CompareOperator compareOperator = conditionV1.getCompareOperator();
        Column left = new Column();
        left.setColumnName(conditionV1.getQueryFieldV1().getAssetCode());
        Object value = conditionV1.getOperand();

        //另外的查询条件赋值
        Expression right;
        ExpressionList expressionList = new ExpressionList();
        if (value.toString().contains(SEPARATOR)) {
            Arrays.stream(value.toString().split(",")).forEach(val -> expressionList.addExpressions(new StringValue(val)));
            right = new StringValue(value.toString());
        } else {
            if (value instanceof Integer) {
                right = new LongValue(value.toString());
            } else if (value instanceof Double) {
                right = new DoubleValue(value.toString());
            } else {
                right = new StringValue(value.toString());
            }
        }
        //判断操作符，封装对应的对象
        Expression comparisonOperator = createComparisonOperator(compareOperator, value, left, right, expressionList);

        andExpression_prov_area.setRightExpression(comparisonOperator);
        andExpression.setRightExpression(provId.equals(ProvinceEnum.Province_JT.getId()) ? comparisonOperator : andExpression_prov_area);
        plainSelect.setWhere(andExpression);

        return plainSelect;
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
    public Expression getDataSetConditionWhereExecpermission(List<QueryField_v2.Column> columns, Expression expression) {


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
            Expression comparisonOperator = createComparisonOperator(columns.get(i).getOperator(), columns.get(i).getParams(), new Column(columns.get(i).getCode()), right, expressionList);
            //如果只有一个column
            if (columns.size() == 1) {
                return comparisonOperator;
            } else {
                List<QueryField_v2.Column> columnsChildren = columns;
                columnsChildren.remove(i);

                expression = Enums.LogicalOperator.OR.equals(columns.get(i).getLogicalOperator()) ? new OrExpression(comparisonOperator, getDataSetConditionWhereExecpermission(columnsChildren, expression)) : new AndExpression(comparisonOperator, getDataSetConditionWhereExecpermission(columnsChildren, expression));
                return expression;
            }

        }
        return expression;

    }

    /**
     * <p>
     * 获取自定义数据集的关联关系字段
     * </p>
     *
     * @author zhaozesheng
     * @since 2023-01-11 17:37:26
     */
    public String getJoinCode(List<DataModelBase.QueryTable> queryTables, List<Condition_v2> conditions) {

//        List<DataModelBase.QueryTable> queryTables = dataModel.getQueryTables();
//        List<Condition_v2> conditions = dataModel.getConditionV1s();

        String code = "";
        for (Condition_v2 condition : conditions) {
            if ("asset".equals(condition.getConditionType())) {
                String tableCode = condition.getQueryField().getTableCode();
                for (DataModelBase.QueryTable queryTable : queryTables) {
                    if (queryTable.getTableCode().equals(tableCode)) {
                        code = queryTable.getRelationFieldCode();
                    }
                }
            }

            if ("subConditionList".equals(condition.getConditionType())) {
                List<Condition_v2> subConditions = condition.getSubConditions();
                for (Condition_v2 subCondition : subConditions) {
                    if ("asset".equals(subCondition.getConditionType())) {
                        String tableCode = subCondition.getQueryField().getTableCode();
                        for (DataModelBase.QueryTable queryTable : queryTables) {
                            if (queryTable.getTableCode().equals(tableCode)) {
                                code = queryTable.getRelationFieldCode();
                            }
                        }
                    }
                }
            }
        }

        return code;


    }


    /**
     * <p>
     * 将Cndition条件封装成join
     * </p>
     *
     * @author zhaozesheng
     * @since 2022-11-10 15:03:27
     */
    private Join buildCondition4Join(QueryFrom mainTable, SelectBody conditionSql, String releation) {

        Join join = new Join();
        join.setInner(true);

        ParenthesisFromItem rightItem = new ParenthesisFromItem();

        SubSelect fromItem = new SubSelect();
        fromItem.setSelectBody(conditionSql);
        fromItem.setUseBrackets(true);
        rightItem.setFromItem(fromItem);
        String conditionAlias = "CONT_" + System.currentTimeMillis();
        rightItem.setAlias(new Alias(conditionAlias, true));

        join.setRightItem(rightItem);

        List<Expression> onExpression = new ArrayList<>();
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(new Table(mainTable.getAlias()), mainTable.getRelationFieldCode()));
        equalsTo.setRightExpression(new Column(new Table(conditionAlias), releation));
        onExpression.add(equalsTo);
        join.setOnExpressions(onExpression);
        return join;

    }


    /**
     * <p>
     * 封装sql连接符
     * </p>
     *
     * @param conditionV1
     * @return SetOperation
     * @author zhaozesheng
     * @since 2022-11-09 10:59:32
     */
    private SetOperation createSetOperation(Condition_v1 conditionV1) {
        Enums.LogicalOperator logicalOp = conditionV1.getLogicalOp();
        switch (logicalOp) {
            case OR:
                return new UnionOp();
            case AND:
                return new IntersectOp();
            default:
                return null;
        }
    }


    /**
     * <p>
     * 根据condition和 queryTable 获取账期字段和用户ID
     * </p>
     *
     * @author zhaozesheng
     * @since 2022-11-11 08:39:33
     */
    private Map<String, String> getReleAndAcct(Condition_v1 conditionV1, List<DataModel_v1.QueryTable> queryTables) {
        //获取condition中查询的表
        String tableCode = conditionV1.getQueryFieldV1().getTableCode();
        //封装返回结果
        Map<String, String> res = new HashMap<>();

        if (StringUtils.isNotEmpty(tableCode)) {
            for (DataModel_v2.QueryTable queryTable : queryTables) {
                if (tableCode.equals(queryTable.getTableCode())) {
                    res.put(USER_ID, queryTable.getRelationFieldCode());
                    res.put(ACCT, queryTable.getAcctFieldCode());
                    return res;
                }
            }
        }
        return res;
    }
}
