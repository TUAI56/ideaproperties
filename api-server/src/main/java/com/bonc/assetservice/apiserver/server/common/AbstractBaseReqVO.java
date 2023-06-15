package com.bonc.assetservice.apiserver.server.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/11/24
 * @Description:
 */

@Data
@ApiModel(value="apiServer的请求消息的父类")
public abstract class AbstractBaseReqVO {

    /**
     * 请求流水号
     */
    @NotBlank(message = "reqId不能为空")
    @ApiModelProperty(value = "应用系统单次请求流水号，同一租户下每次请求不允许重复，最大长度36", required = true)
    private String reqId;

    /**
     * 用于存储请求httpReq中的的天擎appUUId
     */
    private String tqAppUUId;
}
