package com.bonc.assetservice.assetsync.consumer;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.config.innermq.InnerMqOperater;
import com.bonc.assetservice.assetsync.handler.IdimInfoHandler;
import com.bonc.assetservice.assetsync.handler.impl.DimInfoServiceHandler;
import com.bonc.assetservice.assetsync.model.DimInfoMsgVO;
import com.bonc.assetservice.assetsync.model.GroupInfoMsgVO;
import com.bonc.assetservice.metadata.entity.MetaDimMqLog;
import com.bonc.assetservice.metadata.entity.MetaGrpInfo;
import com.bonc.assetservice.metadata.entity.MetaGrpMqLog;
import com.bonc.assetservice.metadata.mapper.MetaDimMqLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DimInfoRegConsumer {

    @Autowired
    private IdimInfoHandler dimInfoServiceHandler;

    @Autowired
    private MetaDimMqLogMapper metaDimMqLogMapper;

    @Autowired
    InnerMqOperater mqOperater;

    @Value("${yudao.innerrocketmq.adb-dim-unicom-topic}")
    private String adbDimUnicomTopic;

    public void assetRegister(MessageExt messageExt) {
        log.info("开始处理数据");
        String messageString = new String(messageExt.getBody());
        log.info("messageString ====> {}" + JSON.toJSON(messageString));
        mqOperater.sendMsgToInnerMQ(adbDimUnicomTopic,messageString);
        DimInfoMsgVO dimInfoMsgVO = JSONUtil.toBean(messageString, DimInfoMsgVO.class);

        MetaDimMqLog msgLog = MetaDimMqLog.builder().oid(null).msgId(messageExt.getMsgId())
                .createDate(new Date()).msg(messageString).build();
        try {
            metaDimMqLogMapper.insert(msgLog); // 插入MQ 的记录......
        } catch (Exception e) {
            log.error("消息写入日志异常返回, 消息：" + messageString, e);
            return;
        }

        String mqLogId = msgLog.getOid();

        try {
            //码表信息入库
            dimInfoServiceHandler.assetHandle(dimInfoMsgVO);

        }catch (Exception e){
            LambdaUpdateWrapper<MetaDimMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MetaDimMqLog::getOid, mqLogId)
                    .set(MetaDimMqLog::getUpdateDate,new Date())
                    .set(MetaDimMqLog::getExceptionMsg,e.getMessage());
            metaDimMqLogMapper.update(null,lambdaUpdateWrapper);
            e.printStackTrace();
        }
    }

}
