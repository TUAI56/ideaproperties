package com.bonc.assetservice.apiserver.server.service.metadata;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bonc.assetservice.metadata.appmodel.EntityTableAcct;
import com.bonc.assetservice.metadata.entity.MetaEntityTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: meta_entity_table
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
public interface IMetaEntityTableService extends IService<MetaEntityTable> {
    /**
     * 根据模板表ID列表，获取模板表相关信息
     * @param tableCodes 模板表id列表
     * @return ModelAndKeyInfo
     */
    Map<String, EntityTableAcct> getEntityTableAcct(@Param("tableCodes") List<String> tableCodes);
}
