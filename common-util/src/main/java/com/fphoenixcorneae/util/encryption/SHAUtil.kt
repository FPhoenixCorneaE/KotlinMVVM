package com.fphoenixcorneae.util.encryption

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * SHA-1 加密:
 * 不可逆（Secure Hash Algorithm，安全散列算法）
 */
class SHAUtil private constructor() {
    companion object {
        /**
         * SHA-512 加密
         */
        fun encrypt(data: ByteArray): String {
            val builder = StringBuilder()
            try {
                val sha = MessageDigest.getInstance("SHA-512")
                sha.update(data)
                val resultBytes = sha.digest()
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
        throw UnsupportedOperationException("cannot be instantiated")
    }
}