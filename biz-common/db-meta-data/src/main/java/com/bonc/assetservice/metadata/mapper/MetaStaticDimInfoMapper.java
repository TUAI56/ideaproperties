package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.entity.MetaStaticDimInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Description: meta_static_dim_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaStaticDimInfoMapper extends BaseMapper<MetaStaticDimInfo> {

    /**
     * 码表放入具体数值
     * @param valueList
     * @return
     */
    int insertDimValue(List<MetaStaticDimInfo> valueList);
}
