package com.bonc.assetservice.apiserver.server.dataset.vo;

import com.bonc.assetservice.apiserver.server.common.AbstractBaseReqVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="自定义数据集更新数据参数")
public class DatasetupdateReqVo extends AbstractBaseReqVO {

    @ApiModelProperty(value = "省分ID", required = true)
    private String provId;

    @NotBlank(message = "自定义数据集id为空")
    private String datasetId;
    @NotNull(message = "存放文件的sftp信息为空")
    private SrcFtp srcFtp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SrcFtp {
        @ApiModelProperty(value = "数据文件存放的sftp服务器编码")
        @NotBlank(message = "数据文件存放的sftp服务器编码为空")
        private String resourceId;
        @ApiModelProperty(value = "数据文件在sftp服务器的相对路径")
        private String filePath;
        @ApiModelProperty(value = "数据文件名称")
        @NotBlank(message = "数据文件名为空")
        private String fileName;
    }


}
