package com.wkz.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 剪贴板工具类
 */
class ClipboardUtil private constructor() {
    companion object {
        /**
         * 复制文本到剪贴板
         *
         * @param text 文本
         */
        fun copyText(text: CharSequence?) {
            val clipboard =
                ContextUtil.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("text", text))
        }

        /**
         * 获取剪贴板的文本
         *
         * @return 剪贴板的文本
         */
        val text: CharSequence?
            get() {
                val clipboard =
                    ContextUtil.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
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
        fun copyUri(uri: Uri?) {
            val clipboard =
                ContextUtil.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(
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
        val uri: Uri?
            get() {
                val clipboard =
                    ContextUtil.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
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
        fun copyIntent(intent: Intent?) {
            val clipboard =
                ContextUtil.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newIntent("intent", intent))
        }

        /**
         * 获取剪贴板的意图
         *
         * @return 剪贴板的意图
         */
        val intent: Intent?
            get() {
                val clipboard =
                    ContextUtil.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                return when {
                    clip != null && clip.itemCount > 0 -> {
                        clip.getItemAt(0).intent
                    }
                    else -> {
                        null
                    }
                }
            }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}