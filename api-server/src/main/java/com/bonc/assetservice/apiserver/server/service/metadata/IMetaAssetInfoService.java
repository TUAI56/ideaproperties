package com.bonc.assetservice.apiserver.server.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonc.assetservice.metadata.appmodel.AssetCodeAndAcct;
import com.bonc.assetservice.metadata.appmodel.ModelAndEntityTableInfo;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: meta_asset_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
public interface IMetaAssetInfoService extends IService<MetaAssetInfo> {

    /**
     * 根据标签ID列表，获取这些标签的最新账期和assetCode
     * @param assetIds 需要查询的标签的集合
     * @return <assetId, AssetCodeAndAcct>
     */
    HashMap<Long, AssetCodeAndAcct> getAssetCodeAndAcct(List<String> assetIds);



    /**
     * 根据实体表ID列表，获取对应的表信息
     * @param tableCodes 模板表id列表
     * @return ModelAndKeyInfo
     */
    List<ModelAndEntityTableInfo> getTableInfosByTableCodes(List<String> tableCodes);

    /**
     * 根据实体表ID列表，获取域主表表相关信息
     * @param tableCodes 模板表id列表
     * @return 模板表的相关信息
     */
    List<ModelAndEntityTableInfo> getDomainMasterTableInfosByTableCodes(List<String> tableCodes);
}
