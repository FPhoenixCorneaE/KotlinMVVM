package com.fphoenixcorneae.util.encryption

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * MD5加密:
 * 不可逆（Message Digest，消息摘要算法）
 */
class MD5Util private constructor() {

    companion object {
        /**
         * MD5加密
         */
        fun encryptMD5(securityStr: String): String {
            val data = securityStr.toByteArray()
            val builder = StringBuilder()
            try {
                val md5 = MessageDigest.getInstance("MD5")
                md5.update(data)
                val resultBytes = md5.digest()
                for (i in resultBytes.indices) {
                    if (Integer.toHexString(0xFF and resultBytes[i].toInt()).length == 1) {
                        builder.append("0").append(
                            Integer.toHexString(0xFF and resultBytes[i].toInt())
                        )
                    } else {
                        builder.append(Integer.toHexString(0xFF and resultBytes[i].toInt()))
                    }
                }
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return builder.toString()
        }
    }

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }
}