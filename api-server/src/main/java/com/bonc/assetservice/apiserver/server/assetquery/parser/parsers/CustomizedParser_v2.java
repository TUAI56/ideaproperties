package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;

import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModel_v2;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.QueryField_v2;
import com.bonc.assetservice.metadata.constant.MetaDataConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/11/11
 * @Description:
 */

@Slf4j
@Component
public class CustomizedParser_v2 {
    public void parse(Boolean aggregation, DataModel_v2 dataModel) throws ParseException {

        //获取最新账期的域主表信息
        DataModel_v2.QueryTable domainMasterTable = getDomainMasterTable(dataModel.getQueryTables());

        //如果不是用户域，或者家庭域，则不添加device_number
        if (!MetaDataConst.USER_DOMAIN_MASTER_TABLE_CODE.equalsIgnoreCase(domainMasterTable.getTableCode())
            && !MetaDataConst.FAMILY_DOMAIN_MASTER_TABLE_CODE.equalsIgnoreCase(domainMasterTable.getTableCode())) {
            log.debug("不是全客请求，无需添加device_number");
            return;
        }
        //当aggregation: true时，select中不添加device_number
        if(aggregation){
            log.debug("aggregation: true，无需添加device_number");
            return;
        }

        //queryFields中增加DEVICE_NUMBER，从域主表中获取值
        QueryField_v2 deviceNumber = new QueryField_v2();
        QueryField_v2.Asset asset = new QueryField_v2.Asset();
        asset.setAssetId(0L);
        asset.setAssetCode(MetaDataConst.USER_FAMILY_DOMAIN_DEVICE_NUMBER);
        deviceNumber.setTableCode(domainMasterTable.getTableCode());
        deviceNumber.setDisplayName(MetaDataConst.USER_FAMILY_DOMAIN_DEVICE_NUMBER);
        asset.setAcct(domainMasterTable.getAcct());
        deviceNumber.setAsset(asset);

        //device_number放到查询的第一列
        List<QueryField_v2> finalQueryList = new ArrayList<>();
        finalQueryList.add(deviceNumber);
        finalQueryList.addAll(dataModel.getQueryFields());

        dataModel.setQueryFields(finalQueryList);
    }

    /**
     * 从当前所有查询的表中，获取最新的域主表信息
     * @param queryTables
     * @return
     */
    private DataModel_v2.QueryTable getDomainMasterTable(List<DataModel_v2.QueryTable> queryTables) {
        DataModel_v2.QueryTable ret = null;
        for (DataModel_v2.QueryTable one : queryTables) {

            //如果不是域主表，则继续
            if (!one.isMain()) {
                continue;
            }

            //是域主表
            if (ret == null) {
                ret = one;
            } else {
                //如果one的账期比ret中的更新，则设置最新的域主表
                if (Integer.parseInt(one.getAcct()) > Integer.parseInt(ret.getAcct())) {
                    ret = one;
                }
            }

        }

        return ret;
    }
}
