package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.entity.MetaDbInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: meta_db_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaDbInfoMapper extends BaseMapper<MetaDbInfo> {
    /**
     * 查询 数据库的编码
     * @return
     */
    String selectDbCode();
}
