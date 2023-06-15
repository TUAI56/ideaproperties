package com.bonc.assetservice.apiserver.server.assetinfo.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bonc.assetservice.apiserver.server.assetinfo.service.LabelMetaService;
import com.bonc.assetservice.apiserver.server.assetinfo.vo.DimTableReqVO;
import com.bonc.assetservice.apiserver.server.assetinfo.vo.DimTableRespVO;
import com.bonc.assetservice.metadata.appmodel.DimTableModel;
import com.bonc.assetservice.metadata.appmodel.LabelDetailsModel;
import com.bonc.assetservice.metadata.appmodel.LabelTreeModel;
import com.bonc.framework.common.pojo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "标签服务元数据服务 - 查询标签分类以及标签属性 维度表等接口")
@RestController
@RequestMapping("/api/v1/")
public class LabelMetaController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LabelMetaService labelMetaService;


    /**
     * 验证用户请求是否能够提供返回,放到 Aspect 里面,拦截器里面实现拦截
     * @param provId 省份id
     */
    @ApiOperation("查询标签树")
    @GetMapping("/asset/list")
    public CommonResult<Object> assetList(String provId) {
        log.info("查询标签列表:参数{}", provId);
//      判断redis中是否有数据
        String key = "grp#"+ provId;
        String jsonData = "";
        List<LabelTreeModel> data = null;
        if (redisTemplate.hasKey(key)) {
            jsonData = redisTemplate.opsForValue().get(key);
            jsonData.replaceAll("\\\\","");
            data = JSONObject.parseArray(jsonData, LabelTreeModel.class);
            if (null != data && !"".equals(data)&&data.size()>0) {
                return CommonResult.success(data);
            }
        }
        try {
            if ("099".equals(provId)) {
                //如果是要查询全国，则不做省份的过滤
                provId = null;
            }
            data = labelMetaService.queryAssetList(provId);
            jsonData = String.valueOf(JSONObject.toJSON(data));
            log.info("存入redis的数据为 {}", jsonData);
            redisTemplate.opsForValue().set(key, jsonData, 24, TimeUnit.HOURS);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return CommonResult.error(500, ex.getMessage());
        }
        return CommonResult.success(data);
    }


    /**
     * @param provId 省份id
     * @param assetId 资产的主键id
     * @return
     */
    @ApiOperation("查询标签详情")
    @GetMapping("/asset/details")
    public CommonResult<Object> assetDetails(String provId,
                                             String assetId) {
        log.info("查询标签详情:参数{},{}", provId, assetId);
//      判断redis中是否有数据
        String key = "label#" + assetId + "#" + provId;
        String jsonData = "";
        LabelDetailsModel data = null;
        //TODO 存在缓存标签数据与最新账期数据不一致的问题,先屏蔽掉从缓存取数据，如有必要再优化缓存方案
       /* if (redisTemplate.hasKey(key)) {
            jsonData = redisTemplate.opsForValue().get(key);
            jsonData.replaceAll("\\\\","");
            data = JSONObject.parseObject(jsonData, LabelDetailsModel.class);
            if (null != data && !"".equals(data)) {
                return CommonResult.success(data);
            }
        }*/
        data = labelMetaService.queryAssetDetails(provId, assetId);
        //TODO 存在缓存标签数据与最新账期数据不一致的问题,先屏蔽掉缓存数据，如有必要再优化缓存方案
        /*jsonData = String.valueOf(JSONObject.toJSON(data));
        log.info("存入redis的数据为 {}", jsonData);
        redisTemplate.opsForValue().set(key, jsonData, 24, TimeUnit.HOURS);*/
        return CommonResult.success(data);
    }


    /**
     * @param request 请求参数
     * @return
     */
    @ApiOperation("查询码表详情")
    @PostMapping("/dimTable/details")
    public CommonResult<DimTableRespVO> dimTableDetails(@RequestBody DimTableReqVO request) {

        IPage<DimTableModel> page = labelMetaService.queryDimTableDetails(request);

        DimTableRespVO resp = new DimTableRespVO();
        resp.setTotal(new Long(page.getTotal()).intValue());
        resp.setCurrent(new Long(page.getCurrent()).intValue());
        resp.setPages(new Long(page.getPages()).intValue());
        resp.setSize(new Long(page.getSize()).intValue());
        resp.setRecords(page.getRecords());

        return CommonResult.success(resp);
    }


}
