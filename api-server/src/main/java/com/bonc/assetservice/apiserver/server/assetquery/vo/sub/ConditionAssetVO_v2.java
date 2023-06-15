package com.bonc.assetservice.apiserver.server.assetquery.vo.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ConditionAssetVO_v2
 * @Author zzs
 * @Date 2022/6/9 17:49
 * @Version 2.0
 *
 * 比较上一次版本修改
 * 1、新增conditionType
 * 2、新增dataset
 * 3、asset内容新曾一个类
 * 注意：此版本满足实时查询接口2.0版本需求
 **/
@Data
@ApiModel(value="查询条件对象")
public class ConditionAssetVO_v2 implements Serializable {
    private static final long serialVersionUID = -7845827802281385063L;


    @ApiModelProperty(value = "逻辑操作符运算符 AND/OR，数组第一个不传")
    private String logicalOperator;

    @ApiModelProperty(value = "条件类型：asset：标签作为查询条件 dataset：自定义数据集作为查询条件")
    private String conditionType;

    @ApiModelProperty(value = "conditionType为asset时，此对象必填。同级目录下的asset对象和subConditionList不生效")
    private Asset asset;

    @ApiModelProperty(value = "conditionType为dataset时，此对象必填。同级目录下的dataset对象和subConditionList不生效")
    private Dataset dataset;

    @Valid
    @ApiModelProperty(value = "子查询（格式和conditionlist一致，用于括号查询。当传subConditionList时，同级id、operator、params非必填）")
    private List<ConditionAssetVO_v2> subConditionList;


    @Data
    @ApiModel(value="查询条件Asset对象V2.0")
    public static class Asset{
        @ApiModelProperty(value = "标签id")
        private String assetId;

        @ApiModelProperty(value = "操作符 =、!=、>、<、IN、LIKE等")
        private String operator;

        @ApiModelProperty(value = "条件值，如果条件in时多个值逗号分隔")
        private String params;

        @ApiModelProperty(value = "账期:字段为空时使用默认账期。日账期格式：yyyyMMdd，月账期格式：yyyyMM")
        private String acct;
    }


    @Data
    @ApiModel(value="查询条件Dataset对象V2.0")
    public static class Dataset{
        @ApiModelProperty(value = "自定义数据集id")
        private String datasetId;

        @ApiModelProperty(value = "逻辑操作符运算符 AND/OR，数组第一个不传")
        private String logicalOperator;

        @ApiModelProperty(value = "用来作为筛选数据用的列信息")
        private List<Column> columns;




        @Data
        @ApiModel(value="查询条件Dataset对象的ColumnV2.0")
        public  static  class Column{

            @ApiModelProperty(value = "自定义数据集中的列编码")
            private String code;

            @ApiModelProperty(value = "操作符 =、!=、>、<、IN、LIKE等")
            private String operator;

            @ApiModelProperty(value = "条件值，如果条件in时多个值逗号分隔")
            private String params;

            @ApiModelProperty(value = "逻辑操作符运算符 AND/OR，数组第一个不传")
            private String logicalOperator;
        }
    }






}
