package com.bonc.assetservice.apiserver.server.assetquery.vo;

import com.bonc.assetservice.apiserver.server.common.AbstractBaseReqVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="标签查询请求消息的父类")
public abstract class AbstractAssetQueryBaseReqVO extends AbstractBaseReqVO {

    /**
     * 省分ID
     */
    @NotBlank(message = "provId不能为空")
    @ApiModelProperty(value = "省分ID", required = true)
    private String provId;

    /**
     * 地市ID
     */
    @ApiModelProperty(value = "地市ID", required = true)
    private String areaId;

    /**
     * 是否汇聚
     */
    @ApiModelProperty(value = "是否汇总 默认false")
    private Boolean aggregation = false;

    /**
     * 是否计算总数
     */
    @ApiModelProperty(value = "是否查询总数 默认false")
    private Boolean groupCount = false;
}
