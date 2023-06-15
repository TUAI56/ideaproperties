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
 * @Description: meta_col_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_col_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_col_info对象", description="字段信息表")
public class MetaColInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**colCode*/
    @ApiModelProperty(value = "colCode")
    private String colCode;
	/**colName*/
    @ApiModelProperty(value = "colName")
    private String colName;
    /**modelTableId*/
    @ApiModelProperty(value = "modelTableId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelTableId;
	/**modelCode*/
    @ApiModelProperty(value = "modelCode")
    private String modelCode;
	/**colType*/
    @ApiModelProperty(value = "colType")
    private String colType;
	/**colLength*/
    @ApiModelProperty(value = "colLength")
    private Integer colLength;
	/**precisions*/
    @ApiModelProperty(value = "precisions")
    private String precisions;
	/**defaultValue*/
    @ApiModelProperty(value = "defaultValue")
    private String defaultValue;
	/**colSort*/
    @ApiModelProperty(value = "colSort")
    private Integer colSort;
	/**tenantCode*/
    @ApiModelProperty(value = "tenantCode")
    private String tenantCode;
	/**isPaimary*/
    @ApiModelProperty(value = "isPaimary")
    private String isPaimary;
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
	/**comments*/
    @ApiModelProperty(value = "comments")
    private String comments;
	/**businessRemark*/
    @ApiModelProperty(value = "businessRemark")
    private String businessRemark;
	/**tecRemark*/
    @ApiModelProperty(value = "tecRemark")
    private String tecRemark;
	/**securityLevel*/
    @ApiModelProperty(value = "securityLevel")
    private String securityLevel;
    /**securityLevelName*/
    @ApiModelProperty(value = "securityLevelName")
    private String securityLevelName;
	/**securityType*/
    @ApiModelProperty(value = "securityType")
    private String securityType;
    /**securityTypeName*/
    @ApiModelProperty(value = "securityTypeName")
    private String securityTypeName;
	/**isNullable*/
    @ApiModelProperty(value = "isNullable")
    private String isNullable;
	/**valueType*/
    @ApiModelProperty(value = "valueType")
    private String valueType;
	/**valueUnit*/
    @ApiModelProperty(value = "valueUnit")
    private String valueUnit;
	/**dateFormat*/
    @ApiModelProperty(value = "dateFormat")
    private String dateFormat;
	/**isDate*/
    @ApiModelProperty(value = "isDate")
    private String isDate;
	/**isMasterKey*/
    @ApiModelProperty(value = "isMasterKey")
    private String isMasterKey;
	/**isArea*/
    @ApiModelProperty(value = "isArea")
    private String isArea;
    /**
     * 字段id
     */
    @ApiModelProperty(value = "fieldOid")
    private String fieldOid;
    /**
     * 表元数据id
     */
    @ApiModelProperty(value = "tableOid")
    private String tableOid;
}
