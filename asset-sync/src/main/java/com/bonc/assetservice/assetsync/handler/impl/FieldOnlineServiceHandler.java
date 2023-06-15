package com.bonc.assetservice.assetsync.handler.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttrVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;
import com.bonc.assetservice.assetsync.model.DimExternDataVO;
import com.bonc.assetservice.assetsync.util.GetUtil;
import com.bonc.assetservice.assetsync.util.UrlRequestFactory;
import com.bonc.assetservice.metadata.entity.MetaAssetInfo;
import com.bonc.assetservice.metadata.entity.MetaAssetMqLog;
import com.bonc.assetservice.metadata.entity.MetaColInfo;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import com.bonc.assetservice.metadata.mapper.MetaAssetInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaAssetMqLogMapper;
import com.bonc.assetservice.metadata.mapper.MetaColInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service("fieldOnlineHandler")
public class FieldOnlineServiceHandler implements IAssetRegisterHandler {


    @Autowired
    private UrlRequestFactory urlRequestFactory;

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
     * 字段资产上线
     * @param assetsVO
     * @param detailData
     * @param logId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void assetHandle(AssetsMsgVO assetsVO, AttributeDetailDataVO detailData, String logId) {
        log.info("字段上线开始");
        //获取额外属性
        List<AttrVO> attrList = detailData.getAttrList();
        String assetsId = assetsVO.getAssetsId();
        String modelCode = assetsVO.getTableAssetsId();
        String fieldCode = GetUtil.getAttrValue(attrList, ApiValueEnum.COL_CODE);
        //判断模板表是存在,不存在 需要插入模板表
        MetaModelTable metaModelTable = metaModelTableMapper.selectByMoldCode(modelCode);

        DimExternDataVO dimExternData = null;
        if(metaModelTable == null){
             metaModelTable=new MetaModelTable();
            //模板表不存在,那就需要去处理创建模板表插入进去,然后再发布这个标签的资产
        }else{
           //查询 这个表的字段详情
            MetaColInfo metaCol=metaColInfoMapper.selectMetaColByModelAndCol(modelCode,fieldCode);
            //资产的信息
            MetaAssetInfo metaAssetInfo=metaAssetInfoMapper.selectMetaAssetInfoByAssetCode(assetsId);
            metaAssetInfo=metaAssetInfo==null?new MetaAssetInfo():metaAssetInfo;
            metaAssetInfo.setAssetCode(assetsId);
            metaAssetInfo.setAssetName(metaCol.getColName());
            metaAssetInfo.setModelCode(modelCode);
            metaAssetInfo.setColCode(metaCol.getColCode());
            //asset_type 资产类型
            //metaAssetInfo.setAssetType();
            metaAssetInfo.setBusinessType(metaModelTable.getBusinessType());
            metaAssetInfo.setAssetInterval(metaModelTable.getModelInterval());
            String businessRemark = GetUtil.getAttrValue(attrList, ApiValueEnum.REMARK);
            metaAssetInfo.setBusinessRemark(businessRemark);
            metaAssetInfo.setTecRemark(GetUtil.getAttrValue(attrList, ApiValueEnum.TECHNOLOGY_DESC));

            metaAssetInfo.setColType(metaCol.getColType());
            metaAssetInfo.setColLength(metaCol.getColLength());
            metaAssetInfo.setDomainGrpId(metaModelTable.getDomainGrpId());
            // 分组,获取名称,查找分组的编号
            String grpName = GetUtil.getAttrValue(attrList, ApiValueEnum.GRP_NAME);
            metaAssetInfo.setGrpId(grpName);
            //关联的码表 linkDim
            String dimCode = GetUtil.getAttrValue(attrList, ApiValueEnum.LINK_DIM);
            if (StringUtils.isNotBlank(dimCode)) {
                dimExternData = urlRequestFactory.getDimExternData(dimCode);
                dimCode = dimExternData == null ? null : dimExternData.getTableCode();
            }
            metaAssetInfo.setDimCode(Long.valueOf(dimCode));
            //unit_code  FIELD_0012 单位的编号,需要去处理
            String unit = GetUtil.getAttrValue(attrList, ApiValueEnum.KPI_UNIT);
            metaAssetInfo.setUnitCode(unit);
            //crtUser
            metaAssetInfo.setRegiMan(detailData.getCrtUser());
            metaAssetInfo.setRegiDate(new Date());

            metaAssetInfo.setOnlineDate(metaModelTable.getOnlineDate());
            metaAssetInfo.setOfflineDate(metaModelTable.getOfflineDate());
            metaAssetInfo.setIsValid("1");

            metaAssetInfo.setSecurityLevel(metaCol.getSecurityLevel());
            metaAssetInfo.setSecurityType(metaCol.getSecurityType());

            if(metaAssetInfo.getId()!=null){
                metaAssetInfoMapper.updateById(metaAssetInfo);
            }else{
                metaAssetInfoMapper.insert(metaAssetInfo);
            }
        }
        LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, logId)
                .set(MetaAssetMqLog::getDimJson,dimExternData == null ? null : dimExternData.getReturnJson())
                .set(MetaAssetMqLog::getAssetName,assetsId);
        metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);

       log.info("字段上线完成");
    }

    /**
     *   //字段资产
     *         //获取包id
     *         String dataId = mapper.selectDatapackageMsgOidByAssetId(assetsVO.getTableAssetsId());
     *         // 数据资产编码
     *         String assetId = GetUtil.getAttrValue(attrList, ApiValueEnum.ASSET_ID);
     *         // 判断资产是否存, >0 就存在
     *         Integer isExistAssetInfo = mapper.selectAssetInfoByAssetCode(assetId);
     *         //字段的序号
     *         Integer currFieldOrder   = mapper.selectcurrFieldOrder(dataId, fieldCode);
     *         //数据资产的更新周期
     *         String dataInterval      = mapper.selectDatapackageMsgDataIntervalByOid(dataId);
     *         //表的数据类型
     *         String tableType         = mapper.selectDatapackageMsgTableTypeByOid(dataId);
     *         //查询资产的省分编号
     *         String provId            = mapper.selectDatapackageMsgProvIdByOid(dataId);
     *         //资产的有效时间
     *         Date date                = mapper.selectDatapackageMsgValiTimeByOid(dataId);
     *
     *         MdAssetsInfo info = new MdAssetsInfo();
     *         //设置字段info参数
     *         info.setDataId(dataId);
     *         info.setAssetId(assetId);
     *         String assetType = GetUtil.getAttrValue(attrList, ApiValueEnum.ASSET_TYPE);
     *         info.setAssetTypes(SwitchFactory.assetType(assetType));
     *         info.setRegiMan(detailData.getCrtUser());
     *         info.setValiTime(date);
     *         info.setEndTime(DateUtil.parse(detailData.getOfflineTime()));
     *         info.setPersonInCharge(detailData.getCrtUser());
     *         info.setDeleteFlag(0D);
     *         info.setRegiType("0");
     *         info.setOrderNo(currFieldOrder);
     *         info.setKpiUnitType(SwitchFactory.kpiType(GetUtil.getAttrValue(attrList, ApiValueEnum.KPI_UNIT)));
     *         info.setAssetCode(fieldCode);
     *         info.setAssetName(detailData.getTitle());
     *         String domainAndGrp = GetUtil.getAttrAnalyticValue(attrList, ApiValueEnum.GRP_NAME.getValue());
     *         //域和组名称转id
     *         if (StringUtils.isNotBlank(domainAndGrp) && domainAndGrp.contains("/")) {
     *             String[] split = domainAndGrp.split("/");
     *             String domainName = split[1];
     *             if (domainName != null) {
     *                 domainName = domainName.trim();
     *                 String domainId = mapper.selectDomainIdByName(domainName);
     *                 info.setDomainId(domainId);
     *             }
     *             String grpName = "";
     *             if (split.length == 3) {
     *                 grpName = split[2];
     *             }
     *             if (split.length == 4) {
     *                 grpName = split[3];
     *             }
     *             if (grpName != null) {
     *                 grpName = grpName.trim();
     *                 String grId = mapper.selectGrpIdByName(grpName);
     *                 info.setGrpId(grId);
     *             }
     *         }
     *         info.setRemark(GetUtil.getAttrValue(attrList, ApiValueEnum.REMARK));
     *         String fieldType = GetUtil.getAttrValue(attrList, ApiValueEnum.FIELD_TYPE);
     *         info.setFieldType(SwitchFactory.fieldType(fieldType));
     *         info.setFieldLength(GetUtil.getAttrValue(attrList, ApiValueEnum.FIELD_LENGTH));
     *         String securityLevel = GetUtil.getAttrValue(attrList, ApiValueEnum.SECURITY_LEVEL);
     *         info.setSecurityLevel(Double.valueOf(securityLevel == null ? "3" : securityLevel));
     *         info.setIsNew("1");
     *         info.setDataInterval(dataInterval);
     *         //todo 维护人信息
     *         info.setTechnologyDesc(GetUtil.getAttrValue(attrList, ApiValueEnum.TECHNOLOGY_DESC));
     *         String isPrimarykey = SwitchFactory.isPrimarykey(GetUtil.getAttrValue(attrList, ApiValueEnum.IS_PRIMARYKEY));
     *         if (StringUtils.isNotBlank(isPrimarykey)) {
     *             info.setIsPrimarykey(isPrimarykey);
     *         } else {
     *             info.setIsPrimarykey("0");
     *         }
     *         mapper.insertTmpAssetInfo(info);
     *         //删除当前已存字段
     *         List<String> fieldCodes = new ArrayList<>();
     *         fieldCodes.add(fieldCode);
     *         mapper.deleteFieldByDataIdAndFieldCode(dataId, fieldCodes, assetId);
     *         if (isExistAssetInfo != null && isExistAssetInfo > 0) {
     *             List<String> fieldCodes2 = new ArrayList<>();
     *             fieldCodes2.add(info.getAssetId());
     *             mapper.deleteAssetRegion(fieldCodes2, dataId);
     *             mapper.deleteAssetInfo(fieldCodes2);
     *         }
     *         //获取关联维度码表表名
     *         String linkDimCode = GetUtil.getAttrValue(attrList, ApiValueEnum.LINK_DIM);
     *         String dimTableCode = null;
     *        // DimExternDataVO dimExternData = null;
     *         if (StringUtils.isNotBlank(linkDimCode)) {
     *             dimExternData = urlRequestFactory.getDimExternData(linkDimCode);
     *             dimTableCode = dimExternData == null ? null : dimExternData.getTableCode();
     *         }
     *         this.doWork(mapper, dataId, assetId, dimTableCode);
     *
     *         //是纵表，添加指标集
     *         if ("T".equals(tableType)) {
     *             String domainId = info.getDomainId();
     *             String s = SwitchFactory.setGrp(domainId);
     *             String setGrp = domainId + "-" + s;
     *             mapper.insertAssetInfoBySet(dataId, setGrp);
     *             String assetSetId = "AS" + dataId.substring(3);
     *             Integer num = mapper.selectAssetSetBySetId(assetSetId);
     *             if (num != null && num == 0) {
     *                 String rang = "051";
     *                 if (!"051".equals(provId)) {
     *                     rang = "000";
     *                 }
     *                 mapper.insertSetDomainAndTimeSet(assetSetId, dataInterval, provId, rang);
     *             }
     *             mapper.deleteSetByInfo(info.getAssetId());
     *             mapper.insertSetByInfo(dataId);
     *         }
     *
     *         //更新 MQ消息记录的日志
     *        mapper.updateMqLog(MdAssetMqLog.builder().setOid(logId).setDimJson(dimExternData == null ? null : dimExternData.getReturnJson()).setAssetName(assetId).build());
      */



    /**
     * 表资产的注册到源数据库中
     * @param mapper
     * @param msgOid
     * @param assetId
     * @param dimExtern
     */
//    public void doWork(AssetRegisterFileMapper mapper, String msgOid, String assetId, String dimExtern) {
//        mapper.insertAssetInfo(msgOid);
////        将所有资产，包括未提供的入库字段字段，插入中间表 --将新数据包的资产插入中间表;
//        mapper.insertMidAssetCol(msgOid);
////        确保入库资产，字段名字不重复，插入入库表;
//        mapper.insertAssetRegionIsField(assetId);
////        资产映射;
//        mapper.insertAssetMapping();
////        将新资产的信息同步到META_STRUCT_FILED_CURRENT,MD_ASSETS_TABLE_COL --更新字段表;
//        mapper.insertStructFieldCurrent(msgOid);
//        mapper.deleteOtherTableCol(msgOid);
////        更新table_col;
//        mapper.insertAssetTableCol(msgOid);
//        if (dimExtern != null) {
////          添加维度码表到码表配置表;
//            mapper.insertDimensionCurrent(msgOid, dimExtern);
//        }
////       指标资产单位配置;
//        mapper.insertStandKpiCurrent(msgOid);
////       插入资产分组信息;
//        mapper.insertAssetGrpInfo(msgOid);
//
//    }

}
