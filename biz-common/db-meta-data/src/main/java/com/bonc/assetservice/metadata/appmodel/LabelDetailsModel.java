package com.bonc.assetservice.metadata.appmodel;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabelDetailsModel {

    /**
     * 资产的信息
     */
    @ApiModelProperty(value = "assetId")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assetId;
    /**
     * 标签编码
     */
    private String assetCode;
    /**
     * 标签名称
     */
    private String assetName;
    /**
     * 标签的样例数据
     */
    private String sampleData;
    /**
     * 标签类型：（字符：1；维度：2；数值：3）
     */
    private String assetType;
    /**
     * 业务类型：移网、固网
     */
    private String businessType;
    /**
     * 码表编码
     */
    private String dimId;
    /**
     * 标签单位名称
     */
    private String unitName;
    /**
     *  技术口径
     */
    private String tecRemark;
    /**
     * 业务口径
     */
    private String businessRemark;
    /**
     * 标签的最新账期
     */
    private String latestDate;
    /**
     * 标签的更新周期
     */
    private String assetInterval;
    /**
     * 最新更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateDate")
    private Date updateDate;

}
