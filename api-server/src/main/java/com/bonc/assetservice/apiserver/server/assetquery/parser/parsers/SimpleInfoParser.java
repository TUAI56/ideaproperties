package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;

import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModelBase;
import com.bonc.assetservice.apiserver.server.assetquery.vo.AbstractAssetQueryBaseReqVO;
import com.bonc.assetservice.metadata.constant.MetaDataConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: Ethan.Xing
 * @Date: 2023/3/15
 * @Description: provId、areaId、aggregation、groupCount的解析器
 */

@Slf4j
@Component
public class SimpleInfoParser {
    public void parse(AbstractAssetQueryBaseReqVO req, DataModelBase dataModelBase) throws ParseException {

        //解析是否查询总数
        dataModelBase.setGroupCount(req.getGroupCount());

        //解析是否聚合操作
        dataModelBase.setAggregation(req.getAggregation());

        //解析provId
        dataModelBase.setProvId(req.getProvId());

        //解析proviceCode
        dataModelBase.setProvCode(MetaDataConst.USER_FAMILY_DOMAIN_PROV_KEY);

        //解析areaId
        dataModelBase.setAreaId(req.getAreaId());

        //解析areaCode
        dataModelBase.setAreaCode(MetaDataConst.USER_FAMILY_DOMAIN_AREA_KEY);
    }
}
