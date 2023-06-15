package com.bonc.assetservice.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: meta_dim_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_dim_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_dim_info对象", description="meta_dim_info")
@Builder
public class MetaDimInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**codeField*/
    @ApiModelProperty(value = "codeField")
    private String codeField;
	/**nameField*/
    @ApiModelProperty(value = "nameField")
    private String nameField;
	/**parentCodeField*/
    @ApiModelProperty(value = "parentCodeField")
    private String parentCodeField;
	/**srcDimTable*/
    @ApiModelProperty(value = "srcDimTable")
    private String srcDimTable;
	/**sortField*/
    @ApiModelProperty(value = "sortField")
    private String sortField;
	/**dimTableCode*/
    @ApiModelProperty(value = "dimTableCode")
    private String dimTableCode;
    /**dimTableName*/
    @ApiModelProperty(value = "dimTableName")
    private String dimTableName;
	/**码表状态：动态码表、静态码表*/
    @ApiModelProperty(value = "码表状态：动态码表、静态码表")
    private String dimState;
    /**码表oid*/
    @ApiModelProperty(value = "码表oid")
    private String tableOid;
}
