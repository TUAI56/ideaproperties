package com.bonc.assetservice.apiserver.server.assetquery.constant;

import java.util.stream.Stream;

/**
 * 聚合类型
 *
 * @author zhangxiaonan, zjlong
 */
public enum AggregationType {
    SUM(true,"SUM"),
    COUNT(true,"COUNT"),
    AVG(true,"AVG"), 
    MAX(true,"MAX"),
    MIN(true,"MIN");

/*
    MEDIAN(true,"MEDIAN"),
    QUARTAIL1(false,"QUARTAIL1"),
    QUARTAIL3(false,"QUARTAIL3"), 
    MODE(false,"MODE"),
    VARIANCE(true,"VARIANCE"),
    STDDEV(true,"STDDEV");
*/
    
    /**
     * 能否被数据库支持
     */
    private boolean dbSupported;
    /**
     * 在Script表达式中的写法
     */
    private String identifier;
    
    AggregationType(boolean dbSupported, String identifier) {
        this.dbSupported = dbSupported;
        this.identifier = identifier;
    }

    public boolean isDbSupported() {
        return dbSupported;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public static boolean containsFunction(String identifier) {
        return Stream.of(AggregationType.values())
                .map(AggregationType::getIdentifier).anyMatch(i -> i.equalsIgnoreCase(identifier));
    }

    public static boolean isAggrFunction(String identifier) {
        return Stream.of(AggregationType.values())
                .map(AggregationType::getIdentifier).anyMatch(i -> i.equalsIgnoreCase(identifier));
    }

    @Override
    public String toString() {
        return identifier;
    }
    
}
