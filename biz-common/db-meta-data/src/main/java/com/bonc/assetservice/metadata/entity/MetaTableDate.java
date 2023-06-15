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
@TableName("meta_table_date")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_table_date对象", description="meta_table_date")
public class MetaTableDate implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**entityTableId*/
    @ApiModelProperty(value = "entityTableId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long entityTableId;
	/**tableCode*/
    @ApiModelProperty(value = "tableCode")
    private String tableCode;
	/**tableName*/
    @ApiModelProperty(value = "tableName")
    private String tableName;
	/**acctDate*/
    @ApiModelProperty(value = "acctDate")
    private String acctDate;
	/**dateType*/
    @ApiModelProperty(value = "dateType")
    private String dateType;
}
