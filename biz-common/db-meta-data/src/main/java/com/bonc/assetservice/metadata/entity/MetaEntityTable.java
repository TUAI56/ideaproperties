package com.bonc.assetservice.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: meta_entity_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_entity_table")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_entity_table对象", description="实体表对象")
public class MetaEntityTable implements Serializable {
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
	/**tableCode*/
    @ApiModelProperty(value = "tableCode")
    private String tableCode;
	/**tableName*/
    @ApiModelProperty(value = "tableName")
    private String tableName;
	/**表类型：横表、纵表*/
    @ApiModelProperty(value = "表类型：横表、纵表")
    private String tableType;
	/**业务类型：移网、固网*/
    @ApiModelProperty(value = "业务类型：移网、固网")
    private String businessType;
	/**数据周期*/
    @ApiModelProperty(value = "数据周期")
    private String tableInterval;
	/**tenantCode*/
    @ApiModelProperty(value = "tenantCode")
    private String tenantCode;
    /**dbInfoId*/
    @ApiModelProperty(value = "dbInfoId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dbInfoId;
    /**dbCode*/
    @ApiModelProperty(value = "dbCode")
    private String dbCode;
	/**owner*/
    @ApiModelProperty(value = "owner")
    private String owner;
	/**onlineDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "onlineDate")
    private java.util.Date onlineDate;
	/**offlineDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "offlineDate")
    private java.util.Date offlineDate;
	/**isValid*/
    @ApiModelProperty(value = "isValid")
    private String isValid;
}
