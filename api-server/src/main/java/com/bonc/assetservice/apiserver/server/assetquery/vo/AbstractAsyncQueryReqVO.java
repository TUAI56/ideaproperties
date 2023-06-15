package com.bonc.assetservice.apiserver.server.assetquery.vo;

import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.FtpInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @Author: Ethan.Xing
 * @Date: 2023/3/15
 * @Description:
 */

@Data
@ApiModel(value="异步查询请求的父类")
public abstract class AbstractAsyncQueryReqVO extends AbstractAssetQueryBaseReqVO {

    @Valid
    @NotNull(message = "destFtp不能为空")
    @ApiModelProperty(value = "目标FTP信息", required = true)
    private FtpInfoVO destFtp;
}
