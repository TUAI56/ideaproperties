package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model;

import com.bonc.assetservice.apiserver.server.common.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据模型
 * @ClassName DataModel_v1
 * @Author 李维帅
 * @Date 2022/6/10 14:40
 * @Version 1.0
 **/
@Data
public class DataModelBase implements Serializable, Cloneable {

    private static final long serialVersionUID = 524833132522301807L;
    /**
     * 数据库类型
     */
    private String dialect;


    /**
     * 查询数据表
     */
    private List<QueryTable> queryTables;

    /**
     * 分页信息
     */
    private PageInfo pageInfo;
    /**
     * 是否查询总数
     */
    private boolean groupCount = false;
    /**
     * 查询是否进行聚合操作
     */
    private boolean aggregation = false;

    /**
     * provId
     */
    private String provId;

    /**
     * provId
     */
    private String areaId;

    /**
     * provCode
     */
    private String provCode;

    /**
     * areaCode
     */
    private String areaCode;


    @Data
    public static class QueryTable {
        /**
         * 是否域主表
         */
        private boolean main = false;
        /**
         * 表名
         */
        private String tableCode;
        /**
         * 关联字段编码
         */
        private String relationFieldCode;
        /**
         * 账期字段编码
         */
        private String acctFieldCode;
        /**
         *
         */
        private String acct;
    }




}
