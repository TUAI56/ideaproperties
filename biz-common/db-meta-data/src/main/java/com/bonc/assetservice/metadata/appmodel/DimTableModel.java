package com.bonc.assetservice.metadata.appmodel;


import lombok.Data;
import java.io.Serializable;



@Data
public class DimTableModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

    private String parentCode;

    private String sort;

}
