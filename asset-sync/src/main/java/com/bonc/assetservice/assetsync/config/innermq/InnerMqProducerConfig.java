package com.bonc.assetservice.assetsync.config.innermq;


import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author suqi
 * @Title: 内部RocketMQ生产者
 * @Description: 用户转发收到的资产平台MQ的消息
 * @date 2023/3/7 11:36
 */
@Slf4j
@Configuration
public class InnerMqProducerConfig {


    @Autowired
    private InnerRocketMqProperties propertiesProperties;

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
        producer.setSendLatencyFaultEnable(true);//开启延时故障机制
        producer.setInstanceName("innserMQ");//内部MQ实例，与资产平台MQ实例做区分
        producer.start();
        log.info("innerMQ producer 创建成功, {}, {}", propertiesProperties.getNameSrvSend(), propertiesProperties.getProducerGroup());
        return producer;
    }
}
