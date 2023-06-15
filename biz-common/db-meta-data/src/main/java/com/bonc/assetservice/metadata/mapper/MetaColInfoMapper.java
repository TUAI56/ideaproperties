package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.entity.MetaColInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: meta_col_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaColInfoMapper extends BaseMapper<MetaColInfo> {
    /**
     * 批量插入模板字段信息表......
     * @param metaColInfos
     * @return
     */
    int  insertBatch(List<MetaColInfo> metaColInfos);

    /**
     *  修改 更改表后的字段为失效状态
     * @param cols
     * @param modelCode
     * @return
     */
    int updateMetacolsValid(@Param("cols") List<String> cols, @Param("modelCode") String modelCode, @Param("valid") String Valid);

    /**
     * 设置模板表的字段为失效
     * @param modelCode
     * @return
     */
    int  updateModelColsValid(@Param("modelCode")String modelCode);

    /**
     * 根据 模型和字段编号查询表字段的有关信息
     * @param modelCode
     * @param colCode
     * @return
     */
    MetaColInfo selectMetaColByModelAndCol(@Param("tableOid") String tableOid, @Param("oid") String oid);
}
