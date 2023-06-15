package com.bonc.assetservice.apiserver.server.dataset.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.bonc.assetservice.apiserver.data.entity.UserDataset;
import com.bonc.assetservice.apiserver.data.entity.UserDatasetColInfo;
import com.bonc.assetservice.apiserver.server.dataset.service.IDatasetService;
import com.bonc.assetservice.apiserver.server.dataset.vo.Column;
import com.bonc.assetservice.apiserver.server.dataset.vo.DatasetCreateReqVo;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetColInfoService;
import com.bonc.assetservice.apiserver.server.service.apiserver.IUserDatasetService;
import com.bonc.framework.common.exception.ErrorCode;
import com.bonc.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bonc.assetservice.metadata.constant.MetaDataConst.DB_DEL_FLAG_N;
@Service
@Slf4j
@DS("apiserver")
public class DatasetServiceImpl implements IDatasetService {
    @Resource
    private IUserDatasetService userDatasetService;
    @Resource
    private IUserDatasetColInfoService userDatasetColInfoService;

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;
    @Override
    @Transactional
    public boolean saveDateset(DatasetCreateReqVo datasetCreateReqVo, List<UserDatasetColInfo> userDatasetColInfoList, String tableName) {

        //1.保存userDataset表
        UserDataset userDataset = new UserDataset();
        BeanUtils.copyProperties(datasetCreateReqVo, userDataset);
        userDataset.setTqAppUuid(datasetCreateReqVo.getTqAppUUId());
        userDataset.setCrtTime(new Date());
        userDataset.setEntityTableCode("dataset_" + datasetCreateReqVo.getDatasetId());
        userDataset.setDelFlag(DB_DEL_FLAG_N);
        userDataset.setEntityTableCode(tableName);

        userDatasetService.save(userDataset);

        //2.保存user_dataset_col_info表

        userDatasetColInfoService.saveBatch(userDatasetColInfoList);

        return true;
    }

}
