package com.bonc.assetservice.assetsync.config.innermq;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author suqi
 * @Title: 内部RocketMQ配置属性
 * @Description: 内部MQ需要配置三个主题:
 *                  标签数据主题: adb-unicom-topic  标签：UNICOMCLOUD_ASSETS_7
 *                  目录树信息主题: adb-tag-unicom-topic  标签: UNICOMCLOUD_ASSETS_TAG_7
 *                  码表信息主题: adb-dim-unicom-topic		标签： UNICOMCLOUD_ASSETS_DIM_7
 *                需要配置一个消费者组: GID_DISPATCH_GROUP
 * @date 2023/3/7 11:36
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "yudao.innerrocketmq")
public class InnerRocketMqProperties {

    /**
     * mq 发送给的地址
     */
    private String nameSrvSend;

    /**
     * mq的生产组
     */
    private String producerGroup;

    /**
     * 认证相关属性
     */
    private String accessKey;

    private String secretKey;

}
