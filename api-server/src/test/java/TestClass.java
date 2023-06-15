import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/10/21
 * @Description:
 */


@Slf4j
public class TestClass {

    /*@Test
    public void test() {
        Table table = new Table("test");
        Select select = SelectUtils.buildSelectFromTable(table);
        System.err.println(select); // SELECT * FROM test

        // 指定列查询
        Select buildSelectFromTableAndExpressions = SelectUtils.buildSelectFromTableAndExpressions(new Table("test"), new Column("col1"), new Column("col2"));
        System.err.println(buildSelectFromTableAndExpressions); // SELECT col1, col2 FROM test

        // WHERE =
        EqualsTo equalsTo = new EqualsTo(); // 等于表达式
        equalsTo.setLeftExpression(new Column(table, "user_id")); // 设置表达式左边值
        equalsTo.setRightExpression(new StringValue("123456"));// 设置表达式右边值
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody(); // 转换为更细化的Select对象
        plainSelect.setWhere(equalsTo);
        System.err.println(plainSelect);//  SELECT * FROM test WHERE test.user_id = '123456'

        // WHERE  != <>
        NotEqualsTo notEqualsTo = new NotEqualsTo();
        notEqualsTo.setLeftExpression(new Column(table, "user_id")); // 设置表达式左边值
        notEqualsTo.setRightExpression(new StringValue("123456"));// 设置表达式右边值
        PlainSelect plainSelectNot = (PlainSelect) select.getSelectBody();
        plainSelectNot.setWhere(notEqualsTo);
        System.err.println(plainSelectNot);//  SELECT * FROM test WHERE test.user_id <> '123456'

        // 其他运算符, 参考上面代码添加表达式即可
        GreaterThan gt = new GreaterThan(); // ">"
        GreaterThanEquals geq = new GreaterThanEquals(); // ">="
        MinorThan mt = new MinorThan(); // "<"
        MinorThanEquals leq = new MinorThanEquals();// "<="
        IsNullExpression isNull = new IsNullExpression(); // "is null"
        isNull.setNot(true);// "is not null"
        LikeExpression nlike = new LikeExpression();
        nlike.setNot(true); // "not like"
        Between bt = new Between();
        bt.setNot(true);// "not between"

        // WHERE LIKE
        LikeExpression likeExpression = new LikeExpression(); // 创建Like表达式对象
        likeExpression.setLeftExpression(new Column("username")); // 表达式左边
        likeExpression.setRightExpression(new StringValue("张%")); // 右边表达式
        PlainSelect plainSelectLike = (PlainSelect) select.getSelectBody();
        plainSelectLike.setWhere(likeExpression);
        System.err.println(plainSelectLike); // SELECT * FROM test WHERE username LIKE '张%'

        // WHERE IN
        Set<String> deptIds = Sets.newLinkedHashSet(); // 创建IN范围的元素集合
        deptIds.add("0001");
        deptIds.add("0002");
        ItemsList itemsList = new ExpressionList(deptIds.stream().map(StringValue::new).collect(Collectors.toList())); // 把集合转变为JSQLParser需要的元素列表
        InExpression inExpression = new InExpression(new Column("dept_id "), itemsList); // 创建IN表达式对象，传入列名及IN范围列表
        PlainSelect plainSelectIn = (PlainSelect) select.getSelectBody();
        plainSelectIn.setWhere(inExpression);
        System.err.println(plainSelectIn); // SELECT * FROM test WHERE dept_id  IN ('0001', '0002')

        // WHERE BETWEEN AND
        Between between = new Between();
        between.setBetweenExpressionStart(new LongValue(18)); // 设置起点值
        between.setBetweenExpressionEnd(new LongValue(30)); // 设置终点值
        between.setLeftExpression(new Column("age")); // 设置左边的表达式，一般为列
        PlainSelect plainSelectBetween = (PlainSelect) select.getSelectBody();
        plainSelectBetween.setWhere(between);
        System.err.println(plainSelectBetween); // SELECT * FROM test WHERE age BETWEEN 18 AND 30

        //  WHERE AND 多个条件结合,都需要成立
        AndExpression andExpression = new AndExpression(); // AND 表达式
        andExpression.setLeftExpression(equalsTo); // AND 左边表达式
        andExpression.setRightExpression(between);  // AND 右边表达式
        PlainSelect plainSelectAnd = (PlainSelect) select.getSelectBody();
        plainSelectAnd.setWhere(andExpression);
        System.err.println(plainSelectAnd); //  SELECT * FROM test WHERE test.user_id = '123456' AND age BETWEEN 18 AND 30

        //  WHERE OR 多个条件满足一个条件成立返回
        OrExpression orExpression = new OrExpression();// OR 表达式
        orExpression.setLeftExpression(equalsTo); // OR 左边表达式
        orExpression.setRightExpression(between);  // OR 右边表达式
        PlainSelect plainSelectOr = (PlainSelect) select.getSelectBody();
        plainSelectOr.setWhere(orExpression);
        System.err.println(plainSelectOr); // SELECT * FROM test WHERE test.user_id = '123456' OR age BETWEEN 18 AND 30

        // ORDER BY 排序
        OrderByElement orderByElement = new OrderByElement(); // 创建排序对象
        orderByElement.isAsc(); //  设置升序排列 从小到大
        orderByElement.setExpression(new Column("col01")); // 设置排序字段
        PlainSelect plainSelectOrderBy = (PlainSelect) select.getSelectBody();
        plainSelectOrderBy.addOrderByElements(orderByElement);
        System.err.println(plainSelectOrderBy); // SELECT * FROM test WHERE test.user_id = '123456' OR age BETWEEN 18 AND 30 ORDER BY col01
    }




    *//**
     * 多表SQL查询
     * JOIN / INNER JOIN: 如果表中有至少一个匹配，则返回行
     * LEFT JOIN: 即使右表中没有匹配，也从左表返回所有的行
     * RIGHT JOIN: 即使左表中没有匹配，也从右表返回所有的行
     * FULL JOIN: 只要其中一个表中存在匹配，就返回行
     *//*
    @Test
    public void testSelectManyTable() {
        Table t1 = new Table("tab1").withAlias(new Alias("t1").withUseAs(true)); // 表1
        Table t2 = new Table("tab2").withAlias(new Alias("t2", false)); // 表2
        PlainSelect plainSelect = new PlainSelect().addSelectItems(new AllColumns()).withFromItem(t1); // SELECT * FROM tab1 AS t1

        // JOIN ON 如果表中有至少一个匹配，则返回行
        Join join = new Join(); // 创建Join对象
        join.withRightItem(t2); // 添加Join的表 JOIN t2 =>JOIN tab2 t2
        EqualsTo equalsTo = new EqualsTo(); // 添加 = 条件表达式  t1.user_id  = t2.user_id
        equalsTo.setLeftExpression(new Column(t1, "user_id "));
        equalsTo.setRightExpression(new Column(t2, "user_id "));
        join.withOnExpression(equalsTo);// 添加ON
        plainSelect.addJoins(join);
        System.err.println(plainSelect); // SELECT * FROM tab1 AS t1 JOIN tab2 t2 ON t1.user_id  = t2.user_id

        // 设置join参数可实现其他类型join
        // join.setLeft(true); LEFT JOIN
        // join.setRight(true);  RIGHT JOIN
        // join.setFull(true); FULL JOIN
        // join.setInner(true);
    }



    *//**
     * SQL 函数
     * SELECT function(列) FROM 表
     *//*
    @Test
    public void testFun() throws JSQLParserException {
        Table t1 = new Table("tab1").withAlias(new Alias("t1").withUseAs(true)); // 表1
        PlainSelect plainSelect = new PlainSelect();
        plainSelect.setFromItem(t1); // 设置FROM t1= >  SELECT  FROM tab1 AS t1
        List<SelectItem> selectItemList = new ArrayList<>(); // 查询元素集合
        SelectExpressionItem selectExpressionItem001 = new SelectExpressionItem(); // 元素1表达式
        selectExpressionItem001.setExpression(new Column(t1,"col001"));
        SelectExpressionItem selectExpressionItem002 = new SelectExpressionItem(); // 元素2表达式
        selectExpressionItem002.setExpression(new Column(t1,"col002"));
        selectItemList.add(0, selectExpressionItem001); // 添加入队
        selectItemList.add(1, selectExpressionItem002); // 添加入队

        // COUNT
        SelectExpressionItem selectExpressionItemCount = new SelectExpressionItem(); // 创建函数元素表达式
        selectExpressionItemCount.setAlias(new Alias("count")); // 设置别名
        Function function = new Function(); // 创建函数对象  Function extends ASTNodeAccessImpl implements Expression
        function.setName("COUNT"); // 设置函数名
        ExpressionList expressionListCount = new ExpressionList(); // 创建参数表达式
        expressionListCount.setExpressions(Collections.singletonList(new Column(t1, "id")));
        function.setParameters(expressionListCount); // 设置参数
        selectExpressionItemCount.setExpression(function);
        selectItemList.add(2,selectExpressionItemCount);

        plainSelect.setSelectItems(selectItemList); // 添加查询元素集合入select对象
        System.err.println(plainSelect); // SELECT t1.col001, t1.col002, COUNT(t1.id) AS count FROM tab1 AS t1
    }


    *//**
     * SQL 解析
     *
     * @throws JSQLParserException
     *//*
    @Test
    public void testSelectParser() throws JSQLParserException {
        String SQL002 = "SELECT t1.a , t1.b  FROM tab1 AS t1 JOIN tab2 t2 ON t1.user_id  = t2.user_id";   // 多表SQL

        // 1.解析表名
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(SQL002)); // 解析SQL为Statement对象
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder(); // 创建表名发现者对象
        List<String> tableNameList = tablesNamesFinder.getTableList(statement); // 获取到表名列表
        if (!CollectionUtils.isEmpty(tableNameList)) {
            tableNameList.forEach(System.err::println); // 循环打印解析到的表名 tab1 tab2
        }
        // 2.解析查询元素=》 列，函数等
        Select select = (Select) CCJSqlParserUtil.parse(SQL002);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        selectItems.forEach(System.err::println); // t1.a , t1.b

        // 3.解析WHERE条件
        String SQL_WHERE = "SELECT *  FROM tableName WHERE ID = 8";
        PlainSelect plainSelectWhere = (PlainSelect) ((Select) CCJSqlParserUtil.parse(SQL_WHERE)).getSelectBody();
        EqualsTo equalsTo = (EqualsTo) plainSelectWhere.getWhere();
        Expression leftExpression = equalsTo.getLeftExpression();
        Expression rightExpression = equalsTo.getRightExpression();
        System.err.println(leftExpression); // ID
        System.err.println(rightExpression); // 8

        // 4.解析Join
        List<Join> joins = plainSelect.getJoins();
        joins.forEach(e -> {
            Expression onExpression = e.getOnExpression();
            System.err.println(onExpression); // 获取ON 表达式 t1.user_id = t2.user_id
        });

        // 5.解析IN
        String SQL_IN = "SELECT *  FROM tableName WHERE ID IN (8,9,10)";
        PlainSelect plainSelectIn = (PlainSelect) ((Select) CCJSqlParserUtil.parse(SQL_IN)).getSelectBody();
        InExpression inExpression = (InExpression) plainSelectIn.getWhere();
        ItemsList rightItemsList = inExpression.getRightItemsList();
        System.err.println(rightItemsList); // (8, 9, 10)

        // plainSelect.getDistinct();
        // plainSelect.getFetch();
        // plainSelect.getFirst();
        // plainSelect.getGroupBy();
        // .......
    }

    @Test
    public void parse() throws JSQLParserException {
//        String SQL002 = "select user_id from A WHERE acct = '202210' AND a2 = 'a22'" +
//                " INTERSECT" +
//                " ( SELECT user_id FROM B WHERE acct = '202210' AND b1 = 'b12'" +
//                " UNION" +
//                " SELECT user_id FROM C WHERE acct = '202210' AND c1 = 'c12')";

        String SQL002 = "SELECT\n" +
                "\tdomain.device_number,\n" +
                "\tt1.a1 \n" +
                "FROM\n" +
                "\t( SELECT user_id, device_number FROM domain WHERE acct = '202210' ) AS domain\n" +
                "\tINNER JOIN ( SELECT user_id FROM A WHERE acct = '202209' AND a1 <> 'a14' ) AS condi ON domain.user_id = condi.user_id\n" +
                "\tLEFT JOIN ( SELECT user_id, a1 FROM A WHERE acct = '202210' ) AS t1 ON domain.user_id = t1.user_id";
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(SQL002)); // 解析SQL为Statement对象

        log.info(statement.toString());
    }*/


//    @Test
//    public void testSqlCondition(){
//
//
//
//
//
//        SqlBuilder sqlBuilder = new SqlBuilder();
//
//        List<DataModel.Condition> conditions = new ArrayList<>();
//
//        DataModel.Condition condition = new DataModel.Condition();
//        DataModel.QueryField queryField = new DataModel.QueryField();
//        queryField.setAcct("202210");
//        queryField.setAssetId(123L);
//        queryField.setAssetCode("标签a1");
//        queryField.setTableCode("A表");
//        condition.setQueryField(queryField);
//
//        condition.setCompareOperator(Enums.CompareOperator.LIKE);
//        condition.setLogicalOp(Enums.LogicalOperator.AND);
//        condition.setOperand("值a1");
//        conditions.add(condition);
//
//
//
//
//
//
//        DataModel.Condition condition2 = new DataModel.Condition();
//        DataModel.QueryField queryField2 = new DataModel.QueryField();
//        queryField2.setAcct("202210");
//        queryField2.setAssetId(123L);
//        queryField2.setAssetCode("标签b2");
//        queryField2.setTableCode("B表");
//        condition2.setQueryField(queryField2);
//
//        condition2.setCompareOperator(Enums.CompareOperator.GERATER_EQUALS_THAN);
//        condition2.setLogicalOp(Enums.LogicalOperator.OR);
//        condition2.setOperand("值b2");
//        conditions.add(condition2);
//
//
//
//
//        DataModel.Condition condition3 = new DataModel.Condition();
//
//        List<DataModel.Condition> sub = new ArrayList<>();
//        DataModel.Condition conditionSub1 = new DataModel.Condition();
//        DataModel.QueryField queryFieldSub1 = new DataModel.QueryField();
//        queryFieldSub1.setAcct("202210");
//        queryFieldSub1.setAssetId(123L);
//        queryFieldSub1.setAssetCode("子标签a1");
//        queryFieldSub1.setTableCode("子A表");
//        conditionSub1.setQueryField(queryField);
//
//        conditionSub1.setCompareOperator(Enums.CompareOperator.IN);
//        conditionSub1.setLogicalOp(Enums.LogicalOperator.AND);
//
//        conditionSub1.setOperand("值子a1,值子a1,值子a1");
//
//
//        DataModel.Condition conditionSub2 = new DataModel.Condition();
//        DataModel.QueryField queryFieldSub2 = new DataModel.QueryField();
//        queryFieldSub2.setAcct("202210");
//        queryFieldSub2.setAssetId(123L);
//        queryFieldSub2.setAssetCode("子标签b1");
//        queryFieldSub2.setTableCode("B表");
//        conditionSub2.setQueryField(queryField2);
//
//        conditionSub2.setCompareOperator(Enums.CompareOperator.EQUALS);
//        conditionSub2.setLogicalOp(Enums.LogicalOperator.OR);
//        conditionSub2.setOperand("值子b1");
//
//
//        sub.add(conditionSub1);
//        sub.add(conditionSub2);
//        condition3.setSubConditions(sub);
//
//        condition3.setLogicalOp(Enums.LogicalOperator.AND);
//        conditions.add(condition3);
//
//        SelectBody selectBody = sqlBuilder.buildConditionsExpression(conditions);
//        System.out.println(selectBody.toString());
//    }

//    @Test
//    public void testDataModel(){
//
//        DataModel dataModel = new DataModel();
//        List<DataModel.QueryField> queryFields = new ArrayList<>();
//        DataModel.QueryField queryField_q = new DataModel.QueryField();
//        queryField_q.setAcct("202210");
//        queryField_q.setAssetId(123L);
//        queryField_q.setAssetCode("标签a1");
//        queryField_q.setTableCode("A表");
//
//        queryFields.add(queryField_q);
//        dataModel.setQueryFields(queryFields);
//
//
//
//        //构建condition
//
//        SqlBuilder sqlBuilder = new SqlBuilder();
//        List<DataModel.Condition> conditions = new ArrayList<>();
//
//        DataModel.Condition condition = new DataModel.Condition();
//        DataModel.QueryField queryField = new DataModel.QueryField();
//        queryField.setAcct("202210");
//        queryField.setAssetId(123L);
//        queryField.setAssetCode("con标签");
//        queryField.setTableCode("con表");
//        condition.setQueryField(queryField);
//
//        condition.setCompareOperator(Enums.CompareOperator.EQUALS);
//        condition.setLogicalOp(Enums.LogicalOperator.AND);
//        condition.setOperand("con表值");
//        conditions.add(condition);
//
//
//
//
//        dataModel.setConditions(conditions);
//
//        List<DataModel.QueryTable> queryTables = new ArrayList<>();
//
//        DataModel.QueryTable queryTable = new DataModel.QueryTable();
//        queryTable.setAcct("202210");
//        queryTable.setAcctFieldCode("acct");
//        queryTable.setMain(true);
//        queryTable.setRelationFieldCode("user_id");
//        queryTable.setTableCode("A表");
//        queryTables.add(queryTable);
//
//
//        DataModel.QueryTable queryTableCondition = new DataModel.QueryTable();
//        queryTableCondition.setAcct("202210");
//        queryTableCondition.setAcctFieldCode("acct");
//        queryTableCondition.setMain(true);
//        queryTableCondition.setRelationFieldCode("user_id");
//        queryTableCondition.setTableCode("con表");
//        queryTables.add(queryTableCondition);
//
//
//        dataModel.setQueryTables(queryTables);
//
//        SelectBody selectBody = SqlBuilder.buildPlainSelect(dataModel);
//        System.out.println(selectBody.toString());
//    }


   /* @Test
    public void dataxTest(){
        int exitCode = 0;

        *//**
         * classPath 为刚才打包的目录 bin的上一级目录 我的dataX源码目录为E:\study\code\DataX
         * jobFilePath:要放在job目录中和job.json同级
         *  System.setProperty("datax.home", classPath); 必须要设置datax.home这个属性不然会找不到执行的入口
         *//*
        String id = "122343233443";
        String querySql ="select * from tablea";
        String filetype = "txt";
        String hdfsUri = "hdfs://172.17.1.121:9000/adb/hdfs_output_test_csv_data";
        String separateChar = ",";

        String ctSql = "create table wb_"+id + " as " + querySql + " where 1=2 " + "ENGINE='HDFS'\n" +
                "TABLE_PROPERTIES='{\n" +
                "    \"format\":\""+ filetype +"\",\n" +
                "    \"delimiter\":\""+separateChar +"\",\n" +
                "    \"hdfs_url\":\""+ hdfsUri +"\"\n" +
                "}'";
        String classPath ="D:\\workspace\\DataX\\target\\datax\\datax";

        String jobFilePath="D:\\adb2ftp-test.json";

        System.setProperty("datax.home", classPath);


        String[] dataXArgs = {"-job", jobFilePath, "-mode", "standalone", "-jobid", "-1"};
        try {
              Engine.entry(dataXArgs);

        }catch (DataXException e){
            e.printStackTrace();
        }
        catch (Throwable throwable) {
            *//**
             * 拿到异常信息入库.
             *//*
            log.error("\n\n经DataX智能分析,该任务最可能的错误原因::\n" + ExceptionTracker.trace(throwable));

        }
    }*/
    @Test
    public void testJar()  {
        String[] arguments4 ={  "-h","10.177.64.78", "-u","esd", "-p","oo0ucxX0JD2WD1vpZPq6", "-P","3000", "-D","dc_hh_ser_prod_000_tag", "--lineSeparator","\\n", "--delimiter",",","--encoding","UTF-8", "--nullAsQuotes","false", "--dataFile","C:\\Users\\29134\\Desktop\\test-aaa.txt", "--tableName","test", "--concurrency", "40", "--batchSize", "2048", "--printRowCount", "false", "--maxConcurrentNumOfFilesToImport", "64", "--windowSize", "128", "--failureSqlPrintLengthLimit", "2000", "--disableInsertOnlyPrintSql", "false", "--skipHeader", "false", "--windowSize", "128", "--encryptPassword", "false", "--escapeSlashAndSingleQuote", "true", "--ignoreErrors", "false", "--printErrorSql", "true", "--printErrorStackTrace", "true", "--insertWithColumnNames", "true"};

        //ImportTool.run(arguments4);
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        //log.info(jarFile.getParentFile().toString());




    }



}
