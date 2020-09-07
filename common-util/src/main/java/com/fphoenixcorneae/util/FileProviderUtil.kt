package com.fphoenixcorneae.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import java.io.File

/**
 * 文件提供者工具类
 *
 * @date 2019-12-04 15:16
 */
object FileProviderUtil {

    private fun getUriForFile(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getUriForFile24(file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun getUriForFile24(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${AppUtil.packageName}.FileProvider",
            file
        )
    }

    @JvmStatic
    fun setIntentDataAndType(
        intent: Intent,
        type: String,
        file: File,
        writeAble: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getUriForFile(file), type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
    }
}