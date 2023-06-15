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
 * @Description: meta_domain_define
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_domain_define")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_domain_define对象", description="meta_domain_define")
public class MetaDomainDefine implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**domainCode*/
    @ApiModelProperty(value = "domainCode")
    private String domainCode;
	/**domainName*/
    @ApiModelProperty(value = "domainName")
    private String domainName;
	/**masterId*/
    @ApiModelProperty(value = "masterId")
    private String masterId;
	/**remark*/
    @ApiModelProperty(value = "remark")
    private String remark;
	/**validDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "validDate")
    private java.util.Date validDate;
	/**invalidDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "invalidDate")
    private java.util.Date invalidDate;
	/**isValid*/
    @ApiModelProperty(value = "isValid")
    private String isValid;
}
