package com.bonc.assetservice.assetsync.config.listener;


import com.bonc.assetservice.assetsync.config.RocketMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 言曌
 * @date 2020/9/15 7:15 下午
 */
@Slf4j
@Configuration
public abstract class AbstractListener {


    @Autowired
    private RocketMqProperties consumerProperties;


    /**
     * 开启消费注册
     *
     * @param topic
     * @param tags  支持多个tag, 如 tag1 || tag2 || tag3
     * @throws MQClientException
     */
    public void listener(String topic, String tags) throws MQClientException {
        log.info("启动 topic:[" + topic + "], tags:[" + tags + "] 的消费者");
        String[] groups = consumerProperties.getConsumerGroup().split("\\|");
        String group = groups[0];
        if("adb-tag-unicom-topic".equals(topic)){
            group=groups[1];
        }else if("adb-dim-unicom-topic".equals(topic)){
            group=groups[2];
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(consumerProperties.getNamesrvAddr());
        consumer.subscribe(topic, tags);
        // 开启内部类实现监听
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info(Thread.currentThread().getName() + " 接收到消息：{}" + msgs);
                return AbstractListener.this.onMessage(msgs);
            }
        });
        consumer.setInstanceName("zcptMQ");//资产平台MQ实例
        consumer.start();
    }

    /**
     * 处理body的业务
     *
     * @param msgs
     * @return
     */
    public abstract ConsumeConcurrentlyStatus onMessage(List<MessageExt> msgs);

}
