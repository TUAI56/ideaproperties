package com.bonc.assetservice.assetsync.handler.impl;

import com.alibaba.fastjson2.JSON;

import com.bonc.assetservice.assetsync.handler.IgroupInfoHandler;
import com.bonc.assetservice.assetsync.model.GroupInfoMsgVO;
import com.bonc.assetservice.metadata.mapper.MetaGrpInfoMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service("GroupInfoHandler")
public class GroupInfoServiceHandler implements IgroupInfoHandler {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private MetaGrpInfoMapper metaGrpInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void assetHandle(GroupInfoMsgVO msgVO) {
        log.info("分组信息入库开始 -------->");
        List<Map<String, Object>> dataList = new ArrayList<>();
        ArrayList<GroupInfoMsgVO> children = new ArrayList<>();
        HashMap<String, Integer> numMap = new HashMap<>();
        numMap.put("num",0);
        children.add(msgVO);
        //数据放入
        List<Map<String, Object>> groupInfoList = null;
        try {
             groupInfoList = getGroupInfo(dataList, children, "", 0, "","0",numMap);
        }catch (Exception e){
            log.info("mq消息处理异常 {}",e.getMessage());
        }
        //删除redis缓存
//        Set<String> keys = redisTemplate.keys("grp#*");
//        if(null!=keys || !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//            log.info("删除分组信息的缓存key --------> {}",keys);
//        }
        ScanOptions options = ScanOptions.scanOptions().match("grp#*").count(100)
                .build();
        Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                        redisTemplate.getKeySerializer()::deserialize));

        cursor.forEachRemaining(key -> redisTemplate.delete(key));
        int i = metaGrpInfoMapper.insertGroupInfo(groupInfoList);
        log.info("受影响行数为--------> {}",i);
    }

    public synchronized List<Map<String,Object>> getGroupInfo(List<Map<String,Object>> dataList,List<GroupInfoMsgVO> children, String domainCode, int count, String idPath,String parentGrpCode,Map<String, Integer> numMap){
        for (GroupInfoMsgVO child : children) {
            if(count==1){
              domainCode = child.getCode();
            }
            HashMap<String, Object> dataMap = new HashMap<>();
            if(count==0){
                child.setId(child.getChildren().get(0).getParentId());
            }
            numMap.put("num",numMap.get("num")+1);
            dataMap.put("id",child.getId());
            dataMap.put("grp_code",child.getCode());
            dataMap.put("grp_name",child.getName());
            dataMap.put("parent_id", child.getParentId());
            dataMap.put("parent_grp_code",parentGrpCode);
            dataMap.put("path",child.getTitle());
            dataMap.put("id_path",idPath+"/"+child.getId());
            dataMap.put("domain_code", domainCode);
            dataMap.put("grp_sort", numMap.get("num"));
            dataMap.put("level",count);
            dataMap.put("link_id",child.getLinkId());
            dataList.add(dataMap);
            if(null!=child.getChildren() && !child.getChildren().isEmpty()){
                getGroupInfo(dataList, child.getChildren(), domainCode, count + 1, idPath + "/" + child.getId(),child.getCode(),numMap);
            }
        }

        return dataList;
    }
}
