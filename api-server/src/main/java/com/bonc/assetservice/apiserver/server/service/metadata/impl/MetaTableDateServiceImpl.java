package com.bonc.assetservice.apiserver.server.service.metadata.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IMetaTableDateService;
import com.bonc.assetservice.metadata.entity.MetaTableDate;
import com.bonc.assetservice.metadata.mapper.MetaTableDateMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: meta_table_date
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class MetaTableDateServiceImpl extends ServiceImpl<MetaTableDateMapper, MetaTableDate> implements IMetaTableDateService {

}
