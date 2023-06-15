package com.bonc.assetservice.apiserver.server.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMq配置信息
 * @Author 李维帅
 * @Date 2022/7/6 10:41
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.rocketmq")
public class RocketMqProperties {

    /**
     * mq 发送给的地址
     */
    private String nameSrvSend;
    /**
     * mq的消费组
     */
    private String consumerGroup;

    private String accessKey;

    private String secretKey;
    private String topic;
    private String datasetupddatatopic;


}
