package com.bonc.assetservice.metadata.appmodel;

import lombok.Data;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/19
 * @Description:
 */

@Data
public class ModelAndEntityTableInfo {
    /**
     * 模板表编码
     */
    private String modelCode;

    /**
     * 实体表编码
     */
    private String tableCode;

    /**
     * 是否域主表
     */
    private String isMaster;

    /**
     * 表的默认账期
     */
    private String acct;

    /**
     * 所属域
     */
    private String domainGrpId;
}
