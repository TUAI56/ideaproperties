package com.bonc.assetservice.assetsync.model;


import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class AttributeDetailDataVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * item 类型: object
     */
    private List<AttrVO> attrList;
    /**
     * 资产id
     */
    private String assetsId;
    /**
     * 资产版本id
     */
    private String assetVersionId;
    /**
     * 元数据OID
     */
    private String tableOid;
    /**
     * 标题
     */
    private String title;
    /**
     * 资产类别主键
     */
    private String classesId;
    /**
     * 描述
     */
    private String description;
    /**
     * 上线时间
     */
    private String onlineTime;
    /**
     * 下线时间
     */
    private String offlineTime;
    /**
     * 创建人
     */
    private String crtUser;
    /**
     * 资产请求返回的json 数据
     */
    private String returnJson;

    public String getReturnJson() {
        return returnJson;
    }

    public void setReturnJson(String returnJson) {
        this.returnJson = returnJson;
    }

    /**
     * 获取 item 类型: object
     *
     * @return attrList item 类型: object
     */
    public List<AttrVO> getAttrList() {
        return this.attrList;
    }

    /**
     * 设置 item 类型: object
     *
     * @param attrList item 类型: object
     */
    public void setAttrList(List<AttrVO> attrList) {
        this.attrList = attrList;
    }

    /**
     * 获取 资产id
     *
     * @return assetsId 资产id
     */
    public String getAssetsId() {
        return this.assetsId;
    }

    /**
     * 设置 资产id
     *
     * @param assetsId 资产id
     */
    public void setAssetsId(String assetsId) {
        this.assetsId = assetsId;
    }

    /**
     * 获取 资产版本id
     *
     * @return assetVersionId 资产版本id
     */
    public String getAssetVersionId() {
        return this.assetVersionId;
    }

    /**
     * 设置 资产版本id
     *
     * @param assetVersionId 资产版本id
     */
    public void setAssetVersionId(String assetVersionId) {
        this.assetVersionId = assetVersionId;
    }

    /**
     * 获取 元数据OID
     *
     * @return tableOid 元数据OID
     */
    public String getTableOid() {
        return this.tableOid;
    }

    /**
     * 设置 元数据OID
     *
     * @param tableOid 元数据OID
     */
    public void setTableOid(String tableOid) {
        this.tableOid = tableOid;
    }

    /**
     * 获取 标题
     *
     * @return title 标题
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * 设置 标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取 资产类别主键
     *
     * @return classesId 资产类别主键
     */
    public String getClassesId() {
        return this.classesId;
    }

    /**
     * 设置 资产类别主键
     *
     * @param classesId 资产类别主键
     */
    public void setClassesId(String classesId) {
        this.classesId = classesId;
    }

    /**
     * 获取 描述
     *
     * @return description 描述
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置 描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取 上线时间
     *
     * @return onlineTime 上线时间
     */
    public String getOnlineTime() {
        return this.onlineTime;
    }

    /**
     * 设置 上线时间
     *
     * @param onlineTime 上线时间
     */
    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    /**
     * 获取 下线时间
     *
     * @return offlineTime 下线时间
     */
    public String getOfflineTime() {
        return this.offlineTime;
    }

    /**
     * 设置 下线时间
     *
     * @param offlineTime 下线时间
     */
    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    /**
     * 获取 创建人
     *
     * @return crtUser 创建人
     */
    public String getCrtUser() {
        return this.crtUser;
    }

    /**
     * 设置 创建人
     *
     * @param crtUser 创建人
     */
    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
    }

    @Override
    public String toString() {
        return "AttributeDetailDataVO{" +
                "attrList=" + attrList +
                ", assetsId='" + assetsId + '\'' +
                ", assetVersionId='" + assetVersionId + '\'' +
                ", tableOid='" + tableOid + '\'' +
                ", title='" + title + '\'' +
                ", classesId='" + classesId + '\'' +
                ", description='" + description + '\'' +
                ", onlineTime='" + onlineTime + '\'' +
                ", offlineTime='" + offlineTime + '\'' +
                ", crtUser='" + crtUser + '\'' +
                '}';
    }
}
