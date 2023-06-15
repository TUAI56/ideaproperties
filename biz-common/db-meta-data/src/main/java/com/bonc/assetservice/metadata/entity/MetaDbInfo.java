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
 * @Description: meta_db_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_db_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_db_info对象", description="meta_db_info")
public class MetaDbInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**dbCode*/
    @ApiModelProperty(value = "dbCode")
    private String dbCode;
	/**dbName*/
    @ApiModelProperty(value = "dbName")
    private String dbName;
	/**dbType*/
    @ApiModelProperty(value = "dbType")
    private String dbType;
	/**ip*/
    @ApiModelProperty(value = "ip")
    private String ip;
	/**userName*/
    @ApiModelProperty(value = "userName")
    private String userName;
	/**passwd*/
    @ApiModelProperty(value = "passwd")
    private String passwd;
	/**serviceName*/
    @ApiModelProperty(value = "serviceName")
    private String serviceName;
	/**dbCharset*/
    @ApiModelProperty(value = "dbCharset")
    private String dbCharset;
	/**isCluster*/
    @ApiModelProperty(value = "isCluster")
    private String isCluster;
	/**clusterName*/
    @ApiModelProperty(value = "clusterName")
    private String clusterName;
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
