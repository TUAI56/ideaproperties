package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DetailExternalVO extends BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private DetailExternalDataVO data;

    private String returnJson;



}
