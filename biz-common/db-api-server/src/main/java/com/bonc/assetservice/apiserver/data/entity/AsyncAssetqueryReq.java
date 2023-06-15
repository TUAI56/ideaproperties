package com.bonc.assetservice.apiserver.data.entity;

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
 * @Description: async_assetquery_req
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Data
@TableName("async_assetquery_req")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="async_assetquery_req对象", description="async_assetquery_req")
public class AsyncAssetqueryReq implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**tqAppId*/
    @ApiModelProperty(value = "tqAppUuid")
    private String tqAppUuid;
	/**reqId*/
    @ApiModelProperty(value = "reqId")
    private String reqId;
	/**queryReq*/
    @ApiModelProperty(value = "queryReq")
    private String queryReq;
	/**resourceId*/
    @ApiModelProperty(value = "resourceId")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resourceId;
	/**fileNam*/
    @ApiModelProperty(value = "fileName")
    private String fileName;
	/**filePath*/
    @ApiModelProperty(value = "filePath")
    private String filePath;
	/**fileType*/
    @ApiModelProperty(value = "fileType")
    private String fileType;
	/**separatChar*/
    @ApiModelProperty(value = "separateChar")
    private String separateChar = SEPARATE_CHAR_COMMA;
    /**
     *分隔符-逗号
     **/
    public final static String SEPARATE_CHAR_COMMA = ",";
    /**
     *分隔符-竖线
     **/
    public final static String SEPARATE_CHAR_VERTICAL_BAR = "|";
	/**0：待处理；1：处理中；2：处理成功；-1：校验失败；-2：解析失败；-3：执行失败*/
    @ApiModelProperty(value = "0：待处理；1：处理中；2：处理成功； -1：校验失败；-2：解析失败；-3：执行失败")
    private Integer state;
    /**
     *状态-待处理：0
     **/
    public final static Integer STATE_PENDING = 0;
    /**
     *状态-处理中：1
     **/
    public final static Integer STATE_PROCESSING = 1;
    /**
     *状态-处理成功：2
     **/
    public final static Integer STATE_SUCCESS = 2;
    /**
     *状态-校验失败：-1
     **/
    public final static Integer STATE_VALIDATE_FAIL = -1;
    /**
     *状态-解析失败：-2
     **/
    public final static Integer STATE_PARSE_FAIL = -2;
    /**
     *状态-执行失败：-3
     **/
    public final static Integer STATE_EXEC_FAIL = -3;
    /**crtTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "crtTime")
    private Date crtTime;
	/**finishTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "finishTime")
    private Date finishTime;
}
