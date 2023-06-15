package com.bonc.assetservice.assetsync.constant;

/**
 * @author
 */

public enum ApiValueEnum {

    IS_TEMPLATE("827290808556756992", "是否是模板表"),
    DATA_CODE("code", "表英文名"),
    DATA_NAME("name", "表中文名"),
    DATA_INTERVAL("PRIV_TABLE_0016", "表数据周期"),
    DOMAIN_NAME("PUB_1_0008", "归属域名"),
    FILE_NAMES("835881180606259200", "入库文件名称"),
    IS_MASTER("PRIV_TABLE_0001", "是否域主表"),
//    TABLE_TYPE("PRIV_TABLE_0010", "表类型"),
    TABLE_TYPE("PRIV_COL_0009", "表类型"),
//    TABLE_TYPE("attrType", "表类型"),
    SORT_FIELD("sortField","排序字段"),
    SORT_TYPE("sortType","排序类型"),
    PROV_NAME("836965581427666944", "省份名"),
    PUBLISH_RANGE("850094313860272128", "发布范围省份名"),
    DB_USER("PRIV_TABLE_0009", "数据库用户"),
    TENANT_ID("PUB_1_0003", "归属租户名称"),
    GSITXM_CODE("PUB_1_0005", "归属IT项目"),
    GSSJC("PUB_1_0007", "归属数据层"),

    ASSET_CODE("dataCode", "数据编码（资产code）"),
//    ASSET_CODE("FIELD_0003", "字段英文名称"),
    ASSET_TYPE("FIELD_0020", "资产类型"),
    KPI_UNIT("PRIV_COL_0011", "单位"),
    GRP_NAME("PUB_2_0001", "分组"),
    REMARK("PRIV_COL_0006", "业务口径"),
    FIELD_TYPE("fieldType", "字段类型"),
    FIELD_LENGTH("fieldLength", "字段长度"),
    SECURITY_LEVEL("FIELD_0018", "涉密级别"),
    TECHNOLOGY_DESC("PRIV_COL_0007", "技术口径"),
    LINK_DIM("linkDim", "关联码表"),
//    CODE_NAME("PRIV_TABLE_1003","code-name"),
    CODE_NAME("codeName","code-name"),
    OID("oid","字段id"),
    COL_CODE("fieldCode","字段英文名称"),
    COL_NAME("fieldName","字段中文名称"),
    BUSINESS_TYPE("YWLX","业务类型(固网、移网)"),
    DOMAIN_CODE("gsy","归属域"),
    SECURITY_LEVEL_ASSET("FLFJMGJB","敏感等级"),
    SECURITY_TYPE_ASSET("FLFJLB","敏感类型"),
    COMMENTS("comments","备注说明"),
    IS_MASTER_KEY("PRIV_COL_0008","是否域主键"),
    IS_DATE("PRIV_COL_0015","是否帐期字段"),
    DATE_FORMAT("PRIV_COL_0016","帐期格式"),
    REGI_DATE("effectiveLabelDate","标签生效日期"),
    COL_SORT("fieldOrd","入库顺序"),
    ;

    private String value;
    private String describe;

    ApiValueEnum(String value, String describe) {
        this.value = value;
        this.describe = describe;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
