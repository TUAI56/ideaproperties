package com.bonc.assetservice.apiserver.consumer.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

/**
 * @author 言曌
 * @date 2020/9/17 2:04 下午
 */
@Slf4j
@Configuration
public class DefaultProducerConfig {


    @Resource
    private RocketMqProperties propertiesProperties;

    /**
     * 创建普通消息发送者实例
     * @return DefaultMQProducer
     * @throws MQClientException
     */
    @Bean
    @Primary
    public DefaultMQProducer defaultProducer() throws MQClientException {
        RPCHook temp = new AclClientRPCHook(new SessionCredentials(propertiesProperties.getAccessKey(),propertiesProperties.getSecretKey()));

        DefaultMQProducer producer = new DefaultMQProducer(propertiesProperties.getProducerGroup(), temp);
        producer.setNamesrvAddr(propertiesProperties.getNameSrvSend());
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.start();
        log.info("default producer 创建成功, {}, {}", propertiesProperties.getNameSrvSend(), propertiesProperties.getProducerGroup());
        return producer;
    }
}
