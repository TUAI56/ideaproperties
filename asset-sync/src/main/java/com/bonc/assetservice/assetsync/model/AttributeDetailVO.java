package com.bonc.assetservice.assetsync.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class AttributeDetailVO extends BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private AttributeDetailDataVO data;



}
