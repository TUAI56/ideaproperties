package com.bonc.assetservice.assetsync.model;

import java.io.Serializable;
import java.util.List;

public class DetailExternalDataVO implements Serializable {

    private static final long serialVersionUID = 1L;
    List<DetailExternalDataBodyVO> body;

    public List<DetailExternalDataBodyVO> getBody() {
        return body;
    }

    public void setBody(List<DetailExternalDataBodyVO> body) {
        this.body = body;
    }
}
