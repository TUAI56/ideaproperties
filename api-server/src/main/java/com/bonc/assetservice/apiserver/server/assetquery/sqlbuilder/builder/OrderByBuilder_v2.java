package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class OrderByBuilder_v2 extends Builder{
    public void build(List<DataModel_v2.SortField> sortFields, PlainSelect plainSelect, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        plainSelect.setOrderByElements(buildOrderBys(sortFields, fieldAndQueryFromRelation));
    }


    /**
     * 构建排序表达式
     *
     * @param sortFields                排序字段列表
     * @param fieldAndQueryFromRelation 字段与QueryFrom关系
     * @return java.util.List<net.sf.jsqlparser.statement.select.OrderByElement>
     * @Author 李维帅
     * @Date 2022/6/16 14:34
     **/
    private List<OrderByElement> buildOrderBys(List<DataModel_v2.SortField> sortFields, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        if (sortFields != null && sortFields.size() > 0) {
            List<OrderByElement> orderByElements = new ArrayList<>();
            sortFields.forEach(sortField -> {
                OrderByElement orderByElement = new OrderByElement();
                // 当字段别名是ALIAS时， 在字段前拼接表名和账期
                String code = null;
                QueryField_v2 queryField = sortField.getQueryField();
                Long assetIdOrDataSetId = null;

                if ("asset".equals(queryField.getQueryType())) {
                    code = ALIAS.equals(fieldAndQueryFromRelation.get(queryField.getQueryType()).get(queryField.getAsset().getAssetId())) ?
                            queryField.getTableCode() + "_" + queryField.getAsset().getAcct() + "_" + queryField.getAsset().getAssetCode() : queryField.getAsset().getAssetCode();

                    assetIdOrDataSetId = queryField.getAsset().getAssetId();
                }

                if ("dataset".equals(queryField.getQueryType())) {
                    code = queryField.getDataSet().getCode();
                    assetIdOrDataSetId = Long.valueOf(queryField.getDataSet().getDatasetId());
                }


                orderByElement.setExpression(buildColumn(code, fieldAndQueryFromRelation.get(sortField.getQueryField().getQueryType()).get(assetIdOrDataSetId)));
                orderByElement.setAsc(!sortField.isDesc());
                orderByElements.add(orderByElement);
            });
            return orderByElements;
        }
        return null;
    }
}
