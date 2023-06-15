package com.bonc.assetservice.assetsync.handler.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;
import com.bonc.assetservice.metadata.entity.MetaAssetMqLog;
import com.bonc.assetservice.metadata.mapper.MetaAssetInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaAssetMqLogMapper;
import com.bonc.assetservice.metadata.mapper.MetaColInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("fieldOfflineHandler")
public class FieldOfflineServiceHandler implements IAssetRegisterHandler {

    //模版表
    @Autowired
    private MetaModelTableMapper metaModelTableMapper;
    @Autowired
    private MetaColInfoMapper metaColInfoMapper;
    //字段资产的编号
    @Autowired
    private MetaAssetInfoMapper metaAssetInfoMapper;
    @Autowired
    private MetaAssetMqLogMapper metaAssetMqLogMapper;
    /**
     * 字段资产下线
     *    修改资产的状态为失效状态
     * @param assetsVO
     * @param detailData
     * @param logId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void assetHandle(AssetsMsgVO assetsVO, AttributeDetailDataVO detailData, String logId) {
        log.info("字段下线开始");
        //设定资产表中的状态为失效状态
        String assetsId = assetsVO.getAssetsId();
        LambdaUpdateWrapper<MetaAssetInfo> metaAssetInfoWrapper = new LambdaUpdateWrapper<>();
        metaAssetInfoWrapper.eq(MetaAssetInfo::getAssetCode,assetsId)
                        .set(MetaAssetInfo::getIsValid,"0");
        metaAssetInfoMapper.update(null,metaAssetInfoWrapper);

        LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, logId)
                .set(MetaAssetMqLog::getAssetName,assetsId);
        metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
        log.info("字段下线完成");
    }


    /*
        //获取额外属性
        List<AttrVO> attrList = detailData.getAttrList();
        //字段资产
        String assetId = GetUtil.getAttrValue(attrList, ApiValueEnum.ASSET_ID);
        log.info("字段资产下线开始");
        String assetCode = GetUtil.getAttrValue(attrList, ApiValueEnum.ASSET_CODE);
        String dataId1 = mapper.selectDataIdByTableAssetId(assetsVO.getTableAssetsId());
        List<String> field = new ArrayList<>();

        field.add(assetCode);
        mapper.updateFieldOffLine(field, dataId1, assetId);

        MdAssetsDown down = new MdAssetsDown();
        down.setAssetId(assetId);
        down.setAssetCode(assetCode);
        down.setAssetName(detailData.getTitle());
        down.setRemark("下线");
        down.setOperType(1D);
        down.setProvId(ProvinceEnum.getId(GetUtil.getAttrValue(attrList, ApiValueEnum.PROV_NAME)));
        mapper.insertAssetDOWN(down);
       */

}
