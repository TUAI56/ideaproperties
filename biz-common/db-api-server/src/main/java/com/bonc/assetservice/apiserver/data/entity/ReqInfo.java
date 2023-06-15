package com.bonc.assetservice.apiserver.data.entity;

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
import java.util.Date;

/**
 * @Description: req_info
 * @Author: jeecg-boot
 * @Date:   2022-04-22
 * @Version: V1.0
 */
@Data
@TableName("req_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="req_info对象", description="req_info")
public class ReqInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**请求ID*/
    @ApiModelProperty(value = "请求ID")
    private String reqId;
	/**请求参数*/
    @ApiModelProperty(value = "请求参数")
    private String reqBody;
    /**创建时间*/
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;
}
