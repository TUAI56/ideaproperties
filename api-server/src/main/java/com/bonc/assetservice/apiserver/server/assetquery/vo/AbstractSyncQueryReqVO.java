package com.bonc.assetservice.apiserver.server.assetquery.vo;

import com.bonc.assetservice.apiserver.server.common.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

/**
 * @Author: Ethan.Xing
 * @Date: 2023/3/15
 * @Description:
 */

@Data
@ApiModel(value="实时查询请求的父类")
public abstract class AbstractSyncQueryReqVO extends AbstractAssetQueryBaseReqVO {

    /**
     * 分页
     */
    @Valid
    @ApiModelProperty(value = "分页对象", required = true)
    private PageInfo pageInfo;
}
