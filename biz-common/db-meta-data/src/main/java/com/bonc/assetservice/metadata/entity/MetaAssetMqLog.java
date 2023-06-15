package com.bonc.assetservice.metadata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: meta_asset_mq_log
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("meta_asset_mq_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_asset_mq_log对象", description="资产消息对象")
@Builder
public class MetaAssetMqLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "oid")
    private String oid;

    @ApiModelProperty(value="msgId")
    private String msgId;

    /**
     * mq消息
     */
    @ApiModelProperty(value = "mq消息内容")
    private String msg;

    /**
     * 状态 0上线、1下线
     */
    @ApiModelProperty(value = "状态 0上线、1下线")
    private String status;

    /**
     * 分类 1表、7字段
     */
    @ApiModelProperty(value = "分类 1表、7字段")
    private String classes;

    /**
     * 表（是否模板表 1是、0否），字段（是否找到相应表 1是、0否）
     */
    @ApiModelProperty(value = "表（是否模板表 1是、0否），字段（是否找到相应表 1是、0否）")
    private String isAllow;

    /**
     * 表oid
     */
    @ApiModelProperty(value = "表oid")
    private String dataId;

    /**
     * 资产名称 表（data_code),字段（asset_id)
     */
    @ApiModelProperty(value = "资产名称 表（data_code),字段（asset_id)")
    private String assetName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createDate;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date updateDate;

    /**
     * 资产信息请求报文
     */
    @ApiModelProperty(value = "资产信息请求报文")
    private String assetJson;

    /**
     * 字段信息请求报文
     */
    @ApiModelProperty(value = "字段信息请求报文")
    private String filedJson;

    /**
     * 码表信息请求报文
     */
    @ApiModelProperty(value = "码表信息请求报文")
    private String dimJson;

    /**
     * 注册不成功信息
     */
    @ApiModelProperty(value = "注册不成功信息")
    private String exceptionMsg;


}
