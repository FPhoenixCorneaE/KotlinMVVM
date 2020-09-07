package com.fphoenixcorneae.util

import android.content.Intent
import android.net.Uri
import com.fphoenixcorneae.annotation.FileType
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import java.io.File
import java.util.*

/**
 * 打开文件工具类
 *
 * @date 2019-12-04 14:58
 */
object OpenFileUtil {

    /**
     * 打开文件
     *
     * @param file
     */
    @JvmStatic
    fun openFile(file: File) {
        if (!file.exists()) {
            return
        }
        // 取得文件扩展名
        val end =
            file.name.substring(file.name.lastIndexOf(".") + 1, file.name.length)
                .toLowerCase(Locale.getDefault())
        when (end) {
            "3gp", "mp4" -> openVideoFileIntent(file, FileType.DATA_TYPE_VIDEO)
            "m4a", "mp3", "mid", "xmf", "ogg", "wav" -> openAudioFileIntent(
                file,
                FileType.DATA_TYPE_AUDIO
            )
            "doc", "docx" -> commonOpenFileWithType(file, FileType.DATA_TYPE_WORD)
            "xls", "xlsx" -> commonOpenFileWithType(file, FileType.DATA_TYPE_EXCEL)
            "jpg", "gif", "png", "jpeg", "bmp" -> commonOpenFileWithType(
                file,
                FileType.DATA_TYPE_IMAGE
            )
            "txt" -> commonOpenFileWithType(file, FileType.DATA_TYPE_TXT)
            "htm", "html" -> commonOpenFileWithType(file, FileType.DATA_TYPE_HTML)
            "apk" -> commonOpenFileWithType(file, FileType.DATA_TYPE_APK)
            "ppt" -> commonOpenFileWithType(file, FileType.DATA_TYPE_PPT)
            "pdf" -> commonOpenFileWithType(file, FileType.DATA_TYPE_PDF)
            "chm" -> commonOpenFileWithType(file, FileType.DATA_TYPE_CHM)
            else -> commonOpenFileWithType(file, FileType.DATA_TYPE_ALL)
        }
    }

    /**
     * 打开文件夹目录
     * @param file 文件
     * @param type 文件类型
     */
    @JvmStatic
    fun openFileDirectory(file: File, @FileType type: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(Uri.parse(file.parent), type)
        context.startActivity(intent)
    }

    /**
     * Android传入type打开文件
     *
     * @param file
     * @param type
     */
    private fun commonOpenFileWithType(file: File, @FileType type: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        FileProviderUtil.setIntentDataAndType(intent, type, file, true)
        context.startActivity(intent)
    }

    /**
     * Android打开Video文件
     *
     * @param file
     */
    private fun openVideoFileIntent(file: File, @FileType type: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        FileProviderUtil.setIntentDataAndType(intent, type, file, false)
        context.startActivity(intent)
    }

    /**
     * Android打开Audio文件
     *
     * @param file
     */
    private fun openAudioFileIntent(file: File, @FileType type: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        FileProviderUtil.setIntentDataAndType(intent, type, file, false)
        context.startActivity(intent)
    }
}