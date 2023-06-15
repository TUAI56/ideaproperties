package com.bonc.assetservice.apiserver.data.entity;

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
import java.util.Date;

/**
 * @Description: user_dataset
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Data
@TableName("user_dataset_col_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="user_dataset_col_info对象", description="user_dataset_col_info")
public class UserDatasetColInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**datasetId*/
    @ApiModelProperty(value = "datasetId")
    private String datasetId;

    /**colCode*/
    @ApiModelProperty(value = "colCode")
    private String colCode;
	/**colName*/
    @ApiModelProperty(value = "colName")
    private String colName;
    /**colType*/
    @ApiModelProperty(value = "colType")
    private String colType;
    /**expireDate*/
    @ApiModelProperty(value = "colLength")
    private Integer colLength;
    /**desc*/
    @ApiModelProperty(value = "colDesc")
    private String colDesc;

    /**crtTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "crtTime")
    private Date crtTime;
	/**delFlag*/
    @ApiModelProperty(value = "delFlag")
    private Integer delFlag;
}
