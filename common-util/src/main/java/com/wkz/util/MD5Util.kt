package com.wkz.util

import android.os.Build

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * MD5加密
 *
 * @author wkz
 */

class MD5Util private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        /**
         * 获取MD5串
         */
        fun getMD5(origin: String): String {
            try {
                val md5 = MessageDigest.getInstance("MD5")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    md5.update(origin.toByteArray(StandardCharsets.UTF_8))
                } else {
                    md5.update(origin.toByteArray(charset("UTF-8")))
                }
                val encryption = md5.digest()

                val strBuilder = StringBuilder()
                for (anEncryption in encryption) {
                    if (Integer.toHexString(0xff and anEncryption.toInt()).length == 1) {
                        strBuilder.append("0").append(Integer.toHexString(0xff and anEncryption.toInt()))
                    } else {
                        strBuilder.append(Integer.toHexString(0xff and anEncryption.toInt()))
                    }
                }

                return strBuilder.toString()
            } catch (e: Exception) {
                return ""
            }

        }

        /**
         * 'A' 是随机生成大写的字母
         * 'a' 是随机生成小写的字母
         */
        fun generateMD5Str(): String {
            val strBuilder = StringBuilder()
            for (i in 0..9) {
                strBuilder.append((Math.random() * 26 + 'A'.toDouble()).toChar())
            }
            strBuilder.append(System.currentTimeMillis())
            return getMD5(strBuilder.toString())
        }
    }
}
