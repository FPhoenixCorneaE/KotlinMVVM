package com.fphoenixcorneae.util.encryption

import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * 3DES对称加密:
 * Triple DES、DESede，进行了三重DES加密的算法，对称加密算法
 */
class TripleDESUtil private constructor() {
    companion object {
        /**
         * 生成密钥
         */
        @Throws(NoSuchAlgorithmException::class)
        fun initKey(): ByteArray {
            val keyGen = KeyGenerator.getInstance("DESede")
            //112 168
            keyGen.init(168)
            val secretKey = keyGen.generateKey()
            return secretKey.encoded
        }

        /**
         * 3DES 加密
         */
        @Throws(Exception::class)
        fun encrypt(data: ByteArray, key: ByteArray? = null): ByteArray {
            val secretKey: SecretKey = SecretKeySpec(key ?: initKey(), "DESede")
            val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        }

        /**
         * 3DES 加密
         */
        @Throws(Exception::class)
        fun encrypt(data: String, key: ByteArray? = null): String {
            return encrypt(data.toByteArray(), key).toString()
        }

        /**
         * 3DES 解密
         */
        @Throws(Exception::class)
        fun decrypt(data: ByteArray, key: ByteArray? = null): ByteArray {
            val secretKey: SecretKey = SecretKeySpec(key ?: initKey(), "DESede")
            val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(data)
        }

        /**
         * 3DES 解密
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