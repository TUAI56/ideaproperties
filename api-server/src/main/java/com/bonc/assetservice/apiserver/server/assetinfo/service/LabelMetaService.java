package com.bonc.assetservice.apiserver.server.assetinfo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bonc.assetservice.apiserver.server.assetinfo.vo.DimTableReqVO;
import com.bonc.assetservice.metadata.appmodel.DimTableModel;
import com.bonc.assetservice.metadata.appmodel.LabelDetailsModel;
import com.bonc.assetservice.metadata.appmodel.LabelTreeModel;

import java.util.List;

public interface LabelMetaService {


    /**
     * 查询标签树 分组的信息
     * @param provId 省分的编号
     * @return
     */
    List<LabelTreeModel> queryAssetList(String provId);

    /**
     * 查询标签的详情
     * @param provId 省分编号
     * @param assetId 资产的id
     * @return
     */
    LabelDetailsModel queryAssetDetails(String provId, String assetId);

    /**
     * 分页查询码表的返回值数据
     * @param request  码表请求条件
     * @return
     */
    IPage<DimTableModel> queryDimTableDetails(DimTableReqVO request);
}
