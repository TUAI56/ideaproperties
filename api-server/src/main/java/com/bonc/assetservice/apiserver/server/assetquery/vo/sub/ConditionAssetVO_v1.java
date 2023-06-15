package com.bonc.assetservice.apiserver.server.assetquery.vo.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Condition_v1
 * @Author 李维帅
 * @Date 2022/6/9 17:49
 * @Version 1.0
 **/
@Data
@ApiModel(value="查询条件对象")
public class ConditionAssetVO_v1 implements Serializable {

    private static final long serialVersionUID = -7845827802281385063L;

    @ApiModelProperty(value = "标签id")
    private String assetId;

    @ApiModelProperty(value = "操作符 =、!=、>、<、IN、LIKE等")
    private String operator;

    @ApiModelProperty(value = "条件值，如果条件in时多个值逗号分隔")
    private String params;

    @ApiModelProperty(value = "账期:字段为空时使用默认账期。日账期格式：yyyyMMdd，月账期格式：yyyyMM")
    private String acct;

    @ApiModelProperty(value = "逻辑操作符运算符 AND/OR，数组第一个不传")
    private String logicalOperator;

    @Valid
    @ApiModelProperty(value = "子查询（格式和conditionlist一致，用于括号查询。当传subConditionList时，同级id、operator、params非必填）")
    private List<ConditionAssetVO_v1> subConditionList;

}
