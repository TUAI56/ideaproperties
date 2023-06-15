package com.bonc.assetservice.metadata.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */

public enum ProvinceEnum {

    Province_JT("集团", "099"),
    Province_AH("安徽", "030"),
    Province_BJ("北京", "011"),
    Province_FJ("福建", "038"),
    Province_GS("甘肃", "087"),
    Province_GD("广东", "051"),
    Province_GX("广西", "059"),
    Province_GZ("贵州", "085"),
    Province_HI("海南", "050"),
    Province_HE("河北", "018"),
    Province_HA("河南", "076"),
    Province_HL("黑龙江", "097"),
    Province_HB("湖北", "071"),
    Province_HN("湖南", "074"),
    Province_JL("吉林", "090"),
    Province_JS("江苏", "034"),
    Province_JX("江西", "075"),
    Province_LN("辽宁", "091"),
    Province_NM("内蒙古", "010"),
    Province_NX("宁夏", "088"),
    Province_QH("青海", "070"),
    Province_SD("山东", "017"),
    Province_SX("山西", "019"),
    Province_SN("陕西", "084"),
    Province_SH("上海", "031"),
    Province_SC("四川", "081"),
    Province_TJ("天津", "013"),
    Province_XZ("西藏", "079"),
    Province_XJ("新疆", "089"),
    Province_YN("云南", "086"),
    Province_ZJ("浙江", "036"),
    Province_CQ("重庆", "083"),

    ;

    private String name;

    private String id;

    private String describe;

    ProvinceEnum(String name, String id) {
        this.name = name;
        this.id = id;
    }

    ProvinceEnum(String apiValue, String localValue, String describe) {
        this.name = apiValue;
        this.id = localValue;
        this.describe = describe;
    }


    public static String getId(String name) {
        if (name == null) {
            return null;
        }
        for (ProvinceEnum e : ProvinceEnum.values()) {
            if (e.getName().equals(name)) {
                return e.getId();
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }



    // 方式一、每次取枚举用for循环遍历
//    public static ProvinceEnum getByCode(String code) {
//        for (ProvinceEnum it : ProvinceEnum.values()) {
//            if (it.getId() == code) {
//                return it;
//            }
//        }
//        return null;
//    }
    // 方式二、放入map中，通过键取值
    private static Map<String,ProvinceEnum > zyMap = new HashMap<>();
    static {
        for (ProvinceEnum value : ProvinceEnum .values()) {
            zyMap.put(value.getId(),value);
        }
    }
    public static ProvinceEnum getByCode(String code){
        return zyMap.get(code);
    }

}
