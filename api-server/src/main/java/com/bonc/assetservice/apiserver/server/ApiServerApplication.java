package com.bonc.assetservice.apiserver.server;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
 *
 * @author 芋道源码
 */

@SpringBootApplication(scanBasePackages = {"com.bonc.module","com.bonc.assetservice.*"})
@EnableEncryptableProperties
public class ApiServerApplication {

    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        System.setProperty("jasypt.encryptor.password", "pMsmcCFSU6I0Zrzbonc");
        System.setProperty("jasypt.encryptor.pool-size", "1");
        System.setProperty("jasypt.encryptor.providerName", "SunJCE");
        System.setProperty("jasypt.encryptor.ivGeneratorClassName", "org.jasypt.iv.NoIvGenerator");
        System.setProperty("jasypt.encryptor.key-obtention-iterations", "1000");
        System.setProperty("jasypt.encryptor.string-output-type", "base64");
        SpringApplication.run(ApiServerApplication.class, args);
    }

}
