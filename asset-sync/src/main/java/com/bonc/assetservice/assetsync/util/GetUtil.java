package com.bonc.assetservice.assetsync.util;


import com.bonc.assetservice.assetsync.constant.ApiValueEnum;
import com.bonc.assetservice.assetsync.model.AttrVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GetUtil {

    /**
     *
     * @param attrList  属性集合
     * @param id  查询的编号
     * @return
     */
    public static String getAttrValue(List<AttrVO> attrList, ApiValueEnum id) {
        for (AttrVO attr : attrList) {
            if (id.getValue().equals(attr.getCode())) {
                return attr.getValue();
            }
        }
        return null;
    }

    public static String getAttrAnalyticValue(List<AttrVO> attrList, ApiValueEnum id) {
        for (AttrVO attr : attrList) {
            if (id.getValue().equals(attr.getCode())) {
                return attr.getAnalyticValue();
            }
        }
        return null;
    }
}