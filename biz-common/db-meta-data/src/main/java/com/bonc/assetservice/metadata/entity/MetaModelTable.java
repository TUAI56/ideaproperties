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
 * @Description: meta_model_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_model_table")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_model_table对象", description="模板表")
public class MetaModelTable implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**modelCode*/
    @ApiModelProperty(value = "modelCode")
    private String modelCode;
	/**modelName*/
    @ApiModelProperty(value = "modelName")
    private String modelName;
	/**存储类型：横表、纵表*/
    @ApiModelProperty(value = "存储类型：横表、纵表")
    private String modelType;
	/**业务类型：YW、GW*/
    @ApiModelProperty(value = "业务类型：YW、GW")
    private String businessType;
    /**业务类型：移网、固往*/
    @ApiModelProperty(value = "业务类型：移网、固网")
    private String businessTypeName;
	/**数据周期*/
    @ApiModelProperty(value = "数据周期")
    private String modelInterval;
    /**数据周期标识*/
    @ApiModelProperty(value = "数据周期标识")
    private String modelIntervalLogo;
	/**tenantCode*/
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;
	/**provCode*/
    @ApiModelProperty(value = "省份编码")
    private String provCode;
	/**isMaster*/
    @ApiModelProperty(value = "isMaster")
    private String isMaster;
	/**itCode*/
    @ApiModelProperty(value = "itCode")
    private String itCode;
	/**domainCode*/
    @ApiModelProperty(value = "域编码")
    private String domainGrpId;
	/**dwCode*/
    @ApiModelProperty(value = "dwCode")
    private String dwCode;
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
    /**
     * 表元数据id
     */
    @ApiModelProperty(value = "tableOid")
    private String tableOid;

    /**
     * 入库文件名称
     */
    @ApiModelProperty(value = "indbFileName")
    private String indbFileName;

    /**
     * 排序类型
     */
    @ApiModelProperty(value = "linkId")
    private String linkId;
}
