package com.bonc.assetservice.metadata.appmodel;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/16
 * @Description:
 */

@Data
public class AssetCodeAndAcct implements Serializable {
    private static final long serialVersionUID = 8477670246891922320L;

    /**
     * 标签ID
     */
    private Long assetId;

    /**
     * 模板表编码
     */
    private String modelCode;
    /**
     * 标签编码
     */
    private String AssetCode;
    /**
     * 账期字段
     */
    private String defaultAcct;
    /**
     * 所属实体表编码
     */
    private String tableCode;
}
