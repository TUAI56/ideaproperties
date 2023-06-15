package com.bonc.assetservice.apiserver.server.assetinfo.vo;

import com.bonc.assetservice.apiserver.server.common.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;


@Data
public class DimTableReqVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String   dimId;

    private String   name;

    private String   code;

    /**
     * 分页
     */
    @Valid
    @ApiModelProperty(value = "分页对象", required = true)
    private PageInfo pageInfo;
}
