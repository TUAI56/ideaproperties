package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.entity.MetaModelTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @Description: meta_model_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaModelTableMapper extends BaseMapper<MetaModelTable> {

    /**
     * 通过模型表编号 查询模型表信息
     * @param modelCode
     * @return
     */
    MetaModelTable selectByMoldCode(@Param("tableOid") String tableOid);
}
