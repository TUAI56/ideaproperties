package com.bonc.assetservice.apiserver.consumer;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 * @author lws
 */

@SpringBootApplication(scanBasePackages = {"com.bonc.module","com.bonc.assetservice.*"})
@EnableEncryptableProperties
public class ConsumerApplication {

    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        System.setProperty("jasypt.encryptor.password", "pMsmcCFSU6I0Zrzbonc");
        System.setProperty("jasypt.encryptor.pool-size", "1");
        System.setProperty("jasypt.encryptor.providerName", "SunJCE");
        System.setProperty("jasypt.encryptor.ivGeneratorClassName", "org.jasypt.iv.NoIvGenerator");
        System.setProperty("jasypt.encryptor.key-obtention-iterations", "1000");
        System.setProperty("jasypt.encryptor.string-output-type", "base64");
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
