package com.bonc.assetservice.assetsync.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "yudao.rocketmq")
public class RocketMqProperties {

    /**
     * mq 发送给的地址
     */
    private String namesrvSend;
    /**
     * mq 监听的地址
     */
    private String namesrvAddr;
    /**
     * mq的消费组
     */
    private String consumerGroup;

    /**
     * api 请求的地址
     */
    private String urlDetail;

    private String urlPublished;

    private String detailExternal;

    private String dimExtern;

}
