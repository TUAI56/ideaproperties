package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttrVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id 属性
     */
    private String id;
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 属性值
     */
    private String value;
    /**
     * 循环周期
     */
    private String analyticValue;


    @Override
    public String toString() {
        return "AttrVO{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", analyticValue='" + analyticValue + '\'' +
                '}';
    }
}
