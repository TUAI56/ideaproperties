package com.bonc.assetservice.apiserver.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonc.assetservice.apiserver.data.entity.UserDataset;
import com.bonc.assetservice.apiserver.data.model.DataSetCodeAndColum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: user_dataset
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Mapper
public interface UserDatasetMapper extends BaseMapper<UserDataset> {
    List<DataSetCodeAndColum> getDataSetCodeAndColum(@Param("dataSetIds") List<String> dataSetIds);

    DataSetCodeAndColum getDataSetByDatasetId(@Param("datasetId")String datasetId);


    List<DataSetCodeAndColum> getDataSetByDatasetTable(@Param("dataSetTableNames")List<String> names);
}
