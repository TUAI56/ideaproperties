package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model;


import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryField_v2 {

    /*
    查询类型：
        asset：标签作为查询的数据列
        dataset：自定义数据集中的列作为查询的数据列
     */
    private String queryType;


    /**
     * 展示名称
     */
    private String displayName;
    /**
     * 所属数据表名
     */
    private String tableCode;
    /**
     * 聚合类型 COUNT、SUM、MAX、MIN、AVG
     */
    private String aggrType;


    private Asset asset;


    private DataSet dataSet;


    /**
     * 是否降序。true降序，false升序
     */
    private boolean desc = false;


    /**
     * 排序方式 ASC|DESC
     */
    @ApiModelProperty(value = "排序方式 ASC、DESC")
    private String sortType;




    @Data
    public static class Asset{
        /**
         * 标签ID
         */
        private Long assetId;
        /**
         * ADB中字段编码
         * ADB中，默认以标签编码来创建列名。所以生成查询SQL要使用标签编码
         */
        private String assetCode;
        /**
         * 账期字段
         */
        private String acct;

    }


    @Data
    public static class DataSet{
        //自定义数据集ID
        private String datasetId;

        //queryFile查询字段
        private String code;

        //condition查询字段
        private List<Column> columns;
    }


    @Data
    public static class Column{

        private String code;

        private Enums.CompareOperator operator;

        private String params;

        private Enums.LogicalOperator logicalOperator;
    }






}
