package com.wkz.util.encryption

import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * AES对称加密:
 * Advanced Encryption Standard，高级数据加密标准，AES算法可以有效抵制针对DES的攻击算法，对称加密算法
 */
class AESUtil private constructor() {
    companion object {
        /**
         * 生成密钥
         */
        @Throws(NoSuchAlgorithmException::class)
        fun initKey(): ByteArray {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256) //192 256
            val secretKey = keyGen.generateKey()
            return secretKey.encoded
        }

        /**
         * AES 加密
         */
        @Throws(Exception::class)
        fun encrypt(data: ByteArray, key: ByteArray? = null): ByteArray {
            val secretKey: SecretKey = SecretKeySpec(key ?: initKey(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        }

        /**
         * AES 加密
         */
        @Throws(Exception::class)
        fun encrypt(data: String, key: ByteArray? = null): String {
            return encrypt(data.toByteArray(), key).toString()
        }

        /**
         * AES 解密
         */
        @Throws(Exception::class)
        fun decrypt(data: ByteArray, key: ByteArray? = null): ByteArray {
            val secretKey: SecretKey = SecretKeySpec(key ?: initKey(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        }

        /**
         * AES 解密
         */
        @Throws(Exception::class)
        fun decrypt(data: String, key: ByteArray? = null): String {
            return decrypt(data.toByteArray(), key).toString()
        }
    }

    init {
        throw UnsupportedOperationException("cannot be instantiated")
    }
}