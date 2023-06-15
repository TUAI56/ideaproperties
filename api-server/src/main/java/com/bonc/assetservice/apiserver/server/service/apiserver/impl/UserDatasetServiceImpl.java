package com.bonc.assetservice.apiserver.server.service.apiserver.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.bonc.assetservice.apiserver.data.entity.UserDataset;
import com.bonc.assetservice.apiserver.data.mapper.UserDatasetMapper;
import com.bonc.assetservice.apiserver.data.model.DataSetCodeAndColum;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: user_dataset
 * @Author: jeecg-boot
 * @Date:   2022-06-30
 * @Version: V1.0
 */
@Service
@DS("apiserver")
public class UserDatasetServiceImpl extends ServiceImpl<UserDatasetMapper, UserDataset> implements IUserDatasetService {



    @Autowired
    UserDatasetMapper userDatasetMapper;


    @Override
    public HashMap<String, DataSetCodeAndColum> getDataSetCodeAndColum(List<String> dataSetIds) {
        if (dataSetIds == null || dataSetIds.size() == 0) {
            return null;
        }

        List<DataSetCodeAndColum> infos = userDatasetMapper.getDataSetCodeAndColum(dataSetIds);

        HashMap<String, DataSetCodeAndColum> ret = new HashMap<>();
        for (DataSetCodeAndColum one : infos) {
            ret.put(one.getDataSetId(), one);
        }
        return ret;
    }

    @Override
    public DataSetCodeAndColum getDataSetByDatasetId(String dataSetIds) {
        return userDatasetMapper.getDataSetByDatasetId(dataSetIds);
    }



    @Override
    public List<DataSetCodeAndColum> getDataSetByDatasetTable(List<String> dataSetTableNames) {
        if (null == dataSetTableNames || dataSetTableNames.isEmpty()) return null;
        return userDatasetMapper.getDataSetByDatasetTable(dataSetTableNames);
    }
}
