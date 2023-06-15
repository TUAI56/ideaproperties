package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.appmodel.AssetCodeAndAcct;
import com.bonc.assetservice.metadata.appmodel.ModelAndEntityTableInfo;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;
import com.bonc.assetservice.metadata.appmodel.LabelDetailsModel;
import com.bonc.assetservice.metadata.entity.MetaDimInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: meta_asset_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */

@Mapper
public interface MetaAssetInfoMapper extends BaseMapper<MetaAssetInfo> {

        /**
         * 通过资产编号查询资产的信息
         * @param assetCode 资产编号
         * @return
         */
        MetaAssetInfo selectMetaAssetInfoByAssetCode(@Param("assetCode") String assetCode);

        /**
         * 根据标签ID列表，获取这些标签的最新账期和assetCode
         * @param assetIds 需要查询的标签的集合
         * @return AssetCodeAndAcct
         */
        List<AssetCodeAndAcct> getAssetCodeAndAcct(@Param("assetIds") List<String> assetIds);

        /**
         * 根据实体表ID列表，获取对应的表信息
         * @param tableCodes 模板表id列表
         * @return ModelAndKeyInfo
         */
        List<ModelAndEntityTableInfo> getTableInfosByTableCodes(@Param("tableCodes") List<String> tableCodes);

        /**
         * 根据实体表ID列表，获取对应的域主表信息
         * @param tableCodes 模板表id列表
         * @return ModelAndKeyInfo
         */
        List<ModelAndEntityTableInfo> getDomainMasterTableInfosByTableCodes(@Param("tableCodes") List<String> tableCodes);

        /**
         * 查询标签资产的详情
         * @param provId 省分的编号
         * @param assetId 资产的id
         * @return
         */
        LabelDetailsModel queryAssetDetails(@Param("provId")String provId,@Param("assetId") String assetId);

        /**
         * 查询编码映射表
         */
        List<Map<String, Object>> selectCodeMapping();

        /**
         * 根据租户id查询省份信息
         */
        Map<String, Object> selectProvCode(String tenantId);

        /**
         * 获取码表id信息
         * @param params
         * @return
         */
        Long selectDimInfo(Map<String, Object> params);
        }
