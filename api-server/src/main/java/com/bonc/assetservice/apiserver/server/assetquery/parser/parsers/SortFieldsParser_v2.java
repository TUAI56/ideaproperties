package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;


import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v2;
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
public class SortFieldsParser_v2 {

    /**
     * 遍历req中queryList，找到sortType不为空的标签
     * 然后去dataModel中找到对应的QueryField，创建sortFields列表
     *
     * @param queryAssetVOList 查询的请求报文
     * @param dataModel 目标内部数据结构
     * @throws ParseException 解析异常
     */

    public void parse(List<QueryAssetVO_v2> queryAssetVOList, DataModel_v2 dataModel) throws ParseException {

        //获取所有的查询字段
        List<QueryField_v2> queryFields = dataModel.getQueryFields();
        //自定义sort
        List<DataModel_v2.SortField> sortFields = new ArrayList<>();

        //遍历req中的queryList，获取参与排序的标签字段
        for (QueryAssetVO_v2 one : queryAssetVOList) {
            //sortType字段为空，检查下一个标签
            if (StringUtils.isBlank(one.getSortType())) {
                continue;
            }

            //sortType字段不为空，去queryFields匹配对应的标签
            for (QueryField_v2 queryField : queryFields) {
                if ("asset".equals(queryField.getQueryType()) && "asset".equals(one.getQueryType())) {
                    //匹配【req中的标签】和【dataModel中queryFields中的标签】
                    if (one.getAsset().getAssetId().equals(queryField.getAsset().getAssetId().toString())) {
                        DataModel_v2.SortField target = new DataModel_v2.SortField();
                        target.setQueryField(queryField);
                        if ("DESC".equalsIgnoreCase(one.getSortType())) {
                            target.setDesc(true);
                        } else {
                            target.setDesc(false);
                        }
                        sortFields.add(target);
                        break; //跳出内层循环
                    }
                }


                if ("dataset".equals(queryField.getQueryType()) && "dataset".equals(one.getQueryType())) {
                    //匹配【req中的标签】和【dataModel中queryFields中的标签】
                    if (one.getDataset().getDatasetId().equals(queryField.getDataSet().getDatasetId())
                            && one.getDataset().getCode().equals(queryField.getDataSet().getCode())) {
                        DataModel_v2.SortField target = new DataModel_v2.SortField();
                        target.setQueryField(queryField);
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
        }

        if (sortFields.size() > 0) {
            dataModel.setSortFields(sortFields);
        }
    }
}
