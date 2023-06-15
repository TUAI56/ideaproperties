package com.bonc.assetservice.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AssetMapper {


    @Select("SELECT 23 AS aname,45 AS avalue FROM DUAL ")
    List<Map<String,Object>>  queryInfo();



}
