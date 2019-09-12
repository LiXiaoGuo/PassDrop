package com.linxiao.framework.util;

import android.util.Base64;

import com.blankj.utilcode.util.SPUtils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * todo 该文件暂时不删除，加解密这块应该放在so文件中实现
 */
public class RSAUtils {
    private static String KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
    /**************************** RSA 公钥加密解密**************************************/
    /**
     * 从字符串中加载公钥,从服务端获取
     *
     * @param pubKey
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.decode(pubKey,Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥加密过程
     *
     * @param plainData
     *            明文数据
     * @return
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encryptByPublicKey(PublicKey publicKey,String plainData) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("encrypt PublicKey is null !");
        }


        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(plainData.getBytes("utf-8"));
        return Base64.encodeToString(output, Base64.DEFAULT);
    }

    /**
     * 公钥解密过程
     *
     * @param encryedData
     *            明文数据
     * @return
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String decryptByPublicKey(PublicKey publicKey,String encryedData) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("encrypt PublicKey is null !");
        }

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 此处如果写成"RSA"解析的数据前多出来些乱码
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(Base64.decode(encryedData, Base64.DEFAULT));
        return new String(output);
    }

    /**
     * 公钥解密过程
     *
     * @param encryedData
     *            明文数据
     * @return
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static byte[] decryptByteArrayByPublicKey(PublicKey publicKey,String encryedData) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("encrypt PublicKey is null !");
        }

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 此处如果写成"RSA"解析的数据前多出来些乱码
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(Base64.decode(encryedData, Base64.DEFAULT));
        return output;
    }

    /**************************** RSA 公钥加密解密**************************************/



    /**
     * 从字符串中加载私钥
     *
     * @param priKey
     *            私钥数据字符串
     * @throws Exception
     *             加载私钥时产生的异常
     */
    public static PrivateKey loadPrivateKey(String priKey) {
        try {
            byte[] buffer = Base64.decode(priKey,Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA","BC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 私钥加密过程
     *
     * @param plainData
     *            明文数据
     * @return
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encryptByPrivateKey(PrivateKey privateKey,String plainData) throws Exception {
        if (privateKey == null) {
            throw new NullPointerException("encrypt PublicKey is null !");
        }

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析

        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(plainData.getBytes("utf-8"));
        return Base64.encodeToString(output, Base64.DEFAULT);
    }

    /**
     * 私钥解密过程
     *
     * @param encryedData
     *            明文数据
     * @return
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String decryptByPrivateKey(PrivateKey privateKey,String encryedData) throws Exception {
        if (privateKey == null) {
            throw new NullPointerException("decrypt PublicKey is null !");
        }

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);// 此处如果写成"RSA"解析的数据前多出来些乱码
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(Base64.decode(encryedData, Base64.DEFAULT));
        return new String(output);
    }

}
