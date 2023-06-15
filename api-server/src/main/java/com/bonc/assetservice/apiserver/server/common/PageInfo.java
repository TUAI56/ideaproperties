package com.bonc.assetservice.apiserver.server.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName PageInfo
 * @Author: 李维帅
 * @Date: 2022/6/9 17:52
 * @Version 1.0
 **/
@Data
@ApiModel(value="分页对象")
public class PageInfo implements Serializable {

    private static final long serialVersionUID = 4620153152720480782L;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页码", required = true)
    private Integer offset;
    /**
     * 一页数量
     */
    @ApiModelProperty(value = "每页记录数", required = true)
    private Integer limit;

}
