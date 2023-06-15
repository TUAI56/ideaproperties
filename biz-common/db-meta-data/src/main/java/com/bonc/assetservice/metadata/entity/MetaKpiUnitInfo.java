package com.bonc.assetservice.metadata.entity;

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
 * @Description: meta_kpi_unit_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_kpi_unit_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_kpi_unit_info对象", description="meta_kpi_unit_info")
public class MetaKpiUnitInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**unitCode*/
    @ApiModelProperty(value = "unitCode")
    private String unitCode;
	/**unitType*/
    @ApiModelProperty(value = "unitType")
    private String unitType;
	/**unitName*/
    @ApiModelProperty(value = "unitName")
    private String unitName;
	/**isBase*/
    @ApiModelProperty(value = "isBase")
    private String isBase;
	/**calculateRule*/
    @ApiModelProperty(value = "calculateRule")
    private String calculateRule;
	/**isValid*/
    @ApiModelProperty(value = "isValid")
    private String isValid;
}
