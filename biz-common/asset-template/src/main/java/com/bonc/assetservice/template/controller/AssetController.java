package com.bonc.assetservice.template.controller;

import com.bonc.assetservice.template.service.AssetService;
import com.bonc.framework.common.pojo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: huyang
 * @Email huyang@bonc.com.cn
 * @Date: 2022/6/14 9:09
 * @Version: V1.0
 */
@RestController
@RequestMapping("/assettemplate")
public class AssetController {


    @Autowired
    private AssetService assetService;

    /**
     *@description
     *@author huyang
     *@date 2022/6/14
     */
    @ApiOperation( "服务标签的信息")
    @PostMapping("/assetinfo")
    public Object assetInfo() {
        List<String> strings = Arrays.asList("data", "rest", "eeeeedd");
        return CommonResult.success(strings);
    }

    /**
     *@description
     *@author huyang
     *@date 2022/6/14
     */
    @ApiOperation( "服务标签的信息")
    @PostMapping("/assetinfodata")
    public Object assetInfoWithData() {
       List<Map<String,Object>> result= assetService.queryAssetInfoWithData();
        return CommonResult.success(result);
    }





}
