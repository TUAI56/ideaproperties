package com.bonc.assetservice.apiserver.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@TableName("user_dataset")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="user_dataset对象", description="user_dataset")
public class UserDataset implements Serializable {
    private static final long serialVersionUID = 1L;

	/**datasetId*/
	@TableId(type = IdType.INPUT)
    @ApiModelProperty(value = "datasetId")
    private String datasetId;
	/**tqAppUuid*/
    @ApiModelProperty(value = "tqAppUuid")
    private String tqAppUuid;
	/**reqId*/
    @ApiModelProperty(value = "reqId")
    private String reqId;
	/**provId*/
    @ApiModelProperty(value = "provId")
    private String provId;
	/**name*/
    @ApiModelProperty(value = "name")
    private String name;
	/**expireDate*/
    @ApiModelProperty(value = "expireDate")
    private String expireDate;
    /**entityTableCode*/
    @ApiModelProperty(value = "entityTableCode")
    private String entityTableCode;
	/**joinColumnCode*/
    @ApiModelProperty(value = "joinColumnCode")
    private String joinColumnCode;
	/**crtTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "crtTime")
    private Date crtTime;
	/**updTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "updTime")
    private Date updTime;
	/**delFlag*/
    @ApiModelProperty(value = "delFlag")
    private Integer delFlag;
}
