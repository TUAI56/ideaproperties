package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;


import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/15
 * @Description: 对请求报文中的queryList进行校验
 */

@Slf4j
@Component
public class QueryListValidator_v2 extends AbstracValidatorBase<List<QueryAssetVO_v2>> {
    private static final List<String> SORT_TYPES = Arrays.asList("ASC", "DESC");

    private static final List<String> AGGR_TYPES = Arrays.asList("COUNT", "SUM", "MAX", "MIN", "AVG");

    public void validate(List<QueryAssetVO_v2> queryList) throws ValidateException {

        //遍历校验queryList
        for (QueryAssetVO_v2 one : queryList) {

            validateValue(one);
        }
    }

    /**
     * 校验字段值
     * @param one 单条querylist
     * @throws ValidateException 校验异常
     */
    private void validateValue(QueryAssetVO_v2 one) throws ValidateException {

        //根据queryType判断asset和dataset参数
        String queryType = one.getQueryType();
        //必填已经在入参VO上校验，此处无需处理
        //判断queryType是否是空
        if (StringUtils.isEmpty(queryType))  throw new ValidateException("QueryType不允许为空");

        if ("asset".equals(queryType)){
            validateAcct(one.getAsset().getAcct());
            validateColum(one.getAsset().getAssetId(),queryType);
        }else if ("dataset".equals(queryType)){
            validateColum(one.getDataset().getDatasetId(),queryType);
        }else{
            throw new ValidateException("QueryType值错误");
        }



        //校验sortType的有效值
        checkValue("sortType", one.getSortType(), false, SORT_TYPES);

        //校验logicalOperator的有效值
        checkValue("aggrType", one.getAggrType(), false, AGGR_TYPES);
    }

    /**
     * <p>
     * 判断要查询的标签ID或者自定义数据集是否为空
     * </p>
     *
     * @author zhaozesheng
     * @since 2023-01-03 17:13:06
     */
    private static void  validateColum(String colum,String queryType) throws ValidateException {
        if (StringUtils.isEmpty(colum)) {
            if("asset".equals(queryType)) {
                throw new ValidateException("assetId不允许为空");
            }
            if("dataset".equals(queryType)) {
                throw new ValidateException("datasetId不允许为空");
            }
        }
    }
}
