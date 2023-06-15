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

@Data
@TableName("meta_grp_mq_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="meta_grp_mq_log对象", description="标签分组日志")
@Builder
public class MetaGrpMqLog implements Serializable {
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
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createDate;

    /**
     * 资产信息请求报文
     */
    @ApiModelProperty(value = "分组信息请求报文")
    private String assetJson;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date updateDate;

    /**
     * 注册不成功信息
     */
    @ApiModelProperty(value = "注册不成功信息")
    private String exceptionMsg;


}
