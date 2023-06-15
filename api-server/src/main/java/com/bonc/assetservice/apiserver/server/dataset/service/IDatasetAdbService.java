package com.bonc.assetservice.apiserver.server.dataset.service;

import com.bonc.assetservice.apiserver.data.entity.UserDatasetColInfo;
import com.bonc.assetservice.apiserver.server.dataset.vo.DatasetCreateReqVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IDatasetAdbService {

    boolean exeSqlInAdb(String sql);
}
