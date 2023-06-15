package com.bonc.assetservice.assetsync.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.bonc.assetservice.assetsync.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class UrlRequestFactory {

    @Value("${yudao.rocketmq.urlDetail}")
    private String urlDetail;   // 查询资产详情的url请求,

    @Value("${yudao.rocketmq.detailExternal}")
    private String urlDetailExternal; //获取表字段信息

    @Value("${yudao.rocketmq.dimExtern}")
    private String urlExtern;   // 根据编码查询码表表名

    /**
     * 获取资产详情
     * @param assetId
     * @return
     */
    public AttributeDetailDataVO getAttributeDetail(String assetId) {
        Map<String, Object> paramMap1 = new HashMap<>(1);
        paramMap1.put("assetsId", assetId);
        log.info("开始请求{}，{}", urlDetail, paramMap1);
        String attributeDetailString =
                HttpUtil.get(urlDetail, paramMap1);
//                TableTestData.attributeDetailString;
//                TableTestData2.attributeDetailString;
//                FileTestData.attributeDetailString;
//                FileTestData2.attributeDetailString;
        log.info("请求成功{}", attributeDetailString);
        AttributeDetailVO attributeDetail = JSONUtil.toBean(attributeDetailString, AttributeDetailVO.class);
        //请求返回数据是否异常
        if (attributeDetail == null) {
            throw new NullPointerException(urlDetail + "返回为空");
        }
        if (attributeDetail.getCode() == null) {
            throw new NullPointerException("Code为空" + attributeDetail.toString());
        }
        if (!attributeDetail.getCode().equals(0)) {
            throw new RuntimeException(urlDetail + "请求异常" + attributeDetail.toString());
        }
        attributeDetail.getData().setReturnJson(attributeDetailString);
        return attributeDetail.getData();
    }

    /**
     * 获取表字段信息
     * @param assetId
     * @return
     */
    public DetailExternalVO getDetailExternalVO(String assetId) {
        //String urlDetailExternal = PropsUtil.get("detail_external");
        Map<String, Object> paramMap3 = new HashMap<>();
        paramMap3.put("assetsId", assetId);
        log.info("开始请求{}，{}", urlDetailExternal, paramMap3);
        String detailExternal =
                HttpUtil.get(urlDetailExternal, paramMap3);
//                TableTestData.getDataItemDetailExternal;
//                TableTestData2.getDataItemDetailExternal;
        DetailExternalVO detailExternalVO = JSONUtil.toBean(detailExternal, DetailExternalVO.class);
        log.info("请求结束{}，{}", detailExternal, paramMap3);
        if (detailExternalVO == null) {
            throw new NullPointerException("对象为空");
        }
        if (detailExternalVO.getData() == null) {
            throw new NullPointerException("code为空");
        }
        if (detailExternalVO.getData().getBody() == null && detailExternalVO.getData().getBody().size() == 0) {
            throw new NullPointerException("body为空");
        }
        detailExternalVO.setReturnJson(detailExternal);
        return detailExternalVO;
    }

    /**
     * 根据编码查询码表表名
     * @param dimIds
     * @return
     */
    public DimExternDataVO getDimExternData(String dimIds) {
        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("dimIds", dimIds);
       // String urlExtern = PropsUtil.get("dim_extern");
        log.info("开始请求{}，{}", urlExtern, paramMap1);
        String attributeDetailString =
                HttpUtil.get(urlExtern, paramMap1);
//                FileTestData.dimExtern;
//                FileTestData2.dimExtern;
        log.info("请求成功{}", attributeDetailString);
        DimExternVO dimExternVO = JSONUtil.toBean(attributeDetailString, DimExternVO.class);
        if (dimExternVO == null) {
            throw new NullPointerException(urlExtern + "返回为空");
        }
        if (dimExternVO.getCode() == null) {
            throw new NullPointerException("Code为空" + dimExternVO.toString());
        }
        if (!dimExternVO.getCode().equals(0)) {
            throw new RuntimeException(urlExtern + "请求异常" + dimExternVO.toString());
        }
        if (dimExternVO.getData() == null || dimExternVO.getData().size() == 0) {
            return null;
        }
        dimExternVO.getData().get(0).setReturnJson(attributeDetailString);
        return dimExternVO.getData().get(0);
    }
}
