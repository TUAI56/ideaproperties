package com.bonc.assetservice.apiserver.server.assetquery.vo;


import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.ConditionAssetVO_v2;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.QueryAssetVO_v2;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @Author zzs
 * @Date 2022/6/9 17:37
 * @Version 2.0
 * @Description: [标签取数] 实时查询数据的请求报文
 * 版本说明: 实现版本 AssetQueryBaseReqVO_v2
 **/
@Data
@ApiModel(value="[标签取数]实时查询数据的请求报文")
public class AsyncAssetQueryReqVO_v2 extends AbstractAsyncQueryReqVO implements Serializable {

    private static final long serialVersionUID = 2894311349534280752L;


    /**
     * 查询标签
     */
    @Valid
    @NotEmpty(message = "queryList不能为空")
    @ApiModelProperty(value = "查询标签对象列表", required = true)
    private List<QueryAssetVO_v2> queryList;

    /**
     * 查询条件
     */
    @Valid
    @ApiModelProperty(value = "查询条件对象列表")
    private List<ConditionAssetVO_v2> conditionList;

}
