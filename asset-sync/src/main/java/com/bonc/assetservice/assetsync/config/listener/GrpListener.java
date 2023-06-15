package com.bonc.assetservice.assetsync.config.listener;

import com.bonc.assetservice.assetsync.consumer.GroupInfoRegConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * 分组信息入库
 * @author lht
 */
@Configuration
@Slf4j
public class GrpListener extends AbstractListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private GroupInfoRegConsumer groupInfoRegConsumer;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String topic="adb-tag-unicom-topic";

            /**
             * 订阅主题和标签（ * 代表所有标签)下信息
             * 	UNICOMCLOUD_ASSETS_1 表资产
             * 	UNICOMCLOUD_ASSETS_2 文件资产
             * 	UNICOMCLOUD_ASSETS_3 数据集资产
             * 	UNICOMCLOUD_ASSETS_4 视图资产
//             * 	UNICOMCLOUD_ASSETS_5 标签资产
             * 	UNICOMCLOUD_ASSETS_6 指标资产
             * 	UNICOMCLOUD_ASSETS_7 标签资产
             */
            // 订阅同一个topic下的tag：标签资产
            String subExpression = "UNICOMCLOUD_ASSETS_TAG_7";
            super.listener(topic, subExpression);
        } catch (MQClientException e) {
            log.error("consumer error");
            e.printStackTrace();
        }
    }
    @Override
    public ConsumeConcurrentlyStatus onMessage(List<MessageExt> msgs) {
        log.info("接收消息=====================成功");
        for (MessageExt msg : msgs) {
            try {
                groupInfoRegConsumer.assetRegister(msg);
            } catch (Exception e) {
                log.error("处理消息出现异常，消息体:" + msg.toString() ,e);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
