package com.bonc.assetservice.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: meta_table_date
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_model_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_model_config对象", description="模板表配置表")
public class MetaModelConfig implements Serializable {

    private static final long serialVersionUID = 1L;
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**modelTableId*/
    @ApiModelProperty(value = "modelTableId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelTableId;

	/**模板表编号*/
    @ApiModelProperty(value = "模板表编号")
    private String modelCode;

	/**文件路径编码*/
    @ApiModelProperty(value = "文件路径编码")
    private String filePathCode;

}
