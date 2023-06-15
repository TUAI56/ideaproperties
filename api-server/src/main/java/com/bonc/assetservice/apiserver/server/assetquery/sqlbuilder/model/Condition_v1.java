package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model;

import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import lombok.Data;

import java.util.List;


@Data
public class Condition_v1 {

    /**
     * 查询字段
     */
    private QueryField_v1 queryFieldV1;
    /**
     * 比较符号字符串
     */
    private Enums.CompareOperator compareOperator;
    /**
     * 操作数
     */
    private Object operand;
    /**
     * 逻辑操作符
     */
    private Enums.LogicalOperator logicalOp;
    /**
     * 子查询条件
     */
    private List<Condition_v1> subConditionV1s;
}
