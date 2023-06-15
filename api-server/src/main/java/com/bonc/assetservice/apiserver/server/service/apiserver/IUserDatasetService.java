package com.bonc.assetservice.apiserver.server.service.apiserver;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bonc.assetservice.apiserver.data.entity.UserDataset;
import com.bonc.assetservice.apiserver.data.model.DataSetCodeAndColum;


import java.util.HashMap;
import java.util.List;

/**
 * @Description: user_dataset
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@DS("apiserver")
public interface IUserDatasetService extends IService<UserDataset> {


    /**
     * 根据自定义数据集列表，获取这些数据集的的join字段  adb中的表名
     * @param dataSetIds 需要查询的标签的集合
     * @return <assetId, AssetCodeAndAcct>
     */
    HashMap<String, DataSetCodeAndColum> getDataSetCodeAndColum(List<String> dataSetIds);


    /**
     * 获取自定义数据集信息
     * @param dataSetIds 需要查询的标签的集合
     * @return <assetId, AssetCodeAndAcct>
     */
     DataSetCodeAndColum getDataSetByDatasetId(String dataSetIds);

    /**
     * 根据自定义表明获取自定义表信息
     * @param dataSetTableNames 需要查询的标签的集合
     * @return <assetId, AssetCodeAndAcct>
     */
    List<DataSetCodeAndColum> getDataSetByDatasetTable(List<String> dataSetTableNames);

}
