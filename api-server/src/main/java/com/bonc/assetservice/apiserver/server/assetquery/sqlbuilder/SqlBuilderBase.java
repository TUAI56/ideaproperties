package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.bonc.assetservice.apiserver.server.assetquery.constant.Enums;
import com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.builder.Builder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;


@Slf4j
public abstract class SqlBuilderBase<T> {







    //step0:初始化需要在链上的传递的信息
    public abstract void init(T t);

    //step:1 设置要查询的字段
    public abstract void setSelectItems(T t, PlainSelect returnSelect);

    //step:2 设置域主表
    public abstract void setFromItem(T t, PlainSelect returnSelect);

    //step3:设置join
    public abstract void setJoins(T t, PlainSelect returnSelect);


    //step3:设置where
    public abstract void setWhere(T t, PlainSelect returnSelect);


    //step4:设置排序
    public abstract void setOrderByElements(T t, PlainSelect returnSelect);

    //step5:设置设置分组
    public abstract void setGroupByElement(T t, PlainSelect returnSelect);

    //step5:设置分页
    public abstract void setLimit(T t, PlainSelect returnSelect);

    //step6：情空ThreadLocal
    public abstract void clean();


    public PlainSelect  buildPlainSelect(T t){
        //初始化返回参数
        PlainSelect plainSelect = new PlainSelect();
        try {
            Assert.notNull(t, "DataModel为空");
            long timeStart = System.currentTimeMillis();
            log.info("构建查询SQL入参：{}", JSONObject.toJSONString(t));




            init(t);

            //1.构造 from 域主表信息  from mainTable
            setFromItem(t, plainSelect);
            //2、构造查询条件 select a,b,c
            setSelectItems(t, plainSelect);

            //3.构造inner join condition 和 left join queryfile的table
            setJoins(t, plainSelect);

            if (Enums.SqlBuilderModel.WIDE_TABLE_MODE.getValue().equalsIgnoreCase(Builder.MODEL)) {
                setWhere(t,plainSelect);
            }

            //4.构造order by
            setOrderByElements(t, plainSelect);
            //5、构造Group by
            setGroupByElement(t, plainSelect);
            //6.构造分页查询条件
            setLimit(t, plainSelect);


            log.info("构建查询SQL完成：{}", plainSelect);
            log.info("构建查询SQL耗时：{}s", (System.currentTimeMillis() - timeStart) / 1000d);



            return plainSelect;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }finally {
            clean();
        }
    }



    public  Column buildColumn(String code, String tableAlias) {
        Table table = StrUtil.isNotBlank(tableAlias) ? new Table(tableAlias) : null;
        return new Column(table, code);
    }



}
