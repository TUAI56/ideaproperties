package com.bonc.assetservice.assetsync.consumer;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bonc.assetservice.assetsync.config.innermq.InnerMqOperater;
import com.bonc.assetservice.assetsync.constant.AssetConstant;
import com.bonc.assetservice.assetsync.handler.IAssetDataRegHandler;
import com.bonc.assetservice.assetsync.handler.IAssetRegisterHandler;
import com.bonc.assetservice.assetsync.model.AssetDataMsgVO;
import com.bonc.assetservice.assetsync.model.AssetsMsgVO;
import com.bonc.assetservice.assetsync.model.AttributeDetailDataVO;
import com.bonc.assetservice.assetsync.util.UrlRequestFactory;
import com.bonc.assetservice.metadata.entity.MetaAssetMqLog;
import com.bonc.assetservice.metadata.mapper.MetaAssetMqLogMapper;
import com.bonc.assetservice.metadata.mapper.MetaModelTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class AssetDataRegConsumer {

    @Autowired
    private MetaAssetMqLogMapper metaAssetMqLogMapper;
    @Autowired
    @Qualifier("labelOnlineHandler")
    private IAssetDataRegHandler labelOnlineHandler;
    @Autowired
    @Qualifier("labelOfflineHandler")
    private IAssetDataRegHandler labelOfflineHandler;

    @Value("${yudao.innerrocketmq.adb-unicom-topic}")
    private String adbUnicomTopic;

    @Autowired
    InnerMqOperater mqOperater;

    /**
     * 消息消息队列的数据,进行资产上线,下线有关操作的处理
     * @param messageExt
     */
    public void assetRegister(MessageExt messageExt) {
        log.info("开始处理数据");
        String messageString = new String(messageExt.getBody());
        AssetDataMsgVO assetDataMsgVO = JSONUtil.toBean(messageString, AssetDataMsgVO.class);
        log.info("messageString==>" + messageString);
        //转发资产平台MQ数据到内部服务MQ,供原始标签组消费
        mqOperater.sendMsgToInnerMQ(adbUnicomTopic,messageString);

        MetaAssetMqLog msgLog = MetaAssetMqLog.builder().oid(null).msgId(messageExt.getMsgId()).status(assetDataMsgVO.getStatus())
                .classes(assetDataMsgVO.getClassesId()).createDate(new Date()).msg(messageString).build();
        try {
            metaAssetMqLogMapper.insert(msgLog); // 插入MQ 的记录......
        } catch (Exception e) {
            log.error("消息写入日志异常返回, 消息：" + messageString, e);
            return;
        }

        //oid使用注解IdType.ASSIGN_ID，会自动生成id
        String mqLogId = msgLog.getOid();
        try{
            //修改消息信息 里面资产信息返回的报文
            LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
                    .set(MetaAssetMqLog::getAssetJson,messageString);
            metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);

            //标签资产上下线
            switch (assetDataMsgVO.getClassesId() + assetDataMsgVO.getStatus()) {
                case AssetConstant.LABEL + AssetConstant.ONLINE:
                    labelOnlineHandler.assetHandle(assetDataMsgVO, mqLogId);
                    break;
                case AssetConstant.LABEL + AssetConstant.OFFLINE:
                case AssetConstant.LABEL + AssetConstant.DELETE:
                    labelOfflineHandler.assetHandle(assetDataMsgVO, mqLogId);
                    break;
                default:
            }

//            lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
//                    .set(MetaAssetMqLog::getUpdateDate,new Date())
//                    .set(MetaAssetMqLog::getIsAllow,"1");
//            metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);

        } catch (Exception e) {
            LambdaUpdateWrapper<MetaAssetMqLog> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MetaAssetMqLog::getOid, mqLogId)
                    .set(MetaAssetMqLog::getUpdateDate,new Date())
//                    .set(MetaAssetMqLog::getIsAllow,"1")
                    .set(MetaAssetMqLog::getExceptionMsg,e.getMessage());
            metaAssetMqLogMapper.update(null,lambdaUpdateWrapper);
            e.printStackTrace();
        }
    }




}
