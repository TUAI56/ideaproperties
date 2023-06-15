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
public class DataModel_v1 extends DataModelBase implements Serializable, Cloneable {

    private static final long serialVersionUID = 524833132522301807L;

    /**
     * 查询字段列表
     */
    private List<QueryField_v1> queryFieldV1s;
    /**
     * 查询条件
     */
    private List<Condition_v1> conditionV1s;

    /**
     * 排序字段
     */
    private List<SortField> sortFields;



//    /**
//     * @Author: Ethan.Xing
//     * @Date: 2022/6/23
//     * @Description:
//     */
//
//    @Data
//    public static class RelationModel {
//
//        /**
//         * 别名
//         */
//        private String alias;
//
//        /**
//         * 关联字段编码
//         */
//        private String relationFieldCode;
//
//        /**
//         * 关联查询对象列表
//         */
//        List<Relation> relations;
//
//        @Data
//        public static class Relation {
//            /**
//             * 关联类型 MERGE合并、UNITE交集、EXCEPT剔除
//             */
//            private String joinType;
//            /**
//             * 子查询，对应接口中的subQuery
//             */
//            private DataModel_v1 dataModel;
//        }
//
//    }


    @Data
    public static class SortField {
        /**
         * 查询字段
         */
        private QueryField_v1 queryFieldV1;
        /**
         * 是否降序。true降序，false升序
         */
        private boolean desc = false;
    }

    @Override
    public DataModel_v1 clone() throws CloneNotSupportedException {
        return (DataModel_v1) super.clone();
    }
}
