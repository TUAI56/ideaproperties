package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口返回的编号
     */
    private Integer code;
    /**
     * 接口返回的信息
     */
    private String msg;



}
