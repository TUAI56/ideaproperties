package com.bonc.assetservice.assetsync.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
public class AssetsMsgVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 资产id
     */
    private String assetsId;
    /**
     * 表资产id，只有字段资产不为空，其他都为空
     */
    private String tableAssetsId;
    /**
     * 状态（0上线，1下线）
     */
    private String status;
    /**
     * 资产类型（1表，2文件，3数据集，4视图，5标签，6指标，7字段
     */
    private String classesId;

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
     * 获取 状态（0上线，1下线）
     *
     * @return status 状态（0上线，1下线）
     */
    public String getStatus() {
        return this.status;
    }



    /**
     * 设置 状态（0上线，1下线）
     *
     * @param status 状态（0上线，1下线）
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取 资产类型（1表，2文件，3数据集，4视图，5标签，6指标，7字段
     *
     * @return classesId 资产类型（1表，2文件，3数据集，4视图，5标签，6指标，7字段
     */
    public String getClassesId() {
        return this.classesId;
    }

    /**
     * 设置 资产类型（1表，2文件，3数据集，4视图，5标签，6指标，7字段
     *
     * @param classesId 资产类型（1表，2文件，3数据集，4视图，5标签，6指标，7字段
     */
    public void setClassesId(String classesId) {
        this.classesId = classesId;
    }

//    @Override
//    public String toString() {
//        return "AssetsMsgVO{" +
//                "assetsId='" + assetsId + '\'' +
//                ", status='" + status + '\'' +
//                ", classesId='" + classesId + '\'' +
//                '}';
//    }

    /**
     * 获取 表资产id，只有字段资产不为空，其他都为空
     *
     * @return tableAssetsId 表资产id，只有字段资产不为空，其他都为空
     */
    public String getTableAssetsId() {
        return this.tableAssetsId;
    }

    /**
     * 设置 表资产id，只有字段资产不为空，其他都为空
     *
     * @param tableAssetsId 表资产id，只有字段资产不为空，其他都为空
     */
    public void setTableAssetsId(String tableAssetsId) {
        this.tableAssetsId = tableAssetsId;
    }

}
