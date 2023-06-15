package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryField_v1 {

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
    /**
     * 账期字段
     */
    private String acct;



    /**
     * 是否降序。true降序，false升序
     */
    private boolean desc = false;


    /**
     * 排序方式 ASC|DESC
     */
    @ApiModelProperty(value = "排序方式 ASC、DESC")
    private String sortType;


}
