package com.bonc.assetservice.apiserver.server.dataset.vo;

import com.bonc.assetservice.apiserver.server.common.AbstractBaseReqVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value="自定义数据集创建参数")
public class
DatasetCreateReqVo extends AbstractBaseReqVO {

    @ApiModelProperty(value = "省分ID", required = true)
    private String provId;

    @NotBlank(message = "自定义数据集id为空")
    private String datasetId;
    @NotBlank(message = "自定义数据集名称为空")
    private String name;
    private String expireDate;
    @NotBlank(message = "自定义数据集关联列编码为空")
    private String joinColumnCode;
    @NotNull(message = "自定义数据集字段列表为空")
    private List<Column> columns;



}
