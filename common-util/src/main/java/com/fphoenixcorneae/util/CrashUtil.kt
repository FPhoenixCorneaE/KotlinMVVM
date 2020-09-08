package com.fphoenixcorneae.util

import android.Manifest.permission
import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresPermission
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import java.io.*
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * 崩溃工具类
 */
class CrashUtil private constructor() {
    companion object {
        private var defaultDir: String? = null
        private var dir: String? = null
        private var versionName: String? = null
        private var versionCode: Long = 0
        private val FILE_SEP = System.getProperty("file.separator")

        @SuppressLint("SimpleDateFormat")
        private val FORMAT: Format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private var DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler? =
            null
        private var UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler? = null
        private var sOnCrashListener: OnCrashListener? = null

        /**
         * Initialization.
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         */
        @SuppressLint("MissingPermission")
        fun init() {
            init("")
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDir The directory of saving crash information.
         */
        @RequiresPermission(permission.WRITE_EXTERNAL_STORAGE)
        fun init(crashDir: File) {
            init(crashDir.absolutePath, null)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDirPath The directory's path of saving crash information.
         */
        @RequiresPermission(permission.WRITE_EXTERNAL_STORAGE)
        fun init(crashDirPath: String) {
            init(crashDirPath, null)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param onCrashListener The crash listener.
         */
        @SuppressLint("MissingPermission")
        fun init(onCrashListener: OnCrashListener?) {
            init("", onCrashListener)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDir        The directory of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(permission.WRITE_EXTERNAL_STORAGE)
        fun init(crashDir: File, onCrashListener: OnCrashListener?) {
            init(crashDir.absolutePath, onCrashListener)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDirPath    The directory's path of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(permission.WRITE_EXTERNAL_STORAGE)
        fun init(crashDirPath: String, onCrashListener: OnCrashListener?) {
            dir = when {
                isSpace(crashDirPath) -> {
                    null
                }
                else -> {
                    if (crashDirPath.endsWith(FILE_SEP!!)) crashDirPath else crashDirPath + FILE_SEP
                }
            }
            defaultDir =
                when {
                    Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                            && context.externalCacheDir != null -> {
                        context.externalCacheDir.toString() + FILE_SEP + "crash" + FILE_SEP
                    }
                    else -> {
                        context.cacheDir.toString() + FILE_SEP + "crash" + FILE_SEP
                    }
                }
            sOnCrashListener = onCrashListener
            Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER)
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////
        private fun input2File(input: String, filePath: String) {
            val submit =
                Executors.newSingleThreadExecutor()
                    .submit(Callable {
                        var bw: BufferedWriter? = null
                        try {
                            bw = BufferedWriter(
                                OutputStreamWriter(
                                    FileOutputStream(
                                        filePath,
                                        true
                                    ), "UTF-8"
                                )
                            )
                            bw.write(input)
                            return@Callable true
                        } catch (e: IOException) {
                            e.printStackTrace()
                            return@Callable false
                        } finally {
                            CloseUtil.closeIOQuietly(bw)
                        }
                    })
            try {
                if (submit.get()) {
                    return
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
            loggerD("write crash info to $filePath failed!", "CrashUtil")
        }

        private fun createOrExistsFile(filePath: String): Boolean {
            val file = File(filePath)
            if (file.exists()) {
                return file.isFile
            }
            return when {
                !createOrExistsDir(file.parentFile) -> {
                    false
                }
                else -> {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        false
                    }
                }
            }
        }

        private fun createOrExistsDir(file: File?): Boolean {
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) {
                return true
            }
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        init {
            versionName = AppUtil.versionName
            versionCode = AppUtil.versionCode
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER =
                Thread.getDefaultUncaughtExceptionHandler()
            UNCAUGHT_EXCEPTION_HANDLER =
                Thread.UncaughtExceptionHandler { t, e ->
                    val time =
                        FORMAT.format(Date(System.currentTimeMillis()))
                    val sb = StringBuilder()
                    val head = "************* Log Head ****************" +
                            "\nTime Of Crash      : " + time +
                            "\nDevice Manufacturer: " + Build.MANUFACTURER +
                            "\nDevice Model       : " + Build.MODEL +
                            "\nAndroid Version    : " + Build.VERSION.RELEASE +
                            "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                            "\nApp VersionName    : " + versionName +
                            "\nApp VersionCode    : " + versionCode +
                            "\n************* Log Head ****************\n\n"
                    sb.append(head)
                        .append(ThrowableUtil.getFullStackTrace(e))
                    val crashInfo = sb.toString()
                    val fullPath =
                        (if (dir == null) defaultDir else dir) + time + ".txt"
                    if (createOrExistsFile(fullPath)) {
                        input2File(crashInfo, fullPath)
                    } else {
                        loggerD("create $fullPath failed!", "CrashUtil")
                    }
                    if (sOnCrashListener != null) {
                        sOnCrashListener!!.onCrash(crashInfo, e)
                    }
                    DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(
                        t,
                        e
                    )
                }
        }
    }


    interface OnCrashListener {
        fun onCrash(crashInfo: String, e: Throwable?)
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}