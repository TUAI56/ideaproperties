package com.bonc.assetservice.metadata.constant;

/**
 * 标签元数据库中的通用常量
 */
public class MetaDataConst {

    /**
     * is_valid字段：  -2：上线未生效
     *                 -1：已删除
     *                  0：失效
     *                  1：生效
     */
    public static final String IS_VALID_NOT_EFFECTIVE="-2";
    public static final String IS_VALID_DELETE="-1";
    public static final String IS_VALID_NO="0";
    public static final String IS_VALID_YES="1";

    /**
     * 用户域/家庭域相关字段配置
     */
    //用户域域主表名称
    public static final String USER_DOMAIN_MASTER_TABLE_CODE = "user_domain_master_table";

    //家庭域域主表名称
    public static final String FAMILY_DOMAIN_MASTER_TABLE_CODE = "family_domain_master_table";

    //用户域/家庭域域主键
    public static final String USER_FAMILY_DOMAIN_MASTER_KEY = "user_id";

    //政企域主键
    public static final String ENTERPRICES_DOMAIN_MASTER_KEY = "nature_key";


    //用户域
    public static final String USER_DOMAIN_ID = "20210602022";
    //用户域
    public static final String FAMILY_DOMAIN_ID = "20210602024";

    //政企域
    public static final String ENTERPRICES_DOMAIN_ID = "20210602023";

    public static final String USER_FAMILY_DOMAIN_DEVICE_NUMBER = "device_number";
    public static final String USER_FAMILY_DOMAIN_ACCT_KEY = "date_id";
    public static final String USER_FAMILY_DOMAIN_PROV_KEY = "prov_id";
    public static final String USER_FAMILY_DOMAIN_AREA_KEY = "area_id";
    public static final String USER_FAMILY_DOMAIN_CITY_KEY = "city_id";
    public static final Integer DB_DEL_FLAG_Y = 1;
    public static final Integer DB_DEL_FLAG_N = 0;

    //日期格式yyyyMMdd格式检验正则
    public static  final String DATE_FORMAT_YYYYMMDD = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]| [12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
}
