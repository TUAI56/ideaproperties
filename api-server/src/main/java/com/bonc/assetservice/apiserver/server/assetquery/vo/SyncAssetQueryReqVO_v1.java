package com.bonc.assetservice.apiserver.server.assetquery.vo;


import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.ConditionAssetVO_v1;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v1;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName LabelQueryRequestParam
 * @Author 李维帅
 * @Date 2022/6/9 17:37
 * @Version 1.0
 * @Description: [标签取数] 实时查询数据的请求报文
 **/
@Data
@ApiModel(value="[标签取数]实时查询数据的请求报文")
public class SyncAssetQueryReqVO_v1 extends AbstractSyncQueryReqVO implements Serializable {

    private static final long serialVersionUID = 2894311349534280752L;

    /**
     * 查询标签
     */
    @Valid
    @NotEmpty(message = "queryList不能为空")
    @ApiModelProperty(value = "查询标签对象列表", required = true)
    private List<QueryAssetVO_v1> queryList;

    /**
     * 查询条件
     */
    @Valid
    @ApiModelProperty(value = "查询条件对象列表")
    private List<ConditionAssetVO_v1> conditionList;

}
