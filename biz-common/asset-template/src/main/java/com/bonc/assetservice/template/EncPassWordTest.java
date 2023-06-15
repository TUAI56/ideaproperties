package com.bonc.assetservice.template;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

/**
 * @Description: 数据加密工具测试
 * @Author: huyang
 * @Email huyang@bonc.com.cn
 * @Date: 2022/4/7 9:03
 * @Version: V1.0
 */
public class EncPassWordTest {

    public static void main(String[] args) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        //加密算法，默认，无需修改
        config.setAlgorithm("PBEWithMD5AndDES");
        //加盐密钥，修改后需要修改 application中的密钥值
        config.setPassword("pMsmcCFSU6I0Zrzbonc");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        //config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        //config.setStringOutputType("base64");

        standardPBEStringEncryptor.setConfig(config);
        //需要加密的密码
        String plainText = "label_log";
        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);
        System.out.println(plainText+" 加密后:"+encryptedText);
        plainText = "Pub_label2022";
        encryptedText = standardPBEStringEncryptor.encrypt(plainText);
        System.out.println(plainText+" 加密后:"+encryptedText);
        System.out.println("-----------------------------------------------");







    }


}
