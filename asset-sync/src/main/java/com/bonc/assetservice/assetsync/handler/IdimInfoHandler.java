package com.bonc.assetservice.assetsync.handler;

import com.bonc.assetservice.assetsync.model.DimInfoMsgVO;
import com.bonc.assetservice.assetsync.model.GroupInfoMsgVO;
import com.bonc.assetservice.metadata.entity.MetaDimInfo;

import java.util.List;

public interface IdimInfoHandler {

    void assetHandle(DimInfoMsgVO dimInfoMsgVO);

    void syncAssetHandle(List<MetaDimInfo> metaDimInfoList);
}
