package com.bonc.assetservice.apiserver.server.assetquery.parser.parsers;

import com.bonc.assetservice.apiserver.server.assetquery.parser.exception.ParseException;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model.DataModelBase;
import com.bonc.assetservice.apiserver.server.common.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: Ethan.Xing
 * @Date: 2023/3/15
 * @Description: DataModel中pageInfo的解析器
 */

@Slf4j
@Component
public class PageInfoParser {

    public void parse(PageInfo pageInfo, DataModelBase dataModelBase) throws ParseException {
        //解析PageInfo
        if (pageInfo != null) {
            dataModelBase.setPageInfo(pageInfo);
        }
    }

}
