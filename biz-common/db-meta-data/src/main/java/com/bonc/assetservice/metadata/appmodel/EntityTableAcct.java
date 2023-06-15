package com.bonc.assetservice.metadata.appmodel;

import lombok.Data;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/6/20
 * @Description:
 */


@Data
public class EntityTableAcct {
    /**
     * tableCode
     */
    private String tableCode;
    /**
     * 最近账期
     */
    private String acct;
}
