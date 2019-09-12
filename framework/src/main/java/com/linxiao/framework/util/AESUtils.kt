package com.linxiao.framework.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Hex
import java.security.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 *
 * Created by Extends on 2017/10/24 20:00
 */
object AESUtils {
    // 算法名称
    private val KEY_ALGORITHM = "AES"
    // 加解密算法/模式/填充方式
    private val algorithmStr = "AES/CBC/PKCS7Padding"
    private val SHA1PRNG = "SHA1PRNG"//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
    private var key: Key? = null
    private var cipher: Cipher? = null

    private fun init(keyByte: ByteArray) {
        var keyBytes = keyByte

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        val base = 16
        if (keyBytes.size % base != 0) {
            val groups = keyBytes.size / base + if (keyBytes.size % base != 0) 1 else 0
            val temp = ByteArray(groups * base)
            Arrays.fill(temp, 0.toByte())
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.size)
            keyBytes = temp
        }
        // 初始化
        Security.addProvider(BouncyCastleProvider())
        // 转化成JAVA的密钥格式
        key = SecretKeySpec(keyBytes, KEY_ALGORITHM)
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }

    }

    /**
     * 加密方法
     *
     * @param content
     * 要加密的字符串
     * @param keyBytes
     * 加密密钥
     * @return
     */
    fun encrypt(content: ByteArray, keyBytes: ByteArray,iv:ByteArray="0000000000000000".toByteArray()): ByteArray? {
        var encryptedText: ByteArray? = null
        init(keyBytes)
        try {
            cipher!!.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
            encryptedText = cipher!!.doFinal(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return encryptedText
    }

    /**
     * 解密方法
     *
     * @param encryptedData
     * 要解密的字符串
     * @param keyBytes
     * 解密密钥
     * @return
     */
    fun decrypt(encryptedData: ByteArray, keyBytes: ByteArray,iv:ByteArray="0000000000000000".toByteArray()): ByteArray? {
        var encryptedText: ByteArray? = null
        init(keyBytes)
        try {
            cipher!!.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            encryptedText = cipher!!.doFinal(encryptedData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return encryptedText
    }

    /**
     * 生成随机数，可以当做动态的密钥 加密和解密的密钥必须一致，不然将不能解密
     */
    fun generateKey(): String {
        val localSecureRandom = SecureRandom.getInstance(SHA1PRNG)
        val bytes_key = ByteArray(16)
        localSecureRandom.nextBytes(bytes_key)
        return String(Hex.encode(bytes_key))
    }
}