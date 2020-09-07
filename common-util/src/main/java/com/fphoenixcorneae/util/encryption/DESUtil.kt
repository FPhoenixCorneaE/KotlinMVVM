package com.fphoenixcorneae.util.encryption

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * DES对称加密:
 * Data Encryption Standard，数据加密标准，对称加密算法
 */
class DESUtil private constructor() {
    companion object {
        /**
         * 生成密钥
         */
        @Throws(Exception::class)
        fun initKey(): ByteArray {
            val keyGen = KeyGenerator.getInstance("DES")
            keyGen.init(56)
            val secretKey = keyGen.generateKey()
            return secretKey.encoded
        }

        /**
         * DES 加密
         */
        @Throws(Exception::class)
        fun encrypt(data: ByteArray, key: ByteArray? = null): ByteArray {
            val secretKey: SecretKey = SecretKeySpec(key ?: initKey(), "DES")
            val cipher =
                Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        }

        /**
         * DES 加密
         */
        @Throws(Exception::class)
        fun encrypt(data: String, key: ByteArray? = null): String {
            return encrypt(data.toByteArray(), key).toString()
        }

        /**
         * DES 解密
         */
        @Throws(Exception::class)
        fun decrypt(data: ByteArray, key: ByteArray? = null): ByteArray {
            val secretKey: SecretKey = SecretKeySpec(key ?: initKey(), "DES")
            val cipher =
                Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        }

        /**
         * DES 解密
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