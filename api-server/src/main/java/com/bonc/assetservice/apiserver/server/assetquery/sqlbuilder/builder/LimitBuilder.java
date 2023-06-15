package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder;

import com.bonc.assetservice.apiserver.server.common.PageInfo;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.springframework.stereotype.Component;

@Component
public class LimitBuilder extends Builder{
    public void build(PageInfo pageInfo, String dialect, PlainSelect plainSelect) {

        //step6: 设置分页
        plainSelect.setLimit(buildLimit(pageInfo, dialect));

    }
    /**
     * 构建分页
     *
     * @param pageInfo 分页信息
     * @param dialect  数据库类型 根据数据库类型生成不同的分页SQL TODO:目前实现的分页方式：limit 偏移量,返回行数，其余方式后续用到补充
     * @return net.sf.jsqlparser.statement.select.Limit
     * @Author 李维帅
     * @Date 2022/6/17 15:04
     **/
    private Limit buildLimit(PageInfo pageInfo, String dialect) {
        if (pageInfo != null && pageInfo.getLimit() != null && pageInfo.getOffset() != null) {
            return new Limit().withOffset(new LongValue((long) (pageInfo.getOffset() - 1) * pageInfo.getLimit()))
                    .withRowCount(new LongValue(pageInfo.getLimit()));
        }
        return null;
    }

}
