import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.Test;

/**
 * @Author: Ethan.Xing
 * @Date: 2022/10/21
 * @Description:
 */


@Slf4j
public class JasyptEncrpt {

    @Test
    public void encrypt() {
        //该类的选择根据algorithm：PBEWithMD5AndDE选择的算法选择
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("pMsmcCFSU6I0Zrzbonc");
        String encrypt = encryptor.encrypt("tcZ1fzt7cGDDII2c");
        System.out.println("用户名加密后：" + encrypt);
        String decrypt = encryptor.decrypt(encrypt);
        System.out.println("用户名解密后：" + decrypt);

        encrypt = encryptor.encrypt("Jscnivwsq76EFm2oCAKSFRGWg1xdfO");
        System.out.println("密码加密后：" + encrypt);
        decrypt = encryptor.decrypt(encrypt);
        System.out.println("密码解密后：" + decrypt);
    }

}
