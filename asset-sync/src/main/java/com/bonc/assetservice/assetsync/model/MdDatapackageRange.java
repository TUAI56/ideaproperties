package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;

/**
 * md_datapackage_range
 * @author 
 */
@Data
public class MdDatapackageRange implements Serializable {
    /**
     * 数据表id
     */
    private String dataId;

    /**
     * 省份id
     */
    private String provRangeId;

    private String provId;

    public MdDatapackageRange() {
    }

    public MdDatapackageRange(String dataId, String provRangeId, String provId) {
        this.dataId = dataId;
        this.provRangeId = provRangeId;
        this.provId = provId;
    }

    private static final long serialVersionUID = 1L;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getProvRangeId() {
        return provRangeId;
    }

    public void setProvRangeId(String provRangeId) {
        this.provRangeId = provRangeId;
    }

    public String getProvId() {
        return provId;
    }

    public void setProvId(String provId) {
        this.provId = provId;
    }
}