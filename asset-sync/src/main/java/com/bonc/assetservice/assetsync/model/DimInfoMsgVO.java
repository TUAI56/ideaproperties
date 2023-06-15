package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.util.List;

@Data
public class DimInfoMsgVO {
    /**
     * 编码表OID
     */
    private String codeId;
    /**
     * 编码表code
     */
    private String codeCode;
    /**
     * 编码表名称
     */
    private String codeName;
    /**
     * code-name映射关系
     */
    private String mapping;
}
