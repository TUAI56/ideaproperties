package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;


import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.ConditionAssetVO_v1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/15
 * @Description: 对请求报文中的conditionList进行校验
 */

@Slf4j
@Component
public class ConditionListValidator_v1 extends AbstracValidatorBase< List<ConditionAssetVO_v1>> {
    private static final List<String> OPERATORS = Arrays.asList("=", "!=", ">", ">=", "<", "<=",
            "IN", "NOT IN", "LIKE", "NOT LIKE", "BETWEEN", "NOT BETWEEN", "CONTAINS", "NOT CONTAINS", "IS");

    private static final List<String> LOGICAL_OPERATORS = Arrays.asList("AND", "OR");


    public void validate( List<ConditionAssetVO_v1> conditionList) throws ValidateException {

        if (conditionList == null || conditionList.size() == 0) {
            log.info("conditionList为空，无需校验");
            return;
        }

        //因为可能存在subConditionList，所以递归校验
        recursiveValid(conditionList);
    }

    /**
     * 递归校验conditionList
     * @param conditionList conditionList或者subConditionList
     * @throws ValidateException 校验异常信息
     */
    private void recursiveValid(List<ConditionAssetVO_v1> conditionList) throws ValidateException {
        //遍历校验conditionList
        for (int i = 0; i < conditionList.size(); i++) {
            ConditionAssetVO_v1 one = conditionList.get(i);

            //校验字段的值
            validateValue(one, i);

            //递归校验子查询
            if (one.getSubConditionList() != null && one.getSubConditionList().size() > 0) {
                recursiveValid(one.getSubConditionList());
            }
        }

    }

    /**
     * 校验字段值
     * @param one 单条condition
     * @throws ValidateException 校验异常
     * TODO：使用注解进行枚举值校验
     */
    private void validateValue(ConditionAssetVO_v1 one, int conditionOrder) throws ValidateException {

        Boolean required = false;
        //subConditionList为空时，assetId, operator, params不允许为空
        if (null == one.getSubConditionList() || one.getSubConditionList().size() == 0) {
            required = true;
        }
        checkValue("assetId", one.getAssetId(), required, null);
        checkValue("operator", one.getOperator(), required, OPERATORS);
        checkValue("params", one.getParams(), required, null);

        //acct账期格式
        validateAcct(one.getAcct());

        //数组第一个不传logicalOperator, 其他都需要填
        if (conditionOrder != 0) {
            checkValue("logicalOperator", one.getLogicalOperator(), true, LOGICAL_OPERATORS);
        }
    }
}
