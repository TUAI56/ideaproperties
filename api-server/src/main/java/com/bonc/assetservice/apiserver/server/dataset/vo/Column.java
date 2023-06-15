package com.bonc.assetservice.apiserver.server.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column {
    @ApiModelProperty(value = "字段名")
    @NotBlank(message = "字段名为空")
    private String name;

    @ApiModelProperty(value = "字段编码")
    @NotBlank(message = "字段编码为空")
    private String code;

    @ApiModelProperty(value = "支持的字段类型，默认值STRING。当前支持的类型如下：字符串：STRING ")
    private String type;
    @ApiModelProperty(value = "字段长度")
    @NotNull(message = "字段长度为空")
    private int length;
    @ApiModelProperty(value = "字段描述")
    private String desc;
}
