package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;


import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v1;
import lombok.extern.slf4j.Slf4j;
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
public class QueryListValidator_v1 extends AbstracValidatorBase<List<QueryAssetVO_v1>> {
    private static final List<String> SORT_TYPES = Arrays.asList("ASC", "DESC");

    private static final List<String> AGGR_TYPES = Arrays.asList("COUNT", "SUM", "MAX", "MIN", "AVG");

    public void validate(List<QueryAssetVO_v1> queryList ) throws ValidateException {



        //遍历校验queryList
        for (QueryAssetVO_v1 one : queryList) {
            //必填已经在入参VO上校验，此处无需处理

            validateValue(one);
        }
    }

    /**
     * 校验字段值
     * @param one 单条querylist
     * @throws ValidateException 校验异常
     */
    private void validateValue(QueryAssetVO_v1 one) throws ValidateException {
        //acct账期格式
        validateAcct(one.getAcct());

        //校验sortType的有效值
        checkValue("sortType", one.getSortType(), false, SORT_TYPES);

        //校验logicalOperator的有效值
        checkValue("aggrType", one.getAggrType(), false, AGGR_TYPES);
    }
}
