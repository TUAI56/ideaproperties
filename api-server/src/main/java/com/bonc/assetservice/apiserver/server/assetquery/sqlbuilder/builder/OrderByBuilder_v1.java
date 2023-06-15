package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v1;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrderByBuilder_v1 extends Builder{
    public void build(List<DataModel_v1.SortField> sortFields, PlainSelect plainSelect, Map<String, Map<Long, String>> fieldAndQueryFromRelation) {
        plainSelect.setOrderByElements(buildOrderBys(sortFields,fieldAndQueryFromRelation));
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
    private List<OrderByElement> buildOrderBys(List<DataModel_v1.SortField> sortFields, Map<String,Map<Long, String>> fieldAndQueryFromRelation) {
        if (sortFields != null && sortFields.size() > 0) {
            List<OrderByElement> orderByElements = new ArrayList<>();
            sortFields.forEach(sortField -> {
                OrderByElement orderByElement = new OrderByElement();
                // 当字段别名是ALIAS时， 在字段前拼接表名和账期
                String code = ALIAS.equals(fieldAndQueryFromRelation.get("asset").get(sortField.getQueryFieldV1().getAssetId())) ?
                        sortField.getQueryFieldV1().getTableCode() + "_" + sortField.getQueryFieldV1().getAcct() + "_" + sortField.getQueryFieldV1().getAssetCode()
                        : sortField.getQueryFieldV1().getAssetCode();
                orderByElement.setExpression(buildColumn(code, fieldAndQueryFromRelation.get("asset").get(sortField.getQueryFieldV1().getAssetId())));
                orderByElement.setAsc(!sortField.isDesc());
                orderByElements.add(orderByElement);
            });
            return orderByElements;
        }
        return null;
    }
}
