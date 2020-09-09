package com.fphoenixcorneae.ext

import android.text.TextUtils
import com.fphoenixcorneae.util.CloseUtil
import com.fphoenixcorneae.util.ContextUtil
import com.fphoenixcorneae.util.toast.ToastUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * 判断任意一个字符串是否为空
 */
fun String?.isSpace(): Boolean {
    if (this.isNull()) {
        return true
    }
    var i = 0
    val len = this!!.length
    while (i < len) {
        if (!Character.isWhitespace(this[i])) {
            return false
        }
        ++i
    }
    return true
}

/**
 * 判断任意一个对象是否为null
 */
fun Any?.isNull(): Boolean {
    return this == null
}

/**
 * 判断任意一个对象是否为非null
 */
fun Any?.isNonNull(): Boolean {
    return this != null
}

fun <T> Collection<T>?.isNonNullAndNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

/**
 * 字符序列比较
 */
fun CharSequence?.equals(charSequence: CharSequence?): Boolean {
    return TextUtils.equals(this, charSequence)
}

fun showToast(content: CharSequence?) {
    ToastUtil.show(content)
}

/**
 * 读取assets文件夹下文件
 * @param fileName 文件名称
 * @return Json String
 */
fun readFileFromAssets(fileName: String): String {
    val stringBuilder = StringBuilder()
    //获得assets资源管理器
    //使用IO流读取json文件内容
    var bufferedReader: BufferedReader? = null
    try {
        val assetManager = ContextUtil.context.assets
        bufferedReader = BufferedReader(
            InputStreamReader(assetManager.open(fileName), Charset.defaultCharset())
        )
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        CloseUtil.closeIOQuietly(bufferedReader)
        return stringBuilder.toString()
    }
}


