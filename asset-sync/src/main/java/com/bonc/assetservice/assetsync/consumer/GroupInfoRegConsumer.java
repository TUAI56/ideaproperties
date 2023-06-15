package com.bonc.assetservice.assetsync.consumer;



import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.config.innermq.InnerMqOperater;
import com.bonc.assetservice.assetsync.handler.impl.GroupInfoServiceHandler;
import com.bonc.assetservice.assetsync.model.GroupInfoMsgVO;
import com.bonc.assetservice.metadata.entity.MetaGrpInfo;
import com.bonc.assetservice.metadata.entity.MetaGrpMqLog;
import com.bonc.assetservice.metadata.mapper.MetaGrpInfoMapper;
import com.bonc.assetservice.metadata.mapper.MetaGrpMqLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GroupInfoRegConsumer {

    @Autowired
    private GroupInfoServiceHandler groupDataServiceHandler;

    @Autowired
    private MetaGrpMqLogMapper metaGrpMqLogMapper;

    @Autowired
    private MetaGrpInfoMapper metaGrpInfoMapper;

    @Autowired
    InnerMqOperater mqOperater;

    @Value("${yudao.innerrocketmq.adb-tag-unicom-topic}")
    private String adbTagUnicomTopic;
    /**
     * 分组信息入库
     * @param messageExt
     */
    public void assetRegister(MessageExt messageExt) {
        log.info("开始处理数据");
        String messageString = new String(messageExt.getBody());
        log.info("messageString ====> {}" + JSON.toJSON(messageString));
        List<GroupInfoMsgVO> groupInfoMsgVOS = JSONUtil.toList(messageString, GroupInfoMsgVO.class);
        log.info("转化后的对象信息为-------> {}",JSON.toJSON(groupInfoMsgVOS));
        mqOperater.sendMsgToInnerMQ(adbTagUnicomTopic,messageString);
        MetaGrpMqLog msgLog = MetaGrpMqLog.builder().oid(null).msgId(messageExt.getMsgId())
                .createDate(new Date()).msg(messageString).build();
        try {
            metaGrpMqLogMapper.insert(msgLog); // 插入MQ 的记录......
        } catch (Exception e) {
            log.error("消息写入日志异常返回, 消息：" + messageString, e);
            return;
        }

        String mqLogId = msgLog.getOid();
        try {
            //清理之前的数据
            QueryWrapper<MetaGrpInfo> queryWrapper = new QueryWrapper<>();
            metaGrpInfoMapper.delete(queryWrapper);
            for (GroupInfoMsgVO groupInfoMsgVO : groupInfoMsgVOS) {
                groupDataServiceHandler.assetHandle(groupInfoMsgVO);
            }
            //分组消息处理入库
        }catch (Exception e){
            LambdaUpdateWrapper<MetaGrpMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MetaGrpMqLog::getOid, mqLogId)
                    .set(MetaGrpMqLog::getUpdateDate,new Date())
                    .set(MetaGrpMqLog::getExceptionMsg,e.getMessage());
            metaGrpMqLogMapper.update(null,lambdaUpdateWrapper);
            e.printStackTrace();
        }
    }
}
