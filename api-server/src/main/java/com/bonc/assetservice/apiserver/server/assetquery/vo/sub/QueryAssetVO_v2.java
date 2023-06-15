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
public class QueryAssetVO_v2 implements Serializable {

    private static final long serialVersionUID = 6409117364192928691L;

    @NotBlank(message = "queryType不能为空")
    @ApiModelProperty(value = "查询类型：asset：标签作为查询的数据列 dataset：自定义数据集中的列作为查询的数据列", required = true)
    private String queryType;

    @ApiModelProperty(value = "queryType为asset时，此对象必填。同级目录下的dataset对象不生效")
    private Asset asset;

    @ApiModelProperty(value = "queryType为dataset时，此对象必填。同级目录下的asset对象不生效")
    private Dataset dataset;

    /**
     * 显示名称
     */
    @NotBlank(message = "queryList中displayName不能为空")
    @ApiModelProperty(value = "展示名称（别名）", required = true)
    private String displayName;

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

    @Data
    @ApiModel(value="查询标签对象Asset")
    public static class Asset{

        /**
         * 标签标识
         */

        @ApiModelProperty(value = "标签id", required = true)
        private String assetId;
        /**
         * 账期
         */
        @ApiModelProperty(value = "账期:字段为空时使用默认账期。日账期格式：yyyyMMdd，月账期格式：yyyyMM")
        private String acct;
    }


    @Data
    @ApiModel(value="查询标签对象Dataset")
    public static class Dataset{
        /**
         * 标签标识
         */
        @ApiModelProperty(value = "自定义数据集Id，与用户调用接口8创建自定义数据集时传入的datasetId相同")
        private String datasetId;
        /**
         * 标签标识
         */
        @ApiModelProperty(value = "自定义数据集中的列编码")
        private String code;

    }

}
