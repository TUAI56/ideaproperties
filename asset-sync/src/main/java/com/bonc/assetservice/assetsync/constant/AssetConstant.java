package com.bonc.assetservice.assetsync.constant;

/**
 * @author 
 */
public interface AssetConstant {

    /**
     * 1 表
     */
    String TABLE = "1";
    /**
     * 2 文件
     */
    String FILE = "2";
    /**
     * 3 数据集
     */
    String DATA_SET = "3";
    /**
     * 4 视图
     */
    String VIEW = "4";
    /**
     * 5 字段
     */
    String FIELD = "5";
    /**
     * 6 指标
     */
    String QUOTA = "6";
    /**
     * 7 标签
     */
    String LABEL = "7";

    /**
     * 0上线
     */
    String ONLINE = "0";
    /**
     * 1下线
     */
    String OFFLINE = "1";
    /**
     * 4删除状态
     */
    String DELETE = "4";
    /**
     * -2：上线未生效
     * -1：已删除
     * 0：失效
     * 1：生效
     */
    String IS_VALID_INITIAL = "-2";
    String IS_VALID_DELETE = "-1";
    String IS_INVALID= "0";
    String IS_VALID= "1";
    /**
     * 是否是模板表
     */
    String IS_TEMPLATE = "1";
}
