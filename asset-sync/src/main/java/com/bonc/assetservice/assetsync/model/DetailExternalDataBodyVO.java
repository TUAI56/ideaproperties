package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DetailExternalDataBodyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**入库顺序*/
    private Integer ord;
    /**
     * 字段中文名称
     */
    private String fieldName;
    /**
     * 备注说明
     */
    private String comments;
    /**
     * 是否域主键
     */
    private String SFYZJ;
    /**
     * 字段英文名称
     */
    private String fieldCode;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 帐期格式
     */
    private String ZQGS;
    /**
     * 技术口径
     */
    private String JSKJ;
    /**
     * 保密等级
     */
    private String BMDJ;
    /**
     * 是否帐期字段
     */
    private String SFZQZD;
    /**
     * 单位
     */
    private String DW;
    /**
     * 业务口径
     */
    private String YWKJ;
    /**
     * 是否分区字段
     */
    private String SFFQZD;
    /**
     * 维表设置
     */
    private String WBSZ;
    /**
     * id
     */
    private String id;
    /**
     * 归属表ID
     */
    private String tableOid;
    /**
     * 数据类型
     */
    private String fieldType;
    /**
     * 数据长度
     */
    private String fieldLength;

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    /**
     * 获取 字段中文名称
     *
     * @return fieldName 字段中文名称
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * 设置 字段中文名称
     *
     * @param fieldName 字段中文名称
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 获取 备注说明
     *
     * @return comments 备注说明
     */
    public String getComments() {
        return this.comments;
    }

    /**
     * 设置 备注说明
     *
     * @param comments 备注说明
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * 获取 是否域主键
     *
     * @return SFYZJ 是否域主键
     */
    public String getSFYZJ() {
        return this.SFYZJ;
    }

    /**
     * 设置 是否域主键
     *
     * @param SFYZJ 是否域主键
     */
    public void setSFYZJ(String SFYZJ) {
        this.SFYZJ = SFYZJ;
    }

    /**
     * 获取 字段英文名称
     *
     * @return fieldCode 字段英文名称
     */
    public String getFieldCode() {
        return this.fieldCode;
    }

    /**
     * 设置 字段英文名称
     *
     * @param fieldCode 字段英文名称
     */
    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    /**
     * 获取 帐期格式
     *
     * @return ZQGS 帐期格式
     */
    public String getZQGS() {
        return this.ZQGS;
    }

    /**
     * 设置 帐期格式
     *
     * @param ZQGS 帐期格式
     */
    public void setZQGS(String ZQGS) {
        this.ZQGS = ZQGS;
    }

    /**
     * 获取 技术口径
     *
     * @return JSKJ 技术口径
     */
    public String getJSKJ() {
        return this.JSKJ;
    }

    /**
     * 设置 技术口径
     *
     * @param JSKJ 技术口径
     */
    public void setJSKJ(String JSKJ) {
        this.JSKJ = JSKJ;
    }

    /**
     * 获取 保密等级
     *
     * @return BMDJ 保密等级
     */
    public String getBMDJ() {
        return this.BMDJ;
    }

    /**
     * 设置 保密等级
     *
     * @param BMDJ 保密等级
     */
    public void setBMDJ(String BMDJ) {
        this.BMDJ = BMDJ;
    }

    /**
     * 获取 是否帐期字段
     *
     * @return SFZQZD 是否帐期字段
     */
    public String getSFZQZD() {
        return this.SFZQZD;
    }

    /**
     * 设置 是否帐期字段
     *
     * @param SFZQZD 是否帐期字段
     */
    public void setSFZQZD(String SFZQZD) {
        this.SFZQZD = SFZQZD;
    }

    /**
     * 获取 单位
     *
     * @return DW 单位
     */
    public String getDW() {
        return this.DW;
    }

    /**
     * 设置 单位
     *
     * @param DW 单位
     */
    public void setDW(String DW) {
        this.DW = DW;
    }

    /**
     * 获取 业务口径
     *
     * @return YWKJ 业务口径
     */
    public String getYWKJ() {
        return this.YWKJ;
    }

    /**
     * 设置 业务口径
     *
     * @param YWKJ 业务口径
     */
    public void setYWKJ(String YWKJ) {
        this.YWKJ = YWKJ;
    }

    /**
     * 获取 是否分区字段
     *
     * @return SFFQZD 是否分区字段
     */
    public String getSFFQZD() {
        return this.SFFQZD;
    }

    /**
     * 设置 是否分区字段
     *
     * @param SFFQZD 是否分区字段
     */
    public void setSFFQZD(String SFFQZD) {
        this.SFFQZD = SFFQZD;
    }

    /**
     * 获取 维表设置
     *
     * @return WBSZ 维表设置
     */
    public String getWBSZ() {
        return this.WBSZ;
    }

    /**
     * 设置 维表设置
     *
     * @param WBSZ 维表设置
     */
    public void setWBSZ(String WBSZ) {
        this.WBSZ = WBSZ;
    }

    /**
     * 获取 id
     *
     * @return id id
     */
    public String getId() {
        return this.id;
    }

    /**
     * 设置 id
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取 归属表ID
     *
     * @return tableOid 归属表ID
     */
    public String getTableOid() {
        return this.tableOid;
    }

    /**
     * 设置 归属表ID
     *
     * @param tableOid 归属表ID
     */
    public void setTableOid(String tableOid) {
        this.tableOid = tableOid;
    }

    /**
     * 获取 数据类型
     *
     * @return fieldType 数据类型
     */
    public String getFieldType() {
        return this.fieldType;
    }

    /**
     * 设置 数据类型
     *
     * @param fieldType 数据类型
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * 获取 数据长度
     *
     * @return fieldLength 数据长度
     */
    public String getFieldLength() {
        return this.fieldLength;
    }

    /**
     * 设置 数据长度
     *
     * @param fieldLength 数据长度
     */
    public void setFieldLength(String fieldLength) {
        this.fieldLength = fieldLength;
    }
}
