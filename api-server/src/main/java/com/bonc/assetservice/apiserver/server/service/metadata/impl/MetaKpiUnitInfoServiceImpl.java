package com.bonc.assetservice.apiserver.server.service.metadata.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaKpiUnitInfoService;
import com.bonc.assetservice.metadata.entity.MetaKpiUnitInfo;
import com.bonc.assetservice.metadata.mapper.MetaKpiUnitInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_kpi_unit_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaKpiUnitInfoServiceImpl extends ServiceImpl<MetaKpiUnitInfoMapper, MetaKpiUnitInfo> implements IMetaKpiUnitInfoService {

}
