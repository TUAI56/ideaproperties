package com.bonc.assetservice.apiserver.server.assetquery.vo.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName QueryLabel
 * @Author 李维帅
 * @Date 2022/6/9 17:48
 * @Version 1.0
 **/
@Data
@ApiModel(value="查询标签对象")
public class QueryAssetVO_v1 implements Serializable {

    private static final long serialVersionUID = 6409117364192928691L;

    /**
     * 标签标识
     */
    @NotBlank(message = "queryList中assetId不能为空")
    @ApiModelProperty(value = "标签id", required = true)
    private String assetId;

    /**
     * 显示名称
     */
    @NotBlank(message = "queryList中displayName不能为空")
    @ApiModelProperty(value = "展示名称（别名）", required = true)
    private String displayName;

    /**
     * 账期
     */
    @ApiModelProperty(value = "账期:字段为空时使用默认账期。日账期格式：yyyyMMdd，月账期格式：yyyyMM")
    private String acct;

    /**
     * 排序方式 ASC|DESC
     */
    @ApiModelProperty(value = "排序方式 ASC、DESC")
    private String sortType;
    /**
     * 聚合类型 COUNT、SUM、MAX、MIN、AVG
     */
    @ApiModelProperty(value = "汇聚运算符 COUNT、SUM、MAX、MIN、AVG")
    private String aggrType;

}
