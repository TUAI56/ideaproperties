package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import cn.hutool.core.util.StrUtil;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GroupByBuilder_v2 extends Builder {

    public void build(List<QueryField_v2> queryFields, boolean aggregation, PlainSelect plainSelect, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        //step5: 设置分组
        plainSelect.setGroupByElement(buildGroupByElement(queryFields,  aggregation,fieldAndQueryFromRelation));

    }


    /**
     * 构建GroupBy表达式
     *
     * @param queryFields               字段列表
     * @param aggregation               是否聚合
     * @param fieldAndQueryFromRelation 字段与QueryFrom关系
     * @return net.sf.jsqlparser.statement.select.GroupByElement
     * @Author 李维帅
     * @Date 2022/6/16 17:34
     **/
    private GroupByElement buildGroupByElement(List<QueryField_v2> queryFields, boolean aggregation,
                                               Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (aggregation) {
            GroupByElement groupByElement = new GroupByElement();
            queryFields.stream().filter(queryField -> StrUtil.isNotBlank(queryField.getAggrType()))
                    .forEach(queryField -> {

                        if ("asset".equals(queryField.getQueryType())) {
                            // 当字段别名是ALIAS时， 在字段前拼接表名和账期
                            String code = ALIAS.equals(fieldAndQueryFromRelation.get(queryField.getQueryType()).get(queryField.getAsset().getAssetId())) ?
                                    queryField.getTableCode() + "_" + queryField.getAsset().getAcct() + "_" + queryField.getAsset().getAssetCode() : queryField.getAsset().getAssetCode();
                            groupByElement.addGroupByExpressions(
                                    buildColumn(code, fieldAndQueryFromRelation.get(queryField.getQueryType()).get(queryField.getAsset().getAssetId())));
                        }


                        if ("dataset".equals(queryField.getQueryType())) {
                            String code = queryField.getDataSet().getCode();
                            groupByElement.addGroupByExpressions(
                                    buildColumn(code, fieldAndQueryFromRelation.get(queryField.getQueryType()).get(Long.valueOf(queryField.getDataSet().getDatasetId()))));
                        }

                    });
            return groupByElement;
        }
        return null;
    }
}
