package com.bonc.assetservice.template.service.impl;

import com.bonc.assetservice.template.mapper.AssetMapper;
import com.bonc.assetservice.template.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class AssetServiceImpl implements AssetService {

   @Autowired
   private AssetMapper assetMapper;


   @Override
   public List<Map<String, Object>> queryAssetInfoWithData() {
      return assetMapper.queryInfo();
   }

}
