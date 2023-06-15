package com.bonc.assetservice.apiserver.server.dataset.service;

import com.bonc.assetservice.apiserver.data.entity.UserDatasetColInfo;
import com.bonc.assetservice.apiserver.server.dataset.vo.DatasetCreateReqVo;
import com.bonc.framework.common.pojo.CommonResult;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface IDatasetService {

    boolean saveDateset(DatasetCreateReqVo datasetCreateReqVo, List<UserDatasetColInfo> userDatasetColInfoList, String tableName);
}
