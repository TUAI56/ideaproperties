package com.bonc.assetservice.apiserver.server.assetquery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description: [标签取数] 实时查询数据的响应报文
 */

@Data
@ApiModel(value="[标签取数] 实时查询数据的响应报文")
public class SyncAssetQueryRespVO implements Serializable {

    private static final long serialVersionUID = -2665909371233041090L;

    @ApiModelProperty(value = "查询数据总条数", required = true)
    private Integer total;

    @ApiModelProperty(value = "当前页数", required = true)
    private Integer current;

    @ApiModelProperty(value = "查询数据总页数", required = true)
    private Integer pages;

    @ApiModelProperty(value = "每页记录数", required = true)
    private Integer size;

    @ApiModelProperty(value = "返回数据字段")
    private List<ReturnField> fields;

    @ApiModelProperty(value = "返回数据，二维数据")
    private List<Row> rows;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnField {
        @ApiModelProperty(value = "标签id")
        private String assetId;
        @ApiModelProperty(value = "展示名称")
        private String displayName;
        @ApiModelProperty(value = "标签编码")
        private String accsetCode;
    }
    @Data
    public static class Row {
        @ApiModelProperty(value = "标签id")
        private List<Column> Columns;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {
        @ApiModelProperty(value = "标签id")
        private String assetId;

        @ApiModelProperty(value = "标签编码")
        private String accsetCode;

        @ApiModelProperty(value = "值")
        private String value;
    }
}
