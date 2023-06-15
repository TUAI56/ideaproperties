package com.bonc.assetservice.assetsync.handler.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttrVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.assetsync.util.GetUtil;
import com.bonc.assetservice.metadata.entity.MetaAssetMqLog;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import com.bonc.assetservice.metadata.mapper.MetaAssetMqLogMapper;
import com.bonc.assetservice.metadata.mapper.MetaColInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("tableOfflineHandler")
public class TableOfflineServiceHandler implements IAssetRegisterHandler {


    @Autowired
    private MetaAssetMqLogMapper metaAssetMqLogMapper;

    //模版表
    @Autowired
    private MetaModelTableMapper metaModelTableMapper;

    //模板表字段信息表
    @Autowired
    private MetaColInfoMapper metaColInfoMapper;


    /**
     * 表资产上线
     * @param assetsVO
     * @param detailData
     * @param logId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void assetHandle(AssetsMsgVO assetsVO, AttributeDetailDataVO detailData, String logId) {
        //获取额外属性
        List<AttrVO> attrList = detailData.getAttrList();
        log.info("开始表资产下线");
        String modelCode = assetsVO.getAssetsId();
        //表的名称
        String tableName = GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_NAME);
        //获取表编码
        String tableCode = GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_CODE);
        //设置表为下线状态......
        LambdaUpdateWrapper<MetaModelTable> metaModelTableWrapper = new LambdaUpdateWrapper<>();
        metaModelTableWrapper.eq(MetaModelTable::getModelCode,modelCode)
                        .set(MetaModelTable::getIsValid,"0");
        metaModelTableMapper.update(null,metaModelTableWrapper);
        metaColInfoMapper.updateModelColsValid(modelCode);
        LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, logId)
                .set(MetaAssetMqLog::getDataId,tableCode)
                .set(MetaAssetMqLog::getAssetName,tableName);
        metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
        log.info("表资产下线完成");

    }

    


}
