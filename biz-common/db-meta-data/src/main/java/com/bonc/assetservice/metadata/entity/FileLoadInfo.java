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
 * @Description: file_load_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("file_load_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="file_load_info对象", description="file_load_info")
public class FileLoadInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**fileCode*/
    @ApiModelProperty(value = "fileCode")
    private String fileCode;
	/**fileName*/
    @ApiModelProperty(value = "fileName")
    private String fileName;
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
	/**startTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "startTime")
    private java.util.Date startTime;
	/**endTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "endTime")
    private java.util.Date endTime;
	/**rowCount*/
    @ApiModelProperty(value = "rowCount")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long rowCount;
	/**dataSize*/
    @ApiModelProperty(value = "dataSize")
    private String dataSize;
	/**state*/
    @ApiModelProperty(value = "state")
    private String state;
	/**note*/
    @ApiModelProperty(value = "note")
    private String note;
	/**acctDate*/
    @ApiModelProperty(value = "acctDate")
    private String acctDate;
	/**tenantCode*/
    @ApiModelProperty(value = "tenantCode")
    private String tenantCode;
	/**isRepeat*/
    @ApiModelProperty(value = "isRepeat")
    private String isRepeat;
}
