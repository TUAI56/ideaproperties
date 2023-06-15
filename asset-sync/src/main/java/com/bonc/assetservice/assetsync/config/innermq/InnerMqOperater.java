package com.bonc.assetservice.assetsync.config.innermq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * @author suqi
 * @Title: 内部RocketMQ操作封装
 * @Description:
 * @date 2023/3/7 14:06
 */
@Slf4j
@Component
public class InnerMqOperater {

    @Autowired
    DefaultMQProducer  defaultMQProducer;


    /**
　　* @Description: TODO 消息失败重试机制，目前失败后记录日志
　　* @param
     *    topic: 主题
     *   msg:    消息
　　* @return
　　* @throws
　　* @author suqi
　　* @date 2023/3/7 15:30
　　*/
    public void sendMsgToInnerMQ(String topic,String msg) {
        if(StringUtils.isNoneEmpty(topic)) {
            Message message = null;
            switch (topic){
                case "adb-unicom-topic":
                     message = new Message(topic, "UNICOMCLOUD_ASSETS_7", null, msg.getBytes());
                    break;
                case "adb-dim-unicom-topic":
                     message = new Message(topic, "UNICOMCLOUD_ASSETS_DIM_7", null, msg.getBytes());
                    break;
                case "adb-tag-unicom-topic":
                    message = new Message(topic, "UNICOMCLOUD_ASSETS_TAG_7", null, msg.getBytes());
                    break;
                default:
                    break;
            }
           try{
                defaultMQProducer.send(message, new SendCallback() {
                   @Override
                   public void onSuccess(SendResult sendResult) {
                       log.info("[转发资产平台MQ数据] 发送消息队列完成,topic:{},msg:{}",topic,msg);
                   }

                   @Override
                   public void onException(Throwable throwable) {
                        // 异常机制目前记录到日志内
                       log.error("[转发资产平台MQ数据] 发送消息队列失败,原因:{},topic:{},msg:{}",throwable.getMessage(),topic,msg);
                       throwable.printStackTrace();
                   }
               });
           }catch (Exception e){
               log.error("[转发资产平台MQ数据] 发送消息队列失败,原因:{},topic:{},msg:{}",e.getMessage(),topic,msg);
               e.printStackTrace();
           }
        }
    }
}
