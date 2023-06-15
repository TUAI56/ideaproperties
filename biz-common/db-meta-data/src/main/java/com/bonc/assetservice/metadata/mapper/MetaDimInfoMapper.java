package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonc.assetservice.metadata.appmodel.DimTableModel;
import com.bonc.assetservice.metadata.entity.DimField;
import com.bonc.assetservice.metadata.entity.MetaDimInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Description: meta_dim_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaDimInfoMapper extends BaseMapper<MetaDimInfo> {

    /**
     * 分页查询码表的数据值
     * @param page
     * @return
     */
    IPage<DimTableModel> queryDimTableDetailsWithPage(IPage<DimTableModel> page,
                                                      @Param("dimId") String dimId,
                                                      @Param("name") String name, @Param("code") String code);

    /**
     * 根据码表字段信息获取id信息
     * @param codeNameMap
     * @return
     */
    MetaDimInfo selectFieldInfo(Map<String, String> codeNameMap);

    /**
     * 获取码表数据量
     * @param countSql
     * @return
     */
    String getDataNum(String countSql);

    /**
     * 分页获取码值数据
     * @param page
     * @param sql
     * @return
     */
    Page<DimField> getPageData(@Param("page") Page<DimField> page, @Param("sql") String sql);
}
