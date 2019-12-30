package com.wkz.extension

import android.text.TextUtils
import com.wkz.util.CloseUtil
import com.wkz.util.ContextUtil
import com.wkz.util.SizeUtil
import com.wkz.util.ToastUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

fun Any?.isNull(): Boolean {
    return this == null
}

fun Any?.isNonNull(): Boolean {
    return this != null
}

fun CharSequence?.equals(charSequence: CharSequence?): Boolean {
    return TextUtils.equals(this, charSequence)
}

fun showToast(content: CharSequence) {
    ToastUtil.showShort(content)
}

fun dp2px(dipValue: Float): Int {
    return SizeUtil.dp2px(dipValue)
}

fun px2dp(pxValue: Float): Int {
    return SizeUtil.px2dp(pxValue)
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


