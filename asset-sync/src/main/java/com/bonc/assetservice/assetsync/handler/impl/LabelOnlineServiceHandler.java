package com.bonc.assetservice.assetsync.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.constant.AssetConstant;
import com.bonc.assetservice.assetsync.handler.IAssetDataRegHandler;
import com.bonc.assetservice.assetsync.handler.IdimInfoHandler;
import com.bonc.assetservice.assetsync.model.*;
import com.bonc.assetservice.assetsync.util.GetUtil;
import com.bonc.assetservice.assetsync.util.UrlRequestFactory;
import com.bonc.assetservice.metadata.entity.*;
import com.bonc.assetservice.metadata.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("labelOnlineHandler")
public class LabelOnlineServiceHandler implements IAssetDataRegHandler {

    @Autowired
    private MetaAssetInfoMapper metaAssetInfoMapper;
    @Autowired
    private MetaGrpInfoMapper metaGrpInfoMapper;
    @Autowired
    private MetaAssetMqLogMapper metaAssetMqLogMapper;
    @Autowired
    private MetaModelTableMapper metaModelTableMapper;
    @Autowired
    private MetaColInfoMapper metaColInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MetaDimInfoMapper metaDimInfoMapper;
    @Autowired
    private IdimInfoHandler dimInfoHandler;

    /**
     * 标签资产上线
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assetHandle(AssetDataMsgVO assetDataMsgVO, String logId) {
        log.info("标签资产上线开始---------->");
        //获取额外属性
        log.info("标签资产上线转化后的对象信息为 {}", JSON.toJSON(assetDataMsgVO));
        List<AttrVO> attrList = JSONArray.parseArray(String.valueOf(assetDataMsgVO.getAttrList()), AttrVO.class);
        String assetsId = assetDataMsgVO.getAssetsId();
        String tableOid = assetDataMsgVO.getTableOid();

        String oid = GetUtil.getAttrValue(attrList, ApiValueEnum.OID);
        //查询编码映射表并处理
        List<Map<String, Object>> codeMappingList = metaAssetInfoMapper.selectCodeMapping();
        Map<String, String> codeMappings = codeMappingList.stream().collect(Collectors.toMap(k -> k.get("indexes") + "#" + k.get("assetsCode"), v -> v.get("customCode").toString()));

        //判断模板表中是否有数据，有则更新，没有则插入
        MetaModelTable metaModelTable = metaModelTableMapper.selectByMoldCode(tableOid);
        int i = 0;
        if (metaModelTable != null) {
            i = metaModelTableMapper.updateById(modelTableInfo(metaModelTable, assetDataMsgVO, attrList, codeMappingList));
        } else {
            i = metaModelTableMapper.insert(modelTableInfo(metaModelTable, assetDataMsgVO, attrList, codeMappingList));
        }
        log.info("模板表受影响行数 ------> {}", i);

        //判断字段表中是否有数据，有则更新，没有则插入
        MetaColInfo metaColInfo = metaColInfoMapper.selectMetaColByModelAndCol(tableOid, oid);
        if (metaColInfo != null) {
            i = metaColInfoMapper.updateById(colInfo(metaColInfo, assetDataMsgVO, attrList, codeMappings));
        } else {
           i = metaColInfoMapper.insert(colInfo(new MetaColInfo(), assetDataMsgVO, attrList, codeMappings));
        }

        log.info("字段表受影响行数 ------> {}", i);
        //资产的信息，资产表中存在过该资产则更新，不存在则插入
        LambdaUpdateWrapper<MetaAssetInfo> metaAssetInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        metaAssetInfoLambdaUpdateWrapper.eq(MetaAssetInfo::getPlatAssetId, assetDataMsgVO.getAssetsId())
                .in(MetaAssetInfo::getIsValid,AssetConstant.IS_VALID,AssetConstant.IS_INVALID);
        List<MetaAssetInfo> metaAssetInfoList = metaAssetInfoMapper.selectList(metaAssetInfoLambdaUpdateWrapper);
        MetaAssetInfo metaAssetInfo = assetInfo(new MetaAssetInfo(), assetDataMsgVO, attrList, logId, codeMappingList);
        if(null == metaAssetInfoList || metaAssetInfoList.isEmpty()) {
            i = metaAssetInfoMapper.insert(metaAssetInfo);
        }else {
            if(null == metaAssetInfo.getDimCode()){
                metaAssetInfoLambdaUpdateWrapper.set(MetaAssetInfo::getDimCode,null);
            }
           i = metaAssetInfoMapper.update(metaAssetInfo,metaAssetInfoLambdaUpdateWrapper);
        }
        log.info("资产表受影响行数 ------> {}", i);

        //删除标签树和标签详情的redis缓存
        QueryWrapper<MetaAssetInfo> infoQueryWrapper = new QueryWrapper<MetaAssetInfo>().eq("plat_asset_id", assetsId);
        List<MetaAssetInfo> metaAssetEntityList = metaAssetInfoMapper.selectList(infoQueryWrapper);
        HashSet<String> treeKeys = new HashSet<>();
        for (MetaAssetInfo metaAssetEntity : metaAssetEntityList) {
            String treeKey = "grp#" + metaAssetEntity.getProvCode();
            treeKeys.add(treeKey);
        }
        if (null != treeKeys && !treeKeys.isEmpty()) {
            redisTemplate.delete(treeKeys);
            log.info("删除省份标签树缓存的key -----> {}", treeKeys);
        }
        String detailKey = "label#" + assetsId;
//        Set<String> keys = redisTemplate.keys(detailKey + "*");
//        if (null != keys || !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//            log.info("删除标签资产缓存的key-----> {}", keys);
//        }
        ScanOptions options = ScanOptions.scanOptions().match(detailKey + "*").count(100)
                .build();
        Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                        redisTemplate.getKeySerializer()::deserialize));

        cursor.forEachRemaining(key -> redisTemplate.delete(key));
        log.info("<-----------标签资产上线完成");
    }

    //todo 封装资产信息(待确认字段是否对应准确)
    public MetaAssetInfo assetInfo(MetaAssetInfo metaAssetInfo, AssetDataMsgVO assetDataMsgVO, List<AttrVO> attrList, String logId, List<Map<String, Object>> codeMappingList) {

        Map<String, String> codeMappings = codeMappingList.stream().collect(Collectors.toMap(k -> k.get("indexes") + "#" + k.get("assetsCode"), v -> v.get("customCode").toString()));
        Map<String, String> pageDataMappings = codeMappingList.stream().collect(Collectors.toMap(k -> k.get("indexes") + "#" + k.get("assetsCode"), v -> v.get("pageData").toString()));
        String assetsId = assetDataMsgVO.getAssetsId();
        metaAssetInfo.setPlatAssetId(assetsId);
        String assetCode = GetUtil.getAttrValue(attrList, ApiValueEnum.ASSET_CODE);
        metaAssetInfo.setAssetCode(assetCode);
        MetaModelTable metaModelTable = metaModelTableMapper.selectByMoldCode(assetDataMsgVO.getTableOid());
        MetaColInfo metaColInfo = metaColInfoMapper.selectMetaColByModelAndCol(assetDataMsgVO.getTableOid(), GetUtil.getAttrValue(attrList, ApiValueEnum.OID));
        metaAssetInfo.setModelTableId(metaModelTable.getId());
        metaAssetInfo.setColInfoId(metaColInfo.getId());
        metaAssetInfo.setSampleData(assetDataMsgVO.getSampleValues());
        metaAssetInfo.setAssetName(assetDataMsgVO.getTitle());
        metaAssetInfo.setModelCode(GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_CODE));
        metaAssetInfo.setColCode(GetUtil.getAttrValue(attrList, ApiValueEnum.COL_CODE));
        metaAssetInfo.setAssetType(codeMappings.get(ApiValueEnum.TABLE_TYPE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.TABLE_TYPE)));
        metaAssetInfo.setBusinessType(codeMappings.get(ApiValueEnum.BUSINESS_TYPE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.BUSINESS_TYPE)));
        metaAssetInfo.setBusinessTypeName(pageDataMappings.get(ApiValueEnum.BUSINESS_TYPE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.BUSINESS_TYPE)));

        metaAssetInfo.setAssetInterval(codeMappings.get("PRIV_TABLE_0016" + "#" + assetDataMsgVO.getPRIV_TABLE_0016()));
        metaAssetInfo.setAssetIntervalLogo(pageDataMappings.get("PRIV_TABLE_0016" + "#" + assetDataMsgVO.getPRIV_TABLE_0016()));
        String businessRemark = GetUtil.getAttrValue(attrList, ApiValueEnum.REMARK);
        metaAssetInfo.setBusinessRemark(businessRemark);
        metaAssetInfo.setTecRemark(GetUtil.getAttrValue(attrList, ApiValueEnum.TECHNOLOGY_DESC));
        metaAssetInfo.setTenantCode(assetDataMsgVO.getTenantId());

        metaAssetInfo.setColType(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.FIELD_TYPE));
        String colLength = GetUtil.getAttrValue(attrList, ApiValueEnum.FIELD_LENGTH);
        metaAssetInfo.setColLength(Integer.valueOf(StringUtils.isNotBlank(colLength) ? colLength : ""));
        metaAssetInfo.setLinkId(GetUtil.getAttrValue(attrList, ApiValueEnum.DOMAIN_CODE));
        // 分组的编号
        metaAssetInfo.setGrpId(GetUtil.getAttrValue(attrList, ApiValueEnum.GRP_NAME));
        MetaGrpInfo metaGrpInfo = metaGrpInfoMapper.selectById(GetUtil.getAttrValue(attrList, ApiValueEnum.GRP_NAME));
        metaAssetInfo.setDomainGrpId(metaGrpInfo.getIdPath().split("/")[2]);
        //关联的码表 linkDim
        String linkDim = GetUtil.getAttrValue(attrList, ApiValueEnum.LINK_DIM);
//        DimExternDataVO dimExternData = null;
//        if (StringUtils.isNotBlank(dimCode)) {
//            dimExternData = urlRequestFactory.getDimExternData(dimCode);
//            dimCode = dimExternData == null ? null : dimExternData.getTableCode();
//        }
        metaAssetInfo.setLinkDim(linkDim);
        String dimName = GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.LINK_DIM);
        metaAssetInfo.setDimName(dimName);
        metaAssetInfo.setUnitCode(GetUtil.getAttrValue(attrList, ApiValueEnum.KPI_UNIT));
        //crtUser
        metaAssetInfo.setRegiMan(assetDataMsgVO.getCrtUser());
        //时间处理
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            now = sdf.parse(sdf.format(new Date()));
            metaAssetInfo.setOnlineDate(sdf.parse(sdf.format(new Long(assetDataMsgVO.getOnlineTime()))));
            metaAssetInfo.setOfflineDate(sdf.parse(sdf.format(new Long(assetDataMsgVO.getOfflineTime()))));
            Date regiDate = sdf1.parse(GetUtil.getAttrValue(attrList, ApiValueEnum.REGI_DATE));
            metaAssetInfo.setRegiDate(regiDate);
           // now = sdf.parse(sdf.format(new Date()));
//            if (regiDate.after(now)){
//                metaAssetInfo.setIsValid(AssetConstant.IS_VALID_INITIAL);
//            }else {

//            }
        } catch (Exception e) {
            log.error("上下线时间转换异常:"+e.getMessage());
            //e.printStackTrace();
        }
        metaAssetInfo.setIsValid(AssetConstant.IS_VALID);
        metaAssetInfo.setCreateTime(now);
        metaAssetInfo.setSecurityLevel(GetUtil.getAttrValue(attrList, ApiValueEnum.SECURITY_LEVEL_ASSET));
        metaAssetInfo.setSecurityLevelName(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.SECURITY_LEVEL_ASSET));
        metaAssetInfo.setSecurityType(GetUtil.getAttrValue(attrList, ApiValueEnum.SECURITY_TYPE_ASSET));
        metaAssetInfo.setSecurityTypeName(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.SECURITY_TYPE_ASSET));
        metaAssetInfo.setTableOid(assetDataMsgVO.getTableOid());
        metaAssetInfo.setFieldOid(GetUtil.getAttrValue(attrList, ApiValueEnum.OID));
        metaAssetInfo.setAssetVersionId(assetDataMsgVO.getAssetVersionId());
        metaAssetInfo.setAssetVersionNo(assetDataMsgVO.getAssetVersionNo());
        metaAssetInfo.setColSort(GetUtil.getAttrValue(attrList, ApiValueEnum.COL_SORT));
        //省份信息查询租户省份对应表(没有省份信息就设为-1)
        Map<String, Object> provData = metaAssetInfoMapper.selectProvCode(assetDataMsgVO.getTenantId());
        if (null != provData && !provData.isEmpty() && !"".equals(String.valueOf(provData.get("prov_code")))) {
            metaAssetInfo.setProvCode(String.valueOf(provData.get("prov_code")));
        } else {
            metaAssetInfo.setProvCode("-1");
        }
        String codeName = GetUtil.getAttrValue(attrList, ApiValueEnum.CODE_NAME);
        Long dimId = null;
        if (null != codeName && !"".equals(codeName)) {
            String[] split = codeName.split("-");
            Map<String, Object> params = new HashMap<>();
            params.put("code_field", split[0]);
            params.put("name_field", split[1]);
            params.put("dim_table_code", dimName);

            dimId = metaAssetInfoMapper.selectDimInfo(params);

            if (null == dimId || "".equals(dimId)) {
                List<MetaDimInfo> metaDimInfoList = new ArrayList<>();
                MetaDimInfo metaDimInfo = MetaDimInfo.builder().id(null).codeField(split[0]).nameField(split[1]).dimTableCode(dimName).tableOid(linkDim).build();
                metaDimInfoMapper.insert(metaDimInfo);
                dimId = metaAssetInfoMapper.selectDimInfo(params);
                metaDimInfo.setId(Long.valueOf(dimId));
                //开启异步任务，静态码表同步
                metaDimInfoList.add(metaDimInfo);
                dimInfoHandler.syncAssetHandle(metaDimInfoList);
            }
        }
        //如果报文不带codeName，需要把dim_code置为空
        metaAssetInfo.setDimCode(dimId);
        metaAssetInfo.setSortField(GetUtil.getAttrValue(attrList, ApiValueEnum.SORT_FIELD));

        String sortType = GetUtil.getAttrValue(attrList, ApiValueEnum.SORT_TYPE);
        metaAssetInfo.setSortType(null != sortType && !"".equals(sortType) ? sortType : "1");

        //更新MQ消息表
//        LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, logId)
//                .set(MetaAssetMqLog::getDimJson,dimExternData == null ? null : dimExternData.getReturnJson())
//                .set(MetaAssetMqLog::getAssetName,assetDataMsgVO.getAssetsId());
//        metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
        //先查询redis中是否有当前资产的数据，有则删除
        String redisData = redisTemplate.opsForValue().get(assetsId);
        if (StringUtils.isNotBlank(redisData)) {
            redisTemplate.delete(assetsId);
        }
        String jsonString = JSONObject.toJSONString(metaAssetInfo);
        redisTemplate.opsForValue().set(assetsId, jsonString);
        log.info("向redis中存储：{}", jsonString);
        String redisValue = redisTemplate.opsForValue().get(assetsId);
        log.info("redis中取数据：{}", redisValue);
        return metaAssetInfo;
    }

    //todo 封装模板表信息(待确认字段是否对应准确)
    public MetaModelTable modelTableInfo(MetaModelTable metaModelTable, AssetDataMsgVO assetDataMsgVO, List<AttrVO> attrList, List<Map<String, Object>> codeMappingList) {
        //模板表存在则不更新上下线时间，不存在才去更新
        if (metaModelTable == null) {
            metaModelTable = new MetaModelTable();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                metaModelTable.setOnlineDate(sdf.parse(sdf.format(new Long(assetDataMsgVO.getOnlineTime()))));
                metaModelTable.setOfflineDate(sdf.parse(sdf.format(new Long(assetDataMsgVO.getOfflineTime()))));
            } catch (Exception e) {
                log.error("资产上线时间转换异常:"+e.getMessage());
               // e.printStackTrace();
            }
        }
        Map<String, String> codeMappings = codeMappingList.stream().collect(Collectors.toMap(k -> k.get("indexes") + "#" + k.get("assetsCode"), v -> v.get("customCode").toString()));
        Map<String, String> typeMappings = codeMappingList.stream().collect(Collectors.toMap(k -> k.get("indexes") + "#" + k.get("assetsCode"), v -> v.get("type").toString()));
        Map<String, String> pageDataMappings = codeMappingList.stream().collect(Collectors.toMap(k -> k.get("indexes") + "#" + k.get("assetsCode"), v -> v.get("pageData").toString()));
        metaModelTable.setModelCode(GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_CODE));
        metaModelTable.setIsValid(AssetConstant.IS_VALID);
        metaModelTable.setIndbFileName(assetDataMsgVO.getIndbFileName());
        metaModelTable.setModelName(GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_NAME));
        String isMaster = codeMappings.get(ApiValueEnum.IS_MASTER.getValue() + "#" + assetDataMsgVO.getPRIV_TABLE_0001());
        metaModelTable.setIsMaster((null != isMaster && !"".equals(isMaster)) ? isMaster : "0");
        metaModelTable.setTenantCode(assetDataMsgVO.getTenantId());
        metaModelTable.setModelType(typeMappings.get(ApiValueEnum.TABLE_TYPE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.TABLE_TYPE)));
        metaModelTable.setModelInterval(codeMappings.get("PRIV_TABLE_0016" + "#" + assetDataMsgVO.getPRIV_TABLE_0016()));
        metaModelTable.setModelIntervalLogo(pageDataMappings.get("PRIV_TABLE_0016" + "#" + assetDataMsgVO.getPRIV_TABLE_0016()));
        metaModelTable.setLinkId(GetUtil.getAttrValue(attrList, ApiValueEnum.DOMAIN_CODE));
        metaModelTable.setBusinessType(codeMappings.get(ApiValueEnum.BUSINESS_TYPE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.BUSINESS_TYPE)));
        metaModelTable.setBusinessTypeName(pageDataMappings.get(ApiValueEnum.BUSINESS_TYPE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.BUSINESS_TYPE)));
        metaModelTable.setTableOid(assetDataMsgVO.getTableOid());
        MetaGrpInfo metaGrpInfo = metaGrpInfoMapper.selectById(GetUtil.getAttrValue(attrList, ApiValueEnum.GRP_NAME));
        metaModelTable.setDomainGrpId(metaGrpInfo.getIdPath().split("/")[2]);
        //省份信息查询租户省份对应表
        Map<String, Object> provData = metaAssetInfoMapper.selectProvCode(assetDataMsgVO.getTenantId());
        if (null != provData && !provData.isEmpty()) {
            metaModelTable.setProvCode(String.valueOf(provData.get("prov_code")));
        }else {
            metaModelTable.setProvCode("-1");
        }
        return metaModelTable;
    }

    //todo 封装字段表信息(待确认字段是否对应准确)
    public MetaColInfo colInfo(MetaColInfo metacolInfo, AssetDataMsgVO assetDataMsgVO, List<AttrVO> attrList, Map<String, String> codeMappings) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            metacolInfo.setOnlineDate(sdf.parse(sdf.format(new Long(assetDataMsgVO.getOnlineTime()))));
            metacolInfo.setOfflineDate(sdf.parse(sdf.format(new Long(assetDataMsgVO.getOfflineTime()))));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        MetaModelTable metaModelTable = metaModelTableMapper.selectByMoldCode(assetDataMsgVO.getTableOid());
        metacolInfo.setModelTableId(metaModelTable.getId());
        metacolInfo.setColCode(GetUtil.getAttrValue(attrList, ApiValueEnum.COL_CODE));
        metacolInfo.setModelCode(GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_CODE));
        metacolInfo.setIsValid(AssetConstant.IS_VALID);
        metacolInfo.setColName(GetUtil.getAttrValue(attrList, ApiValueEnum.COL_NAME));
        metacolInfo.setTenantCode(assetDataMsgVO.getTenantId());
        metacolInfo.setComments(GetUtil.getAttrValue(attrList, ApiValueEnum.COMMENTS));
        metacolInfo.setBusinessRemark(GetUtil.getAttrValue(attrList, ApiValueEnum.REMARK));
        metacolInfo.setTecRemark(GetUtil.getAttrValue(attrList, ApiValueEnum.TECHNOLOGY_DESC));
        String isMasterKey = codeMappings.get(ApiValueEnum.IS_MASTER_KEY.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.IS_MASTER_KEY));
        metacolInfo.setIsMasterKey((null != isMasterKey && !"".equals(isMasterKey)) ? isMasterKey : "0");
        metacolInfo.setValueUnit(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.KPI_UNIT));
        metacolInfo.setColType(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.FIELD_TYPE));
        String colLength = GetUtil.getAttrValue(attrList, ApiValueEnum.FIELD_LENGTH);
        metacolInfo.setColLength(Integer.valueOf(StringUtils.isNotBlank(colLength) ? colLength : ""));
        metacolInfo.setIsDate(codeMappings.get(ApiValueEnum.IS_DATE.getValue() + "#" + GetUtil.getAttrValue(attrList, ApiValueEnum.IS_DATE)));
        metacolInfo.setDateFormat(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.DATE_FORMAT));
        metacolInfo.setSecurityType(GetUtil.getAttrValue(attrList, ApiValueEnum.SECURITY_TYPE_ASSET));
        metacolInfo.setSecurityTypeName(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.SECURITY_TYPE_ASSET));
        metacolInfo.setSecurityLevel(GetUtil.getAttrValue(attrList, ApiValueEnum.SECURITY_LEVEL_ASSET));
        metacolInfo.setSecurityLevelName(GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.SECURITY_LEVEL_ASSET));
        metacolInfo.setFieldOid(GetUtil.getAttrValue(attrList, ApiValueEnum.OID));
        metacolInfo.setTableOid(assetDataMsgVO.getTableOid());
        return metacolInfo;
    }
}
