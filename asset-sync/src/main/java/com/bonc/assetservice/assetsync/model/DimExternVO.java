package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DimExternVO extends BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<DimExternDataVO> data;



    public List<DimExternDataVO> getData() {
        return data;
    }

    public void setData(List<DimExternDataVO> data) {
        this.data = data;
    }


}
