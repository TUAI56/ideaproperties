package com.bonc.assetservice.assetsync.handler.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.constant.ProvinceEnum;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.assetsync.model.*;
import com.bonc.assetservice.assetsync.util.GetUtil;
import com.bonc.assetservice.assetsync.util.SwitchFactory;
import com.bonc.assetservice.assetsync.util.UrlRequestFactory;
import com.bonc.assetservice.metadata.entity.MetaAssetMqLog;
import com.bonc.assetservice.metadata.entity.MetaColInfo;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import com.bonc.assetservice.metadata.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("tableOnlineHandler")
public class TableOnlineServiceHandler implements IAssetRegisterHandler {


    @Autowired
    private MetaAssetMqLogMapper metaAssetMqLogMapper;

    //模版表
    @Autowired
    private MetaModelTableMapper metaModelTableMapper;
    //域定义表
    @Autowired
    private MetaDomainDefineMapper metaDomainDefineMapper;

    //实体表
    @Autowired
    private MetaEntityTableMapper metaEntityTableMapper;

    //实体表数据库信息表
    @Autowired
    private MetaDbInfoMapper metaDbInfoMapper;

    //模板表字段信息表
    @Autowired
    private MetaColInfoMapper metaColInfoMapper;

    @Autowired
    private UrlRequestFactory urlRequestFactory;

    /**
     * 表资产上线
     * //TODO 1.插入模板表数据
     *        2.插入模板表里的字段信息,存在就需要对比进行更新,不存在就需要插入到表中
     *        3.创建 实体表或修改实体表
     *        4.发布标签资产，
     *        5.同步 模板配置表的数据信息(目前还不知道是什么形式同步过来)
     * @param assetsVO
     * @param detailData
     * @param logId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void assetHandle(AssetsMsgVO assetsVO, AttributeDetailDataVO detailData, String logId) {
        log.info("开始表资产上线");
        //获取额外属性
        List<AttrVO> attrList = detailData.getAttrList();
        //表资产id 也是模板表的编号
        String modelCode = assetsVO.getAssetsId();
        //获取表字段信息
        DetailExternalVO detailField = urlRequestFactory.getDetailExternalVO(modelCode);
        MetaModelTable modelTable=metaModelTableMapper.selectByMoldCode(modelCode);
        //是新的模板表
        if(modelTable == null){
            modelTable=new MetaModelTable();
        }
        modelTable.setModelCode(assetsVO.getAssetsId());
        //表的名称
        String tableName = GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_NAME);
        modelTable.setModelName(tableName);
        //xingyi调整：tableCode原计划存储实体表编码。
        //            因数据库调整，在entity_table中引用model_table_id，model_table中删除table_code字段
//        //获取表编码
//        String tableCode = GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_CODE);
//        modelTable.setTableCode(tableCode);
        //文件名称
        String fileNames = GetUtil.getAttrValue(attrList, ApiValueEnum.FILE_NAMES);
        //TODO 如何通过获取的文件名称,查找flieCode 以及处理的逻辑
        //modelTable.setFileCode(fileNames);
        //存储类型 ：横表 纵表  //表类型
        String tableType = GetUtil.getAttrValue(attrList, ApiValueEnum.TABLE_TYPE);
        modelTable.setModelType(tableType);
        //TODO business_type 没有找到
        String business_type="移网";
        modelTable.setBusinessType(business_type);
        //数据更新周期
        String tableInterval = SwitchFactory.dataInterval(GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_INTERVAL));
        modelTable.setModelInterval(tableInterval);
        //租户编号  tenant_code TENANT_ID
        String tenantCode = GetUtil.getAttrValue(attrList, ApiValueEnum.TENANT_ID);
        modelTable.setTenantCode(tenantCode);
        //省分编号
        modelTable.setProvCode(ProvinceEnum.getId(GetUtil.getAttrValue(attrList, ApiValueEnum.PROV_NAME)));
        //是否域主表
        String isMaster = SwitchFactory.isNot(GetUtil.getAttrValue(attrList, ApiValueEnum.IS_MASTER));
        modelTable.setIsMaster(isMaster);
        // it 资产项目 GSITXM_CODE
        modelTable.setItCode(GetUtil.getAttrValue(attrList, ApiValueEnum.GSITXM_CODE));
        //表所属域的编号
        String domainName = GetUtil.getAttrValue(attrList, ApiValueEnum.DOMAIN_NAME);
        domainName=domainName==null?"":domainName.trim();
        String domainCode = metaDomainDefineMapper.selectDomainCodeByName(domainName);
        //域编号
        modelTable.setDomainGrpId(StringUtils.isBlank(domainCode)? domainName : domainCode);
        //所属数据层
       modelTable.setDwCode(GetUtil.getAttrValue(attrList, ApiValueEnum.GSSJC));
       Date onlineTime = StringUtils.isBlank(detailData.getOnlineTime())?new Date(): DateUtil.parse(detailData.getOnlineTime(), DatePattern.NORM_DATETIME_PATTERN);
       Date offlineTime =StringUtils.isBlank(detailData.getOfflineTime())?new Date(): DateUtil.parse(detailData.getOfflineTime(), DatePattern.NORM_DATETIME_PATTERN);
       modelTable.setOnlineDate(onlineTime);
       modelTable.setOfflineDate(offlineTime);
       //设置有效性
       modelTable.setIsValid("1");
       List<DetailExternalDataBodyVO> body = detailField.getData().getBody();
        List<MetaColInfo> metaColInfos=null;
       //如果是新表,就插入模版表信息
       if(modelTable.getId()==null){
           metaModelTableMapper.insert(modelTable);
           //插入实体类表
           //metaEntityTableMapper.insert(entityTable);
           //TODO 拼接创建表的建表语句 ,存放到实体表中
           metaColInfos = body.stream().map(e -> {
               MetaColInfo col = new MetaColInfo();
               col.setColCode(e.getFieldCode());
               col.setColName(e.getFieldName());
               col.setModelCode(modelCode);
               col.setColType(e.getFieldType());
               // 得查看返回的长度的值是什么类型
               col.setColLength(e.getFieldLength()==null?0:Integer.parseInt(e.getFieldLength()));
               //字段的精度 这个字段没有，看是否再长度里面 去截取的
               //col.setPrecisions();
               //默认值 数据也没有
               //col.setDefaultValue("");
               col.setColSort(e.getOrd());
               col.setTenantCode(tenantCode);
               col.setIsPaimary(e.getSFYZJ());
               col.setOnlineDate(onlineTime);
               col.setOfflineDate(offlineTime);
               //默认上线设置为有效
               col.setIsValid("1");
               col.setComments(e.getComments());
               col.setBusinessRemark(e.getYWKJ());
               col.setTecRemark(e.getJSKJ());
               col.setSecurityLevel(e.getBMDJ());
               //col.setSecurityType();
               //col.setIsNullable();
               //col.setValueType();
               col.setValueUnit(e.getDW());
               col.setIsDate(e.getSFZQZD());
               col.setIsMasterKey(SwitchFactory.isPrimarykey(e.getSFYZJ()));
               //col.setDateFormat();
               //col.setIsArea(e.get)
               return col;
           }).collect(Collectors.toList());
//           metaColInfoMapper.insertBatch(metaColInfos);
       }else{
           metaModelTableMapper.updateById(modelTable);
           //判断旧的实体表中 字段与现有的字段的对比,
           QueryWrapper<MetaColInfo> wrapper=new QueryWrapper<>();
           wrapper.eq("model_code",modelCode);
           //模板表字段源有的表中的字段
           List<MetaColInfo> metaCols=metaColInfoMapper.selectList(wrapper);
           List<String> oldcols = metaCols.stream().map(MetaColInfo::getColCode).collect(Collectors.toList());
           List<String> newcols = body.stream().map(DetailExternalDataBodyVO::getFieldCode).collect(Collectors.toList());
           //旧库里面有, 新的库里面没有,就去设定为失效状态,需要下线
           List<String> validCols = oldcols.stream().filter(e -> !newcols.contains(e)).collect(Collectors.toList());
           if(validCols.size()>0) {
               metaColInfoMapper.updateMetacolsValid(validCols, modelCode,"0");
           }
           //旧字段失效,新字段里面有的,需要进行更新.....
           List<String> saveCols = oldcols.stream().filter(e -> newcols.contains(e)).collect(Collectors.toList());
           if(saveCols.size()>0) {
               metaColInfoMapper.updateMetacolsValid(saveCols, modelCode,"1");
           }
           //新字段里面有，旧表中没有的就插入字段信息表
           metaColInfos = body.stream().filter(e -> !oldcols.contains(e.getFieldCode())).map(e -> {
               MetaColInfo col = new MetaColInfo();
               col.setColCode(e.getFieldCode());
               col.setColName(e.getFieldName());
               col.setModelCode(modelCode);
               col.setColType(e.getFieldType());
               // 得查看返回的长度的值是什么类型
               col.setColLength(e.getFieldLength()==null?0:Integer.parseInt(e.getFieldLength()));
               //字段的精度 这个字段没有，看是否再长度里面 去截取的
               //col.setPrecisions(e.)
               //默认值 数据也没有
               //col.setDefaultValue("");
               col.setColSort(e.getOrd());
               col.setTenantCode(tenantCode);
               col.setIsPaimary(e.getSFYZJ());
               col.setOnlineDate(onlineTime);
               col.setOfflineDate(offlineTime);
               //默认上线设置为有效
               col.setIsValid("1");
               col.setComments(e.getComments());
               col.setBusinessRemark(e.getYWKJ());
               col.setTecRemark(e.getJSKJ());
               col.setSecurityLevel(e.getBMDJ());
               //col.setSecurityType();
               //col.setIsNullable();
               //col.setValueType();
               col.setValueUnit(e.getDW());
               col.setIsDate(e.getSFZQZD());
               col.setIsMasterKey(SwitchFactory.isPrimarykey(e.getSFYZJ()));
               //col.setDateFormat();
               //col.setIsArea();
               return col;
           }).collect(Collectors.toList());
       }

       if(metaColInfos!=null && metaColInfos.size()>0) {
           metaColInfoMapper.insertBatch(metaColInfos);
       }
        //将表中的字段 发布标签资产 meta_asset_info ,先删除 这个model 下面的表,然后再 插入 字段下面的资产信息
        this.filedAssetHandle(detailField,modelTable);

        String tableCode = GetUtil.getAttrValue(attrList, ApiValueEnum.DATA_CODE);
       // 记录消息操作的日志
        LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, logId)
                .set(MetaAssetMqLog::getDataId,tableCode)
                .set(MetaAssetMqLog::getAssetName,tableName);
        metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
        log.info("表资产上线完成");
    }


    /**
     * 添加表里面的字段资产发布到资产里面
     * @param detailField
     * @param modelTable
     */
    private void filedAssetHandle(DetailExternalVO detailField, MetaModelTable modelTable) {
        //先删除 原有的 meta_asset_info 里面的数据, 然后,再发布表中的字段为标签资产


        // 修改表的字段 表上线,如何处理




    }

    /**
     * 入库文件名处理  分日,月,  以及设定文件的位置
     * @param mapper
     * @param msg
     * @param fileNames
     */
//    public void indbFileNameHandle(AssetRegisterTableMapper mapper, MdDataPackageMsg msg, String fileNames) {
//        List<InterfaceConfig> interfaceConfigs = new ArrayList<>();
//        String flag = "";
//        for (String fileName : fileNames.split(",")) {
//            fileName = fileName.trim().replace("\n", "");
//            if (fileName.length() >= 22) {
//                InterfaceConfig config = new InterfaceConfig();
//                if ("D".equals(fileName.substring(1, 2))) {
//                    config.setInterfaceFn(fileName.replace(fileName.substring(12, 18), "YYMMDD"));
//                } else if ("M".equals(fileName.substring(1, 2))) {
//                    config.setInterfaceFn(fileName.replace(fileName.substring(12, 18), "YYYYMM"));
//                }
//                if (fileName.contains(".DAT")) {
//                    flag = "DAT";
//                    config.setInterfaceId(fileName.substring(0, 2) + fileName.substring(26, 29) + "_" + fileName.substring(5, 11));
//                    config.setInterfaceCn(config.getInterfaceFn().replace("DAT", "CHP"));
//                    config.setLocalPath("/data/disk01/hh_ser_prod_051_serPlat/data/");
//                    config.setRemotePath("/data/disk01/hh_ser_prod_051_serPlat/data/");
//                } else if (fileName.contains(".gz")) {
//                    flag = "gz";
//                    config.setInterfaceId(fileName.substring(0, 2) + fileName.substring(23, 26) + "_" + fileName.substring(5, 11));
//                    config.setInterfaceCn(config.getInterfaceFn().replace("gz", "check"));
//                    config.setLocalPath("/data/disk01/hh_ser_prod_000_serPlat/000/data/");
//                    config.setRemotePath("/data/disk01/hh_ser_prod_000_serPlat/000/data/");
//                }
//                config.setOid(msg.getOid());
//                config.setValidId(1);
//                config.setTableName(msg.getDataCode());
//                config.setInterfaceDesc(msg.getDataName());
//                config.setIfsAcquisitionCycle(msg.getDataInterval());
//                config.setAcquisitionBegin(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
//                config.setAcquisitionTemergency(DateUtil.formatDate(msg.getValiTime()));
//                config.setAcquisitionDeadline(DateUtil.formatDate(msg.getEndTime()));
//                config.setCriterionSeq(fileName);
//                config.setCriterionDesc("所属域+文件名+账期+文件后缀");
//                config.setDatabaseName("dc_" + msg.getProvId());
//                config.setDateFormat("xxx");
//                config.setRemoteAddr("10.177.75.6");
//                config.setRemotePort("22");
//                config.setRemoteProt("sftp");
//                config.setFieldSeparator("hex01");
//                config.setRowSeparator("hex0a");
//                interfaceConfigs.add(config);
//            }
//        }
//        if (interfaceConfigs.size() > 0) {
//            if (flag.contains("DAT")) {
//                mapper.deleteInterfaceConfig(msg.getOid(), "interface_config_051");
//                mapper.insertInterfaceConfig(interfaceConfigs, "interface_config_051");
//            } else if (flag.contains("gz")) {
//                mapper.deleteInterfaceConfig(msg.getOid(), "interface_config_000");
//                mapper.insertInterfaceConfig(interfaceConfigs, "interface_config_000");
//            }
//        }
//    }


    /**
     * 添加表字段的操作......
     * @param mapper
     * @param detailData
     * @param detailField
     * @param msg
     */
//    public void addFieldHandle(AssetRegisterTableMapper mapper, AttributeDetailDataVO detailData, List<DetailExternalDataBodyVO> detailField, MdDataPackageMsg msg) {
//        List<MdAssetsInfo> infos = new ArrayList<>();
//        for (DetailExternalDataBodyVO body : detailField) {
//            MdAssetsInfo asset = new MdAssetsInfo();
//            asset.setDataId(msg.getOid());
//            asset.setIsNew("1");
//            asset.setOrderNo(body.getOrd());
//            /*asset.setKpiUnitType(body.getDW());
//            asset.setKpiUnit(body.getDW());*/
//            asset.setAssetId(body.getFieldCode());
//            /*asset.setAssetTypes();--*/
//            asset.setAssetCode(body.getFieldCode());
//            asset.setAssetName(body.getFieldName());
//            asset.setDomainId(msg.getDomainId());
//            asset.setRegiMan(msg.getBuConcactMan());
//            asset.setRegiDate(new Date());
//            asset.setValiTime(msg.getValiTime());
//            asset.setEndTime(DateUtil.parse(detailData.getOfflineTime()));
//            asset.setDeleteFlag(0D);
//            /*asset.setGrpId();*/
//            asset.setRemark(body.getYWKJ());
//            String fieldType = body.getFieldType();
//            asset.setFieldType(SwitchFactory.fieldType(fieldType));
//            asset.setFieldLength(body.getFieldLength());
//            asset.setSecurityLevel(Double.valueOf(SwitchFactory.securityLevel(body.getBMDJ())));
//            /*asset.setDescondid();*/
//            /*asset.setEnccondid();*/
//            asset.setRegiType("0");
//            /*asset.setRelaId();*/
//            asset.setDataInterval(msg.getDataInterval());
//            asset.setPersonInCharge(detailData.getCrtUser());
//            /*asset.setContactInfo();*/
//            asset.setTechnologyDesc(body.getJSKJ());
//            asset.setIsPrimarykey(SwitchFactory.isPrimarykey(body.getSFYZJ()));
//            infos.add(asset);
//        }
//        mapper.insertTmpAssetInfos(infos);
//        mapper.insertStoreRule(msg.getOid());
//        // 插入表结构信息表
//        mapper.insertStructFieldCurrent(msg.getOid());
////        mapper.insertAssetTableCol(msg.getOid());
//    }





}
