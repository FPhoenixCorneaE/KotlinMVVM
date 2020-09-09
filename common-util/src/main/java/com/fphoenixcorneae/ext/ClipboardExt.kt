package com.fphoenixcorneae.ext

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fphoenixcorneae.util.ContextUtil

/**
 * 复制文本到剪贴板
 *
 * @param text 文本
 */
fun Context.copyText(text: CharSequence?) {
    val clipboard = clipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("text", text))
}

/**
 * 获取剪贴板的文本
 *
 * @return 剪贴板的文本
 */
val clipboardText: CharSequence?
    get() {
        val clipboard = ContextUtil.context.clipboardManager
        val clip = clipboard?.primaryClip
        return when {
            clip != null && clip.itemCount > 0 -> {
                clip.getItemAt(0).coerceToText(ContextUtil.context)
            }
            else -> {
                null
            }
        }
    }

/**
 * 复制uri到剪贴板
 *
 * @param uri uri
 */
fun Context.copyUri(uri: Uri?) {
    val clipboard = clipboardManager
    clipboard?.setPrimaryClip(
        ClipData.newUri(
            ContextUtil.context.contentResolver,
            "uri",
            uri
        )
    )
}

/**
 * 获取剪贴板的uri
 *
 * @return 剪贴板的uri
 */
val clipboardUri: Uri?
    get() {
        val clipboard = ContextUtil.context.clipboardManager
        val clip = clipboard?.primaryClip
        return when {
            clip != null && clip.itemCount > 0 -> {
                clip.getItemAt(0).uri
            }
            else -> {
                null
            }
        }
    }

/**
 * 复制意图到剪贴板
 *
 * @param intent 意图
 */
fun Context.copyIntent(intent: Intent?) {
    val clipboard = clipboardManager
    clipboard?.setPrimaryClip(ClipData.newIntent("intent", intent))
}

/**
 * 获取剪贴板的意图
 *
 * @return 剪贴板的意图
 */
val clipboardIntent: Intent?
    get() {
        val clipboard = ContextUtil.context.clipboardManager
        val clip = clipboard?.primaryClip
        return when {
            clip != null && clip.itemCount > 0 -> {
                clip.getItemAt(0).intent
            }
            else -> {
                null
            }
        }
    }