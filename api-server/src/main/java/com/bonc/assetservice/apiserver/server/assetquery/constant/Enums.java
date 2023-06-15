package com.bonc.assetservice.apiserver.server.assetquery.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;
import org.omg.PortableInterceptor.INACTIVE;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 枚举集合
 *
 * @author zhangxiaonan
 */
public interface Enums {
    /**
     * 表类型
     */
    enum TableType {
        TABLE, VIEW, DIMENSION, FACT, NODEFINED
    }

    /**
     * 查询对象类型
     */
    enum QueryType {
        ASSET,  //标签对象
        DATASET //应用系统自定义数据集
    }

    /**
     * 集合关联类型
     */
    enum RelationType {
        UNION,      //合并
        INTERSECT,  //交集
        EXCEPT      //差集
    }

    /**
     * 逻辑操作符
     */
    enum LogicalOperator {
        OR, AND
    }

    /**
     * 比较操作符
     */
    enum CompareOperator {
        EQUALS("=", "="),
        NOT_EQUALS("!=", "!="),
        GREATER_THAN(">", ">"),
        GERATER_EQUALS_THAN(">=", ">="),
        LESS_THAN("<", "<"),
        LESS_EQUALS_THAN("<=", "<="),

        IN("IN", "IN"),
        NOT_IN("NOT IN", "NOT IN"),

        LIKE("LIKE", "LIKE"),
        NOT_LIKE("NOT LIKE", "NOT LIKE"),

        NOT_BETWEEN_AND("NOT BETWEEN", "NOT BETWEEN"),
        BETWEEN_AND("BETWEEN", "BETWEEN"),
        CONTAINS("CONTAINS", "LIKE"),
        NOT_CONTAINS("NOT CONTAINS", "NOT LIKE"),
        CASCADE("CASCADE", "CASCADE"),
        /**
         * 区间
         */
//        OPEN_INTERVAL("OPEN_INTERVAL", ">|<"),
//        CLOSED_INTERVAL("CLOSED_INTERVAL", ">=|<="),
//        HALF_OPEN_INTERVAL("HALF_OPEN_INTERVAL", ">|<="),
//        HALF_CLOSED_INTERVAL("HALF_CLOSED_INTERVAL", ">=|<"),
        /**
         * 是否为空
         */
        IS("IS", "IS");

        private String mark;
        private String value;

        CompareOperator(String value, String mark) {
            this.value = value;
            this.mark = mark;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        public String getMark() {
            return mark;
        }

        public static CompareOperator markValueOf(String value) {
            Optional<CompareOperator> opt =
                    Stream.of(values()).filter(op -> op.getValue().equalsIgnoreCase(value)).findFirst();
            if (!opt.isPresent()) {
                throw new IllegalArgumentException("No enum constant of " + CompareOperator.class.getName() +
                        " has mark value of \"" + value + "\". ");
            }
            return opt.get();
        }
    }

    /**
     * 数据服务的数据类型
     */
    enum DataType {
        BOOLEAN, STRING, INTEGER, DECIMAL, DATE, DATETIME, TIMESTAMP, OTHER;

        public static DataType convertFromJdbcDataType(int type) {
            DataType dataType;
            switch (type) {
                case Types.BIT:
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                    dataType = INTEGER;
                    break;
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.DECIMAL:
                    dataType = DECIMAL;
                    break;
                case Types.BOOLEAN:
                    dataType = BOOLEAN;
                    break;
                case Types.DATE:
                    dataType = DATE;
                    break;
                case Types.TIME:
                    dataType = DATETIME;
                    break;
                case Types.TIMESTAMP:
                    dataType = TIMESTAMP;
                    break;
                default:
                    dataType = STRING;
            }
            return dataType;
        }

        public static int convertToJdbcDataType(DataType type) {
            int dataType;
            switch (type) {
                case INTEGER:
                    dataType = Types.INTEGER;
                    break;
                case DECIMAL:
                    dataType = Types.DECIMAL;
                    break;
                case BOOLEAN:
                    dataType = Types.BOOLEAN;
                    break;
                case DATE:
                    dataType = Types.DATE;
                    break;
                case DATETIME:
                    dataType = Types.TIME;
                    break;
                case TIMESTAMP:
                    dataType = Types.TIMESTAMP;
                    break;
                default:
                    dataType = Types.VARCHAR;
            }
            return dataType;
        }

        /**
         * 数据类型转换
         *
         * @param type 平台的数据类型
         * @return 数据类型
         */
        public static DataType convertFromDPDataType(int type) {
            switch (type) {
                case 1:
                    return DataType.INTEGER;
                case 2:
                    return DataType.DECIMAL;
                case 3:
                    return DataType.STRING;
                case 4:
                    return DataType.DATE;
                case 6: // 图片
                case 7: // url
                case 8: // 附件
                default:
                    return DataType.OTHER;
            }
        }
    }

    /**
     * 维度属性类型
     */
    enum AttributeType {
        CODE, NAME, ORDINAL, PCODE, PATH, LEVEL, OTHER
    }

    /**
     * 关联类型
     */
    enum JoinType {
        INNER, LEFT, RIGHT, FULL,
    }

    /**
     * 排序方向
     */
    enum SortType {
        ASC, DESC, NONE
    }

    /**
     * 查询FromItem的角色类型。查询对象Query，在整体查询中扮演的角色。
     */
    enum QueryRole {
        /**
         * 主表或查询
         */
        MAIN,
        /**
         * 从表或查询
         */
        FOLLOWER,
        /**
         * 关联表、中间表或查询
         */
        RELATION,
        /**
         * 翻译表或查询（例如码表）
         */
        INTERPRETER
    }

    /**
     * 数据转换类型
     *
     * @author zhangxiaonan
     */
    enum TransformType {
        ACCUMULATE("com.bonc.vbap.data.transformation.AccumulateInfo"),
        COL2ROW("com.bonc.vbap.data.transformation.Col2RowInfo"),
        CROSS("com.bonc.vbap.data.transformation.CrossInfo"),
        GROUP_TOPN("com.bonc.vbap.data.transformation.GroupTopNInfo"),
        GROUPED("com.bonc.vbap.data.transformation.GroupedInfo"),
        GROUP_SORTED("com.bonc.vbap.data.transformation.GroupSortedInfo"),
        PAGE("com.bonc.vbap.data.transformation.PageInfo"),
        ROW2COL("com.bonc.vbap.data.transformation.Row2ColInfo"),
        ROW2COL_TOTAL_COLUMN("com.bonc.vbap.data.transformation.Row2ColTotalColumnInfo"),
        SORTED("com.bonc.vbap.data.transformation.SortedInfo"),
        TOPN("com.bonc.vbap.data.transformation.TopNInfo"),
        TOTAL_ROW("com.bonc.vbap.data.transformation.TotalRowInfo"),
        FILTER("com.bonc.vbap.data.transformation.FilterTransInfo"),
        SUB_SELECT("com.bonc.vbap.data.transformation.SubSelectTransInfo"),
        RANK("com.bonc.vbap.data.transformation.RankedTransInfo"),
        TREND("com.bonc.vbap.data.transformation.TrendTransInfo");

        private String transformInfoClassName;

        TransformType(String transformInfoClassName) {
            this.transformInfoClassName = transformInfoClassName;
        }

        public String getTransformInfoClassName() {
            return transformInfoClassName;
        }
    }

    /**
     * 异常编码
     *
     * @author zhangjinlong
     */
    enum ExceptionCode {
        /**
         * 未确定类型的异常
         */
        UNTYPED_EXCEPTION(0),
        /**
         * 找不到对象。
         */
        OBJECT_NOT_FOUND(2),
        /**
         * 值无法对应
         */
        UNEXPECTED_VALUE(3),
        /**
         * 不能为null
         */
        CAN_NOT_BE_NULL(4),
        /**
         * 不支持的类型
         */
        UNSUPPORT_TYPE(5),
        /**
         * 执行失败
         */
        EXECUTION_FAILED(6),
        /**
         * 不符合规范
         */
        UNCONFIRMED_STANDARDS(7),
        /**
         * License异常
         */
        LICENSE_EXCEPTION(8),
        /**
         * 没有权限访问
         */
        ACCESS_WITHOUT_PERMISSION(9);

        private int code;

        ExceptionCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 数据集合并类型
     */
    enum CompositeType {
    	INTERSECT("交集"), UNION("去重并集"), UNION_ALL("并集"), EXCEPT("差集"), JOIN("关联");

    	private String compositeName;

    	CompositeType(String compositeName) {
    		this.compositeName = compositeName;
    	}

    	public String getCompositeName() {
    		return compositeName;
    	}
    }

    /***
     * 字段脱敏规则
     */
    enum AssetSafeRule {
        IDCARD("IDCARD"), PHONE("PHONE"), NAME("NAME"), ADDRESS("ADDRESS");

        AssetSafeRule(String safeRuleName) {
            this.safeRuleName = safeRuleName;
        }

        public String getSafeRuleName() {
            return safeRuleName;
        }

        private String safeRuleName;
    }

    /**
     * 数据集账期类型（月账期：MONTH，日账期：DAY, 无账期：NO）
     */
    enum AccountType {
        MONTH("MONTH"), DAY("DAY"), NAME("NAME"), NONE("NO");

        AccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getAccountType() {
            return accountType;
        }

        private String accountType;
    }


    @Getter
    enum SqlBuilderModel {


        /*
            对应到sql上是
            select 标签 from (主表)
            left join 标签表
            where 条件
         */
        WIDE_TABLE_MODE("WIDE_TABLE_MODE","宽表模式"),

        /*
            对应到sql上是
            select 标签 from (主表)
            inner condition
            left join 标签
        */
        INNER_MODE("INNER_MODE","INNER模式");

        private String value;
        private String name;


        SqlBuilderModel(String value, String name) {
            this.name = name;
            this.value = value;
        }
    }


    @Getter
    enum LabelType {



        ASSET("asset","标签"),


        DATASET("dataset","INNER模式");

        private String value;
        private String name;


        LabelType(String value, String name) {
            this.name = name;
            this.value = value;
        }
    }




    /**
     * 数据集账期类型（月账期：MONTH，日账期：DAY, 无账期：NO）
     */
    @Getter
    enum AsyncStatus {
        STATE_PENDING(0, "waiting"), STATE_PROCESSING(1, "handling"), STATE_SUCCESS(2, "success"), STATE_VALIDATE_FAIL
                (-1, "checkfailed"), STATE_PARSE_FAIL(-2, "parsingfailed"), STATE_EXEC_FAIL(-3, "failed");

        AsyncStatus(Integer value, String name) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private int value;

        private static final Map<Integer, String> map = new HashMap<Integer, String>();

        static {
            for(AsyncStatus asyncStatus : AsyncStatus.values()){
                map.put( asyncStatus.getValue(),asyncStatus.getName());

            }
        }
        public static String getNameByValue(Integer value){
            return map.get(value);
        }
    }
}
