package com.bonc.assetservice.assetsync.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonc.assetservice.assetsync.handler.IdimInfoHandler;
import com.bonc.assetservice.assetsync.model.DimInfoMsgVO;
import com.bonc.assetservice.assetsync.model.FieldMsg;
import com.bonc.assetservice.metadata.entity.DimField;
import com.bonc.assetservice.metadata.entity.MetaDimInfo;
import com.bonc.assetservice.metadata.entity.MetaStaticDimInfo;
import com.bonc.assetservice.metadata.mapper.MetaDimInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaStaticDimInfoMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service("DimInfoService")
public class DimInfoServiceHandler implements IdimInfoHandler {

    @Autowired
    private MetaDimInfoMapper metaDimInfoMapper;

    @Autowired
    private DimValueService dimValueService;

    @Autowired
    private MetaStaticDimInfoMapper metaStaticDimInfoMapper;

    @Override
    public void assetHandle(DimInfoMsgVO dimInfoMsgVO) {
        String mappingString = dimInfoMsgVO.getMapping();
        mappingString.replaceAll("\\\\","");
        List<FieldMsg> mappingList = JSONObject.parseArray(mappingString, FieldMsg.class);
        log.info("解析后的消息请求为 ------> {}",JSONObject.toJSON(mappingList));
        List<MetaDimInfo> metaDimInfoList = new ArrayList<>();
        for (FieldMsg mapping : mappingList) {
            //查询是否有相应关系对
            QueryWrapper<MetaDimInfo> dimInfoWrapper = new QueryWrapper<MetaDimInfo>().eq("code_field", mapping.getFieldCode())
                    .eq("name_field", mapping.getFieldName())
                    .eq("dim_table_code", dimInfoMsgVO.getCodeCode());
            MetaDimInfo metaDimInfo = metaDimInfoMapper.selectOne(dimInfoWrapper);
            if (null == metaDimInfo) {
                metaDimInfo = MetaDimInfo.builder().id(null).codeField(mapping.getFieldCode())
                        .nameField(mapping.getFieldName())
                        .dimTableCode(dimInfoMsgVO.getCodeCode())
                        .dimTableName(dimInfoMsgVO.getCodeName())
                        .tableOid(dimInfoMsgVO.getCodeId())
                        .build();
                //没有则放入,并存入待获取码值集合
                metaDimInfoMapper.insert(metaDimInfo);
                metaDimInfo = metaDimInfoMapper.selectOne(dimInfoWrapper);
                metaDimInfoList.add(metaDimInfo);
            }
        }
        if(null!=metaDimInfoList && !metaDimInfoList.isEmpty()){
            dimValueInsert(metaDimInfoList);
        }

    }

    @Override
    @Async
    public void syncAssetHandle(List<MetaDimInfo> metaDimInfoList) {
        try {
            dimValueInsert(metaDimInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("标签资产异步完成");
    }

    private void dimValueInsert(List<MetaDimInfo> metaDimInfoList) {
        int total = 0;
        String countSql = "SELECT COUNT(*) FROM " + metaDimInfoList.get(0).getDimTableCode();
        String count = dimValueService.getDataNum(countSql);
        int size = 1000;
        int times = (int) Math.ceil(Double.parseDouble(count) / size);
        String baseSql = " SELECT code_field AS `code`,name_field AS `name` FROM tableName";
        for (MetaDimInfo metaDimInfo : metaDimInfoList) {
            String sql = baseSql.replace("code_field",metaDimInfo.getCodeField())
                    .replace("name_field",metaDimInfo.getNameField())
                    .replace("tableName",metaDimInfo.getDimTableCode());
            for (int i = 1; i <= times; i++) {
                List<MetaStaticDimInfo> valueList = new ArrayList<>();
                Page<DimField> page = new Page<>(i, size);
                Page<DimField> pageData = dimValueService.getPageData(page,sql);
                List<DimField> dimValueList = pageData.getRecords();
                for (DimField dimField : dimValueList) {
                    MetaStaticDimInfo metaStaticDimInfo = MetaStaticDimInfo.builder().id(null)
                            .dimInfoId(String.valueOf(metaDimInfo.getId()))
                            .dimCode(dimField.getCode())
                            .dimName(dimField.getName())
                            .isValid("1")
                            .build();
                    valueList.add(metaStaticDimInfo);
                }
                int num = metaStaticDimInfoMapper.insertDimValue(valueList);
                total+=num;
            }
        }
        log.info("静态码表数据入库条数为----------> {}", total);
    }

}
