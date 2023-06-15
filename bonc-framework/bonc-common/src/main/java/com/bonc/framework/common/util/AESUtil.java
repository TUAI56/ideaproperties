package com.bonc.framework.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description:
 * @ClassName AESUtil
 * @Author: huyang
 * @Date: 2022/9/27 16:35
 * @version V1.0
 */

public class AESUtil {
    private static final String KEY_AES = "AES";

    /**
     * 加密操作
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("key不满足条件");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src.getBytes());
        return byte2hex(encrypted);
    }


    public static String decrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("key不满足条件");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted1 = hex2byte(src);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);
        return originalString;
    }


    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }


    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }



    public static void main(String[] args) throws Exception {
        String key="Bonc_123456789ab";
        String content = "000000";
        System.out.println("原内容 = " + content);
        String encrypt = AESUtil.encrypt(content, key);
        System.out.println("加密后 = " + encrypt);
        String decrypt = AESUtil.decrypt(encrypt, key);
        System.out.println("解密后 = " + decrypt);

        String content2 = "Sqy000000.";
        System.out.println("原内容 = " + content2);
        String encrypt2 = AESUtil.encrypt(content2, key);
        System.out.println("加密后 = " + encrypt2);
        String decrypt2 = AESUtil.decrypt(encrypt2, key);
        System.out.println("解密后 = " + decrypt2);
    }


}
