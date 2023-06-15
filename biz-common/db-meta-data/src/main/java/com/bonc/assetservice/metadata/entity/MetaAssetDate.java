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
 * @Description: meta_asset_date
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_asset_date")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_asset_date对象", description="meta_asset_date")
public class MetaAssetDate implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**assetInfoId*/
    @ApiModelProperty(value = "assetInfoId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assetInfoId;
	/**assetCode*/
    @ApiModelProperty(value = "assetCode")
    private String assetCode;
	/**dateCode*/
    @ApiModelProperty(value = "dateCode")
    private String dateCode;
	/**updateDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "updateDate")
    private java.util.Date updateDate;
}
