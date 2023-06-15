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
 * @Author: Ethan.Xing
 * @Date: 2022/6/13
 * @Description: [标签取数] 下载数据的请求报文
 */

@Data
@ApiModel(value="[标签取数] 下载数据的请求报文")
public class AsyncAssetQueryReqVO_v1 extends AbstractAsyncQueryReqVO implements Serializable {

    private static final long serialVersionUID = -8533235397483801818L;

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
