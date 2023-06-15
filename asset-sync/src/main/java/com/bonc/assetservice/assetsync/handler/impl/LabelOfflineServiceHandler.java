package com.bonc.assetservice.assetsync.handler.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.constant.AssetConstant;
import com.bonc.assetservice.assetsync.handler.IAssetDataRegHandler;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.assetsync.model.AssetDataMsgVO;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttrVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;
import com.bonc.assetservice.assetsync.util.GetUtil;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;
import com.bonc.assetservice.metadata.entity.MetaColInfo;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import com.bonc.assetservice.metadata.mapper.MetaAssetInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaColInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service("labelOfflineHandler")
public class LabelOfflineServiceHandler implements IAssetDataRegHandler {

    @Autowired
    private MetaAssetInfoMapper metaAssetInfoMapper;
    @Autowired
    private MetaModelTableMapper metaModelTableMapper;
    @Autowired
    private MetaColInfoMapper metaColInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 标签资产下线
     * @param assetDataMsgVO
     * @param logId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assetHandle(AssetDataMsgVO assetDataMsgVO, String logId) {
        log.info("标签资产下线开始------->");
        //额外属性
        List<AttrVO> attrList = JSONArray.parseArray(String.valueOf(assetDataMsgVO.getAttrList()),AttrVO.class);
        String assetsId = assetDataMsgVO.getAssetsId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date offlineTime = null;
        try {
          String offlineDate = sdf.format(new Long(assetDataMsgVO.getOfflineTime()));
            offlineTime = sdf.parse(offlineDate);
        } catch (Exception e) {
            log.error("资产下线-下线时间异常:"+e.getMessage());
           //  e.printStackTrace();
        }
        String oid = GetUtil.getAttrValue(attrList, ApiValueEnum.OID);
        String tableOid = assetDataMsgVO.getTableOid();

        //删除标签树和标签详情的redis缓存
        QueryWrapper<MetaAssetInfo> infoQueryWrapper = new QueryWrapper<MetaAssetInfo>().eq("plat_asset_id", assetsId);
        List<MetaAssetInfo> metaAssetEntityList = metaAssetInfoMapper.selectList(infoQueryWrapper);
        HashSet<String> treeKeys = new HashSet<>();
        for (MetaAssetInfo metaAssetEntity : metaAssetEntityList) {
            String treeKey = "grp#"+metaAssetEntity.getProvCode();
            treeKeys.add(treeKey);
        }
        if(null!=treeKeys && !treeKeys.isEmpty()){
            redisTemplate.delete(treeKeys);
            log.info("删除省份标签树缓存的key -----> {}", treeKeys);
        }
        String detailKey = "label#"+assetsId;
        ScanOptions options = ScanOptions.scanOptions().match(detailKey + "*").count(100)
                .build();
        Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                        redisTemplate.getKeySerializer()::deserialize));

        cursor.forEachRemaining(key -> redisTemplate.delete(key));

        //资产下线（临时下线或者永久删除）
        if (AssetConstant.OFFLINE.equals(assetDataMsgVO.getStatus())){
            LambdaUpdateWrapper<MetaAssetInfo> assetLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            assetLambdaUpdateWrapper.eq(MetaAssetInfo::getPlatAssetId,assetsId)
                    .eq(MetaAssetInfo::getIsValid,AssetConstant.IS_VALID)
                    .set(MetaAssetInfo::getIsValid,AssetConstant.IS_INVALID)
                    .set(MetaAssetInfo::getOfflineDate,offlineTime);
            int assetCount = metaAssetInfoMapper.update(null, assetLambdaUpdateWrapper);
            log.info("资产下线条数：{}",assetCount);
        }else {
            LambdaUpdateWrapper<MetaAssetInfo> assetLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            assetLambdaUpdateWrapper.eq(MetaAssetInfo::getPlatAssetId,assetsId)
                    .set(MetaAssetInfo::getIsValid,AssetConstant.IS_VALID_DELETE)
                    .set(MetaAssetInfo::getOfflineDate,offlineTime);
            int assetCount = metaAssetInfoMapper.update(null, assetLambdaUpdateWrapper);
            log.info("资产删除条数：{}",assetCount);
            //查询该资产的字段还有没有标签资产是未删除状态
            LambdaQueryWrapper<MetaAssetInfo> metaAssetInfoQueryWrapper = new LambdaQueryWrapper<>();
            metaAssetInfoQueryWrapper.eq(MetaAssetInfo::getFieldOid,GetUtil.getAttrValue(attrList, ApiValueEnum.OID))
                    .ne(MetaAssetInfo::getIsValid,AssetConstant.IS_VALID_DELETE);
            List<MetaAssetInfo> metaAssetInfos = metaAssetInfoMapper.selectList(metaAssetInfoQueryWrapper);
            if (metaAssetInfos == null || metaAssetInfos.size() == 0) {
                //字段软删除
                LambdaUpdateWrapper<MetaColInfo> metaColInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                metaColInfoLambdaUpdateWrapper.eq(MetaColInfo::getFieldOid, oid)
                        .set(MetaColInfo::getIsValid, AssetConstant.IS_VALID_DELETE)
                        .set(MetaColInfo::getOfflineDate, offlineTime);
                int colCount = metaColInfoMapper.update(null, metaColInfoLambdaUpdateWrapper);
                log.info("字段表删除条数：{}", colCount);
            }
            //查询该资产的模板表是否还有标签资产是未删除状态
            LambdaQueryWrapper<MetaAssetInfo> metaAssetLambdaQueryWrapper = new LambdaQueryWrapper<>();
            metaAssetLambdaQueryWrapper.eq(MetaAssetInfo::getTableOid,assetDataMsgVO.getTableOid())
                    .ne(MetaAssetInfo::getIsValid,AssetConstant.IS_VALID_DELETE);
            List<MetaAssetInfo> metaAssetInfoList = metaAssetInfoMapper.selectList(metaAssetLambdaQueryWrapper);
            if (metaAssetInfoList == null || metaAssetInfoList.size() == 0) {
                //模板表下线
                LambdaUpdateWrapper<MetaModelTable> metaModelTableLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                metaModelTableLambdaUpdateWrapper.eq(MetaModelTable::getTableOid, tableOid)
                        .set(MetaModelTable::getIsValid, AssetConstant.IS_VALID_DELETE)
                        .set(MetaModelTable::getOfflineDate, offlineTime);
                int modelCount = metaModelTableMapper.update(null, metaModelTableLambdaUpdateWrapper);
                log.info("模板表删除条数：{}", modelCount);
            }
        }
        //删除redis中的缓存
        Boolean aBoolean = redisTemplate.delete(assetsId);
        log.info("redis删除缓存结果：{}",aBoolean);
        log.info("<---------标签资产下线结束");
    }
}
