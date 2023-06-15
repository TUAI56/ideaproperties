package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;


import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.ConditionAssetVO_v2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class ConditionListValidator_v2 extends AbstracValidatorBase<List<ConditionAssetVO_v2>> {
    private static final List<String> OPERATORS = Arrays.asList("=", "!=", ">", ">=", "<", "<=",
            "IN", "NOT IN", "LIKE", "NOT LIKE", "BETWEEN", "NOT BETWEEN", "CONTAINS", "NOT CONTAINS", "IS");

    private static final List<String> LOGICAL_OPERATORS = Arrays.asList("AND", "OR");


    @Override
    public void validate( List<ConditionAssetVO_v2> conditionList) throws ValidateException {

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
    private void recursiveValid(List<ConditionAssetVO_v2> conditionList) throws ValidateException {
        //遍历校验conditionList
        for (int i = 0; i < conditionList.size(); i++) {
            ConditionAssetVO_v2 one = conditionList.get(i);

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
    private void validateValue(ConditionAssetVO_v2 one, int conditionOrder) throws ValidateException {

        Boolean required = false;
        //subConditionList为空时，assetId, operator, params不允许为空
        //subConditionList为空时，asset, dataset不允许为空
        if (null == one.getSubConditionList() || one.getSubConditionList().size() == 0) {
            required = true;
        }

        validateConditionTypeAndInfo(one.getConditionType(),one.getAsset(),one.getDataset(),required);

        //数组第一个不传logicalOperator, 其他都需要填
        if (conditionOrder != 0) {
            checkValue("logicalOperator", one.getLogicalOperator(), true, LOGICAL_OPERATORS);
        }
    }



    /**
     * <p>
     * 判断conditionType以及其内容(asset和dataset)
     * </p>
     *
     * @author zhaozesheng
     * @since 2023-01-03 09:57:13
     * @param asset 标签
     * @param conditionType 条件类型
     * @param dataset 自定义数据集
     * @param required 标签和自定义数据集字段是否必填(subConditionList为空时为true)
     */
    private void validateConditionTypeAndInfo(String conditionType, ConditionAssetVO_v2.Asset asset, ConditionAssetVO_v2.Dataset dataset , Boolean required) throws ValidateException {

        //判断conditionType
        if (StringUtils.isEmpty(conditionType)){
            throw new ValidateException("conditionType不允许为空");
        }

        //required 为true 说明subConditionList为空
        if (required &&  "asset".equals(conditionType)){
            //判断asset是否时null
            if (null == asset) throw new ValidateException("asset不允许为空");
            checkValue("assetId", asset.getAssetId(), required, null);
            checkValue("operator", asset.getOperator(), required, OPERATORS);
            checkValue("params", asset.getParams(), required, null);
            //acct账期格式
            validateAcct(asset.getAcct());
        }else if (required && "dataset".equals(conditionType)){
            if (null == dataset) throw new ValidateException("dataset不允许为空");
            checkValue("datasetId", dataset.getDatasetId(), required, null);
            List<ConditionAssetVO_v2.Dataset.Column> columns = dataset.getColumns();
            if (null != columns &&  !columns.isEmpty()){
                for (ConditionAssetVO_v2.Dataset.Column column : columns) {
                    checkValue("code", column.getCode(), required, null);
                    checkValue("operator", column.getOperator(), required, null);
                    checkValue("params", column.getParams(), required, null);
                }

            }

        }


    }
}
