package com.bonc.assetservice.apiserver.consumer.config;


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
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMqProperties {

    /**
     * mq 发送给的地址
     */
    private String nameSrvSend;
    /**
     * 作为生产者的group
     */
    private String producerGroup;

    private String accessKey;

    private String secretKey;


}
