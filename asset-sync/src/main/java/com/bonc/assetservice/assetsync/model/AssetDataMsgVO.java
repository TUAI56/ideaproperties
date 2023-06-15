package com.bonc.assetservice.assetsync.model;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class AssetDataMsgVO implements Serializable {
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
     * 资产版本id
     */
    private String assetVersionId;
    /**
     * 资产版本号-数字递增
     */
    private String assetVersionNo;
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
     * 表元数据id
     */
    private String tableOid;
    /**
     * 资产名称
     */
    private String title;
    /**
     * 是否域主表
     */
    private String PRIV_TABLE_0001;
    /**
     * 标签资产所属用户
     */
    private String tenantId;
    /**
     * 数据更新周期
     */
    private String PRIV_TABLE_0016;
    /**
     * 创建用户id
     */
    private String crtUser;

    /**
     * 入库文件名称
     */
    private String indbFileName;
    /**
     * 样例值
     */
    private String sampleValues;

    /**
     * 属性列表
     */
    private Object attrList;


}
