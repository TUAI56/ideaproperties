package com.bonc.assetservice.assetsync.handler;


import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;

public interface IAssetRegisterHandler {

    void assetHandle(AssetsMsgVO assetsVO, AttributeDetailDataVO detailData, String logId);


}
