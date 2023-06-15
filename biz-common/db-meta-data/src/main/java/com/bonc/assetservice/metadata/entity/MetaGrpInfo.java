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
import java.util.Date;

/**
 * @Description: meta_grp_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_grp_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_grp_info对象", description="meta_grp_info")
public class MetaGrpInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**grpCode*/
    @ApiModelProperty(value = "grpCode")
    private String grpCode;
	/**grpName*/
    @ApiModelProperty(value = "grpName")
    private String grpName;
	/**parentGrpCode*/
    @ApiModelProperty(value = "parentGrpCode")
    private String parentGrpCode;
	/**path*/
    @ApiModelProperty(value = "path")
    private String path;
	/**domainCode*/
    @ApiModelProperty(value = "domainCode")
    private String domainCode;
	/**grpSort*/
    @ApiModelProperty(value = "grpSort")
    private String grpSort;
    /**level*/
    @ApiModelProperty(value = "level")
    private String level;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "插入时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date insertTime;

    /**idPath*/
    @ApiModelProperty(value = "idPath")
    private String idPath;

}
