package com.bonc.assetservice.apiserver.data.model;

import lombok.Data;

import java.util.List;

@Data
public class DataSetCodeAndColum {
    private String dataSetId;

    private String tableCode;

    private String joinColumnCode;

    List<String> colum;
}
