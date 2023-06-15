package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DimExternDataVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 编码
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 库
     */
    private String dbName;
    /**
     * 用户
     */
    private String owner;
    /**
     * 表
     */
    private String tableCode;
    /**
     *
     */
    private String returnJson;

    public String getReturnJson() {
        return returnJson;
    }

    public void setReturnJson(String returnJson) {
        this.returnJson = returnJson;
    }
    /**
     * 获取 编码
     *
     * @return id 编码
     */
    public String getId() {
        return this.id;
    }

    /**
     * 设置 编码
     *
     * @param id 编码
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取 名称
     *
     * @return name 名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置 名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取 库
     *
     * @return dbName 库
     */
    public String getDbName() {
        return this.dbName;
    }

    /**
     * 设置 库
     *
     * @param dbName 库
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取 用户
     *
     * @return owner 用户
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * 设置 用户
     *
     * @param owner 用户
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * 获取 表
     *
     * @return tableCode 表
     */
    public String getTableCode() {
        return this.tableCode;
    }

    /**
     * 设置 表
     *
     * @param tableCode 表
     */
    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }
}