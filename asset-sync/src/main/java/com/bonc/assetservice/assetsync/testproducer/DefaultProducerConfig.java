package com.bonc.assetservice.assetsync.testproducer;


import com.bonc.assetservice.assetsync.config.RocketMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author 言曌
 * @date 2020/9/17 2:04 下午
 */
@Slf4j
//@Configuration
public class DefaultProducerConfig {


    @Autowired
    private RocketMqProperties propertiesProperties;

    /**
     * 创建普通消息发送者实例
     * @return
     * @throws MQClientException
     */
    @Bean
    //@Primary
    public DefaultMQProducer defaultProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(propertiesProperties.getConsumerGroup());
        producer.setNamesrvAddr(propertiesProperties.getNamesrvSend());
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.start();
        log.info("default producer 创建成功, {}, {}", propertiesProperties.getNamesrvSend(), propertiesProperties.getConsumerGroup());
        return producer;
    }
}
