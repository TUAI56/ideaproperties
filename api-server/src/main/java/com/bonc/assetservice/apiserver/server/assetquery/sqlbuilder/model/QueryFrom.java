package com.bonc.assetservice.apiserver.server.assetquery.sqlbuilder.model;


import lombok.Data;
import net.sf.jsqlparser.statement.select.SelectBody;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SQL中from模型
 * @ClassName QueryFrom
 * @Author 李维帅
 * @Date 2022/6/27 9:02
 * @Version 1.0
 **/
@Data
public class QueryFrom implements Serializable {
    private static final long serialVersionUID = -7908711801848987910L;
    /**
     * 是否主表
     */
    private boolean main = false;
    /**
     * 别名
     */
    private String alias;
    /**
     * 表名
     */
    private String tableCode;
    /**
     * 关联查询对象列表
     */
   // List<DataModel_v1.RelationModel.Relation> relations;
    /**
     * 关联字段编码
     */
    private String relationFieldCode;
    /**
     * 账期字段编码
     */
    private String acctFieldCode;
    /**
     * 账期
     */
    private String acct;
    /**
     * 关联类型：INNER, LEFT
     */
    private String joinType = JOIN_TYPE_LEFT;
    public final static String JOIN_TYPE_INNER = "INNER";
    public final static String JOIN_TYPE_LEFT = "LEFT";
    /**
     * 字段列表
     */
    private Set<String> fields = new HashSet<>();
    /**
     * 查询对象
     */
    private SelectBody select;

    public QueryFrom() {

    }

    public QueryFrom(DataModelBase.QueryTable queryTable, String alias, String acct) {
        this.main = queryTable.isMain();
        this.tableCode = queryTable.getTableCode();
        this.relationFieldCode = queryTable.getRelationFieldCode();
        this.acctFieldCode = queryTable.getAcctFieldCode();
        this.alias = alias;
        this.acct = acct;
        this.fields.add(queryTable.getRelationFieldCode());
    }

    public QueryFrom( String relationFieldCode, String alias) {

        this.relationFieldCode = relationFieldCode;
        this.alias = alias;
        if (relationFieldCode != null) {
            this.fields.add(relationFieldCode);
        }
    }
}
