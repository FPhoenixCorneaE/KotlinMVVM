package com.wkz.util

import android.util.Base64
import com.orhanobut.logger.Logger
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * @author wkz
 * @desc: 图片、Base64字符集转换工具类
 * @date 2019/4/23 16:44
 */
object ImgBase64Util {

    /**
     * 将图片转换成Base64编码的字符串
     *
     * @param path 文件路径
     * @return base64编码的字符串
     */
    @JvmStatic
    fun imageToBase64(path: String): String {
        if (path.isBlank()) {
            return ""
        }
        var `is`: InputStream? = null
        val data: ByteArray?
        var result: String
        try {
            `is` = FileInputStream(path)
            //创建一个字符流大小的数组。
            data = ByteArray(`is`.available())
            //写入数组
            `is`.read(data)
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: IOException) {
            Logger.e(e.toString())
            result = ""
        } catch (e: FileNotFoundException) {
            Logger.e(e.toString())
            result = ""
        } finally {
            if (null != `is`) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    Logger.e(e.toString())
                }
            }
        }
        return result
    }

    /**
     * base64编码字符集转化成字节数组。
     *
     * @param base64Str base64编码字符集
     * @return 是否成功
     */
    @JvmStatic
    fun base64ToByteArray(base64Str: String): ByteArray {
        return Base64.decode(base64Str, Base64.DEFAULT)
    }
}
