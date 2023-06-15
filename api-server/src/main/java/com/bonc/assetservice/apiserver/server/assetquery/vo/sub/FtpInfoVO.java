package com.bonc.assetservice.apiserver.server.assetquery.vo.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description: FTP信息
 */

@Data
@ApiModel(value="FTP信息")
public class FtpInfoVO implements Serializable {

    private static final long serialVersionUID = 1746490840606767204L;

    @NotBlank(message = "ftp信息中resourceId不能为空")
    @ApiModelProperty(value = "文件存放服务器id", required = true)
    private String resourceId;

    @NotBlank(message = "ftp信息中fileName不能为空")
    @ApiModelProperty(value = "文件名称", required = true)
    private String fileName;

    @NotBlank(message = "ftp信息中filePath不能为空")
    @ApiModelProperty(value = "文件存储路径（相对路径）", required = true)
    private String filePath;

    @ApiModelProperty(value = "文件类型txt、csv，默认是txt")
    private String fileType = "txt";

    @ApiModelProperty(value = "当文件类型为TXT格式 需要设置分隔符 默认逗号。 目前支持“|”、“,”")
    private String separator = ",";
}
