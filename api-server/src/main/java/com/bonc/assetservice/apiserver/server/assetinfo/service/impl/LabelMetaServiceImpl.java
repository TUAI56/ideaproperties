package com.bonc.assetservice.apiserver.server.assetinfo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonc.assetservice.apiserver.server.assetinfo.service.LabelMetaService;
import com.bonc.assetservice.apiserver.server.assetinfo.vo.DimTableReqVO;
import com.bonc.assetservice.apiserver.server.common.PageInfo;
import com.bonc.assetservice.metadata.appmodel.DimTableModel;
import com.bonc.assetservice.metadata.appmodel.LabelDetailsModel;
import com.bonc.assetservice.metadata.appmodel.LabelTreeModel;
import com.bonc.assetservice.metadata.mapper.MetaAssetInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaDimInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaGrpInfoMapper;
import com.bonc.framework.common.util.collection.TreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LabelMetaServiceImpl implements LabelMetaService {


    @Autowired
    private MetaGrpInfoMapper metaGrpInfoMapper;
    @Autowired
    private MetaAssetInfoMapper metaAssetInfoMapper;
    @Autowired
    private MetaDimInfoMapper metaDimInfoMapper;

    @Override
    public List<LabelTreeModel> queryAssetList(String provId) {
        List<LabelTreeModel> list = metaGrpInfoMapper.queryAssetList(provId);
        log.info("查询结果为 {}", JSONObject.toJSON(list));
        //将 list 的集合转变为树图集合
        List<LabelTreeModel> treeModel = TreeUtil.listToTree(list);
        log.info("赋值后结果为 {}", JSONObject.toJSON(treeModel));
        return treeModel;
    }

    @Override
    public LabelDetailsModel queryAssetDetails(String provId, String assetId) {
        log.info("请求成功------------->");
        return metaAssetInfoMapper.queryAssetDetails(provId, assetId);
    }

    @Override
    public IPage<DimTableModel> queryDimTableDetails(DimTableReqVO model) {
        PageInfo pageInfo = model.getPageInfo();
        Page<DimTableModel> resultPage=new Page<>(pageInfo.getOffset(), pageInfo.getLimit());
        return metaDimInfoMapper.queryDimTableDetailsWithPage(resultPage, model.getDimId(), model.getName(), model.getCode());
    }


}
