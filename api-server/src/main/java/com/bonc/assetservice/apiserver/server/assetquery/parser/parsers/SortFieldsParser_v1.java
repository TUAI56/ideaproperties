package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v1;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/16
 * @Description:
 */

@Slf4j
@Component
public class SortFieldsParser_v1 {

    /**
     * 遍历req中queryList，找到sortType不为空的标签
     * 然后去dataModel中找到对应的QueryField，创建sortFields列表
     *
     * @param queryAssetVOV1List 查询的请求报文
     * @param dataModelV1 目标内部数据结构
     * @throws ParseException 解析异常
     */

    public void parse(List<QueryAssetVO_v1> queryAssetVOV1List, DataModel_v1 dataModelV1) throws ParseException {

        List<QueryField_v1> queryFieldV1s = dataModelV1.getQueryFieldV1s();
        List<DataModel_v1.SortField> sortFields = new ArrayList<>();

        //遍历req中的queryList，获取参与排序的标签字段
        for (QueryAssetVO_v1 one : queryAssetVOV1List) {
            //sortType字段为空，检查下一个标签
            if (StringUtils.isBlank(one.getSortType())) {
                continue;
            }

            //sortType字段不为空，去queryFields匹配对应的标签
            for (QueryField_v1 queryFieldV1 : queryFieldV1s) {
                //匹配【req中的标签】和【dataModel中queryFields中的标签】
                if (one.getAssetId().equals(queryFieldV1.getAssetId().toString())) {
                    DataModel_v1.SortField target = new DataModel_v1.SortField();
                    target.setQueryFieldV1(queryFieldV1);
                    if ("DESC".equalsIgnoreCase(one.getSortType())) {
                        target.setDesc(true);
                    } else {
                        target.setDesc(false);
                    }
                    sortFields.add(target);
                    break; //跳出内层循环
                }
            }
        }

        if (sortFields.size() > 0) {
            dataModelV1.setSortFields(sortFields);
        }
    }
}
