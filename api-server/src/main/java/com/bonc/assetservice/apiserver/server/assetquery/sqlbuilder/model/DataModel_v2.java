package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据模型
 * @ClassName DataModel_v1
 * @Author 李维帅
 * @Date 2022/6/10 14:40
 * @Version 1.0
 **/
@Data
public class DataModel_v2 extends DataModelBase implements Serializable, Cloneable {

    private static final long serialVersionUID = 524833132522301807L;

    /**
     * 查询字段列表
     */
    private List<QueryField_v2> queryFields;
    /**
     * 查询条件
     */
    private List<Condition_v2> conditions;

    /**
     * 排序字段
     */
    private List<SortField> sortFields;


    @Data
    public static class SortField {
        /**
         * 查询字段
         */
        private QueryField_v2 queryField;
        /**
         * 是否降序。true降序，false升序
         */
        private boolean desc = false;
    }

    @Override
    public DataModel_v2 clone() throws CloneNotSupportedException {
        return (DataModel_v2) super.clone();
    }




}
