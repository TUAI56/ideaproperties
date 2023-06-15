package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.appmodel.EntityTableAcct;
import com.bonc.assetservice.metadata.entity.MetaEntityTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: meta_entity_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaEntityTableMapper extends BaseMapper<MetaEntityTable> {

    /**
     * 根据模板表ID列表，获取模板表相关信息
     * @param tableCodes 模板表id列表
     * @return ModelAndKeyInfo
     */
    List<EntityTableAcct> getEntityTableAcct(@Param("tableCodes") List<String> tableCodes);
}
