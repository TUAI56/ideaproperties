package com.bonc.assetservice.assetsync.handler.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonc.assetservice.metadata.entity.DimField;
import com.bonc.assetservice.metadata.mapper.MetaDimInfoMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@DS("dimdata")
@Transactional(rollbackFor = Exception.class)
public class DimValueService {
    @Autowired
    private MetaDimInfoMapper metaDimInfoMapper;


    public String getDataNum(String countSql){

        return metaDimInfoMapper.getDataNum(countSql);

    }

    public Page<DimField> getPageData(@Param("page") Page<DimField> page, @Param("sql") String sql){

        return metaDimInfoMapper.getPageData(page,sql);

    }
}
