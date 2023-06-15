package com.bonc.assetservice.assetsync.handler;

import com.bonc.assetservice.assetsync.model.AssetDataMsgVO;

public interface IAssetDataRegHandler {

    void assetHandle(AssetDataMsgVO assetDataMsgVO, String logId);
}
