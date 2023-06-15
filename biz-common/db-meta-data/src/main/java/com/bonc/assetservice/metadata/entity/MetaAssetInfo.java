package com.bonc.assetservice.metadata.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: meta_asset_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_asset_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_asset_info对象", description="meta_asset_info")
public class MetaAssetInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**platAssetId*/
    @ApiModelProperty(value = "platAssetId")
    private String platAssetId;
	/**assetCode*/
    @ApiModelProperty(value = "assetCode")
    private String assetCode;
	/**assetName*/
    @ApiModelProperty(value = "assetName")
    private String assetName;
    /**modelTableId*/
    @ApiModelProperty(value = "modelTableId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelTableId;
    /**modelCode*/
    @ApiModelProperty(value = "modelCode")
    private String   modelCode;
    /**colInfoId*/
    @ApiModelProperty(value = "colInfoId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long colInfoId;
	/**colCode*/
    @ApiModelProperty(value = "colCode")
    private String colCode;
	/**资产类型：1指标，2维度，3标签；*/
    @ApiModelProperty(value = "资产类型：横表：文本型1，维度型2，数值型3，字典型4；" +
                                       "纵表：文本型5，维度型6，数值型7，字典型8")
    private String assetType;
	/**业务类型：YW、GW*/
    @ApiModelProperty(value = "业务类型：YW、GW")
    private String businessType;
    /**业务类型：移网、固网*/
    @ApiModelProperty(value = "业务类型：移网、固网")
    private String businessTypeName;
	/**数据周期*/
    @ApiModelProperty(value = "数据周期")
    private String assetInterval;
    /**数据周期标识*/
    @ApiModelProperty(value = "数据周期标识")
    private String assetIntervalLogo;
	/**businessRemark*/
    @ApiModelProperty(value = "businessRemark")
    private String businessRemark;
	/**tecRemark*/
    @ApiModelProperty(value = "tecRemark")
    private String tecRemark;
	/**colType*/
    @ApiModelProperty(value = "colType")
    private String colType;
	/**colLength*/
    @ApiModelProperty(value = "colLength")
    private Integer colLength;
	/**domainCode*/
    @ApiModelProperty(value = "domainCode")
    private String domainGrpId;
	/**grpCode*/
    @ApiModelProperty(value = "grpCode")
    private String grpId;
	/**dimCode*/
    @ApiModelProperty(value = "dimCode")
    private Long dimCode;
    /**linkDim*/
    @ApiModelProperty(value = "linkDim")
    private String linkDim;
    /**维表名称*/
    @ApiModelProperty(value = "dimName")
    private String dimName;
	/**unitCode*/
    @ApiModelProperty(value = "unitCode")
    private String unitCode;
	/**regiMan*/
    @ApiModelProperty(value = "regiMan")
    private String regiMan;
	/**regiDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "regiDate")
    private Date regiDate;
	/**onlineDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "onlineDate")
    private Date onlineDate;
	/**offlineDate*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "offlineDate")
    private Date offlineDate;
	/**isValid*/
    @ApiModelProperty(value = "isValid")
    private String isValid;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
    private Date createTime;
	/**securityLevel*/
    @ApiModelProperty(value = "securityLevel")
    private String securityLevel;
    /**securityLevelName*/
    @ApiModelProperty(value = "securityLevelName")
    private String securityLevelName;
	/**securityType*/
    @ApiModelProperty(value = "securityType")
    private String securityType;
    /**securityTypeName*/
    @ApiModelProperty(value = "securityTypeName")
    private String securityTypeName;
	/**descondid*/
    @ApiModelProperty(value = "descondid")
    private String descondid;
	/**enccondid*/
    @ApiModelProperty(value = "enccondid")
    private String enccondid;
	/**tenantCode*/
    @ApiModelProperty(value = "tenantCode")
    private String tenantCode;
	/**sampleData*/
    @ApiModelProperty(value = "sampleData")
    private String sampleData;
    /**
     * 资产版本id-序列化长字符串
     */
    @ApiModelProperty(value = "assetVersionId")
    private String assetVersionId;
    /**
     * 资产版本号-数字递增
     */
    @ApiModelProperty(value = "assetVersionNo")
    private String assetVersionNo;
    /**
     * 元数据表id
     */
    @ApiModelProperty(value = "tableOid")
    private String tableOid;
    /**
     * 字段id
     */
    @ApiModelProperty(value = "fieldOid")
    private String fieldOid;
    /**
     * 入库顺序
     */
    @ApiModelProperty(value = "colSort")
    private String colSort;
    /**
     * 省份编码
     */
    @ApiModelProperty(value = "provCode")
    private String provCode;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "sortField")
    private String sortField;

    /**
     * 排序类型
     */
    @ApiModelProperty(value = "sortType")
    private String sortType;

    /**
     * 排序类型
     */
    @ApiModelProperty(value = "linkId")
    private String linkId;

}
