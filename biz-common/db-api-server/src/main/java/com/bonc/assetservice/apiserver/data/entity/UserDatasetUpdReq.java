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
 * @Description: user_dataset_upd_req
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Data
@TableName("user_dataset_upd_req")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="user_dataset_upd_req对象", description="user_dataset_upd_req")
public class UserDatasetUpdReq implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
	/**tenantId*/
    @ApiModelProperty(value = "tqAppUuid")
    private String tqAppUuid;
	/**reqId*/
    @ApiModelProperty(value = "reqId")
    private String reqId;
	/**provId*/
    @ApiModelProperty(value = "provId")
    private String provId;
	/**datasetId*/
    @ApiModelProperty(value = "datasetId")
    private String datasetId;
	/**resourceId*/
    @ApiModelProperty(value = "resourceId")
    //JsonSerialize注解用于把Long做json序列化时，转为String，解决前端vue精度丢失问题
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resourceId;
	/**filePath*/
    @ApiModelProperty(value = "filePath")
    private String filePath;
	/**fileName*/
    @ApiModelProperty(value = "fileName")
    private String fileName;
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
	/**0：待处理；1：处理中；2：处理成功；
            -1：网络异常；-2：文件路径异常；-3：文件不存在*/
    @ApiModelProperty(value = "0：待处理；1：处理中；2：处理成功； -1：连接sftp失败；-2：文件或路径不存在；-3：执行失败")
    private Integer state;

    /**0：待处理；1：处理中；2：处理成功；-1：校验失败；-2：解析失败；-3：执行失败*/

    /**
     *状态-待处理：0
     **/
    public final static Integer STATE_WAITING = 0;
    /**
     *状态-处理中：1
     **/
    public final static Integer STATE_PROCESSING = 1;
    /**
     *状态-处理成功：2
     **/
    public final static Integer STATE_SUCCESS = 2;
    /**
     *状态-网络异常：-1
     **/
    public final static Integer STATE_SFTPCONNECT_EXCEPTION = -1;
    /**
     *状态-文件或路径不存在：-2
     **/
    public final static Integer STATE_FILE_PATH_EXCEPTION = -2;
    /**
     *状态-执行失败：-3
     **/
    public final static Integer STATE_EXEC_FAIL = -3;
}
