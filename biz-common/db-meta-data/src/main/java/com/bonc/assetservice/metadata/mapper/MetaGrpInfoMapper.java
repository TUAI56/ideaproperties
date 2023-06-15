package com.bonc.assetservice.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.metadata.entity.MetaGrpInfo;
import com.bonc.assetservice.metadata.appmodel.LabelTreeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: meta_grp_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Mapper
public interface MetaGrpInfoMapper extends BaseMapper<MetaGrpInfo> {

    /**
     * 查询标签分组的信息列表
     * @param provId
     * @return
     */
    List<LabelTreeModel> queryAssetList(@Param("provId") String provId);

    /**
     * 标签信息入库
     * @param groupInfoList
     * @return
     */
    int insertGroupInfo(List<Map<String, Object>> groupInfoList);
}
