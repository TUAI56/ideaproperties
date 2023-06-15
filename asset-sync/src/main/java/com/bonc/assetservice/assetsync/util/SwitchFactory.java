package com.bonc.assetservice.assetsync.util;

import lombok.Data;

/**
 * @author  数据类型得匹配区分
 */
@Data
public class SwitchFactory {

    public static String fieldType(String value) {
        if (value == null) {
            return null;
        }
        String res = null;
        switch (value) {
            case "95440":
                res = "INT";
                break;
            case "95441":
                res="FLOAT";
                break;
            case "954411":
                res = "DOUBLE";
                break;
            case "95442":
                res = "VARCHAR";
                break;
            case "95443":
                res = "DATATIME";
                break;
            case "954401":
                res = "BIGINT";
                break;
            default:
        }
        return res;
    }

    public static String assetType(String value) {
        if (value == null) {
            return null;
        }
        String res = null;
        switch (value) {
            //指标--数值型标签（横表）
            case "20210413002":
                res = "1";
                break;
            //维度--维度型标签（横表）
            case "20210413003":
                res = "2";
                break;
            //有值标签--字典型标签（横表）
            case "20210413004":
                res = "3";
                break;
            //无值标签--文本型标签（横表）
            case "20210413005":
                res = "4";
                break;
            //指标集指标--数值型标签（纵表）
            case "20210413006":
                res = "21";
                break;
            //指标集维度--维度型标签（纵表）
            case "20210413007":
                res = "22";
                break;
            //指标集有值标签--字典型标签（纵表）
            case "20210413008":
                res = "23";
                break;
            //指标集无值标签--文本型标签（纵表）
            case "20210413009":
                res = "24";
                break;
            default:
        }
        return res;
    }

    public static String dataInterval(String value) {
        if (value == null) {
            return null;
        }
        String res = null;
        switch (value) {
            case "一次性":
                res = "RD";     
                break;
            case "日":
                res = "D";
                break;
            case "月":
                res = "M";
                break;
            case "年":
                res = "Y";
                break;
            case "周":
                res="W";
            case "时":
                res = "H";
                break;
            case "实时":
                res = "RT";
                break;
            case "季度":
                res="Q";
            default:
        }
        return res;
    }

    public static String isNot(String value) {
        if (value == null) {
            return null;
        }
        String res = null;
        switch (value) {
            case "否":
                res = "0";
                break;
            case "是":
                res = "1";
                break;
            default:
        }
        return res;
    }

    public static String tableType(String value) {
        if (value == null) {
            return null;
        }
        String res = null;
        switch (value) {
            case "横表":
                res = "H";
                break;
            case "纵表":
                res = "T";
                break;
            default:
        }
        return res;
    }

    public static String securityLevel(String value) {
        if (value == null) {
            return null;
        }
        String res;
        switch (value) {
            case "20210225050":
                res = "1";
                break;
            case "20210225051":
                res = "2";
                break;
            case "20210225052":
            default:
                res = "3";
        }
        return res;
    }

    public static String isPrimarykey(String value) {
        if (value == null) {
            return "0";
        }
        String res;
        switch (value) {
            case "20210225002":
                res = "1";
                break;
            case "20210225001":
            default:
                res = "0";
        }
        return res;
    }

    public static String kpiType(String value) {
        if (value == null) {
            return null;
        }
        String res;
        switch (value) {
            case "20210225042":
                res = "U10892";
                break;
            case "20210330001":
                res = "U10895";
                break;
            case "20210330002":
                res = "U11443";
                break;
            case "20210330003":
                res = "U10898";
                break;
            case "20210330004":
                res = "U10899";
                break;
            case "20210330005":
                res = "U10900";
                break;
            case "20210330006":
                res = "U10901";
                break;
            case "20210330007":
                res = "U11442";
                break;
            case "20210330008":
                res = "U10902";
                break;
            case "20210330009":
                res = "U10904";
                break;
            case "20210330010":
                res = "U10905";
                break;
            case "20210330011":
                res = "U10906";
                break;
            case "20210330012":
                res = "U10907";
                break;
            case "20210330013":
                res = "U10908";
                break;
            case "20210330014":
                res = "U10909";
                break;
            case "20210330015":
                res = "U10911";
                break;
            case "20210330016":
                res = "U11444";
                break;
            case "20210330017":
                res = "U11240";
                break;
            case "20210330018":
                res = "U10913";
                break;
            case "20210330019":
                res = "U10914";
                break;
            case "20210330020":
                res = "U10916";
                break;
            case "20210330021":
                res = "U10917";
                break;
            case "20210330022":
                res = "U11247";
                break;
            case "20210330023":
                res = "U11248";
                break;
            case "20210330024":
                res = "U11257";
                break;
            case "20210330025":
                res = "U11258";
                break;
            case "20210330026":
                res = "U11259";
                break;
            case "20210330027":
                res = "U11445";
                break;
            case "20210330028":
                res = "U11446";
                break;
            default:
                res = null;
        }
        return res;
    }

    public static String setGrp(String value) {
        if (value == null) {
            return null;
        }
        String res = null;
        switch (value) {
            case "001":
            case "008":
                res = "004";
                break;
            case "002":
            case "006":
            case "007":
                res = "005";
                break;
            case "010":
            case "011":
            case "012":
            case "013":
            case "014":
            case "015":
            case "016":
            case "017":
            case "018":
            case "019":
            case "020":
                res = "006";
                break;
            case "004":
            case "005":
                res = "007";
                break;
            case "003":
            case "009":
                res = "009";
                break;
            default:
        }
        return res;
    }

}
