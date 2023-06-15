package com.bonc.assetservice.apiserver.server.assetinfo.vo;

import com.bonc.assetservice.metadata.appmodel.DimTableModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/12/9
 * @Description:
 */

@Data
@ApiModel(value="[标签信息] 查询码表详情响应报文")
public class DimTableRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "查询数据总条数", required = true)
    private Integer total;

    @ApiModelProperty(value = "当前页数", required = true)
    private Integer current;

    @ApiModelProperty(value = "查询数据总页数", required = true)
    private Integer pages;

    @ApiModelProperty(value = "每页记录数", required = true)
    private Integer size;

    @ApiModelProperty(value = "返回数据字段")
    private List<DimTableModel> records;
}
