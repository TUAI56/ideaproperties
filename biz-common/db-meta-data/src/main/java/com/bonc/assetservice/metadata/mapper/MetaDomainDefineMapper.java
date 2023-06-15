package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.entity.MetaDomainDefine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @Description: meta_domain_define
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaDomainDefineMapper extends BaseMapper<MetaDomainDefine> {
    /**
     * 根据域名称查询域的编号
     * @param domainName
     * @return
     */
    String selectDomainCodeByName(@Param("domainName") String domainName);
}
