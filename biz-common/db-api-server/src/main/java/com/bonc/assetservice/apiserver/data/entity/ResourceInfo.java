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

/**
 * @Description: resource_info
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Data
@TableName("resource_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="resource_info对象", description="resource_info")
public class ResourceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**资源名称*/
    @ApiModelProperty(value = "资源名称")
    private String name;
	/**ftp, sftp*/
    @ApiModelProperty(value = "ftp, sftp")
    private String type;
	/**ip*/
    @ApiModelProperty(value = "ip")
    private String ip;
	/**port*/
    @ApiModelProperty(value = "port")
    private Integer port;
	/**用户名*/
    @ApiModelProperty(value = "用户名")
    private String userName;
	/**密码*/
    @ApiModelProperty(value = "密码")
    private String userPwd;
	/**根路径*/
    @ApiModelProperty(value = "根路径")
    private String path;
	/**-1：初始状态;0：失效；1：有效*/
    @ApiModelProperty(value = "-1：初始状态;0：失效；1：有效")
    private Integer status;
	/**删除状态*/
    @ApiModelProperty(value = "删除状态")
    private Integer delFlag;
}
