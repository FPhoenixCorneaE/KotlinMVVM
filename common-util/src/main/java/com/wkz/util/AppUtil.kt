package com.wkz.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.drawable.Drawable
import android.os.Build
import com.orhanobut.logger.Logger
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.security.auth.x500.X500Principal

/**
 * App 相关信息，包括版本名称、版本号、包名等等
 *
 * @author wkz
 */
class AppUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        private val DEBUG_DN = X500Principal("CN=Android Debug,O=Android,C=US")

        /**
         * Get version name
         */
        val versionName: String
            get() {
                val context = ContextUtil.context
                val info: PackageInfo
                try {
                    info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    return info.versionName
                } catch (e: NameNotFoundException) {
                    Logger.e(e.toString())
                }

                return ""
            }

        /**
         * Get version code
         */
        val versionCode: Long
            get() {
                val context = ContextUtil.context
                val info: PackageInfo
                try {
                    info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        info.longVersionCode
                    } else {
                        info.versionCode.toLong()
                    }
                } catch (e: NameNotFoundException) {
                    Logger.e(e.toString())
                }

                return 0
            }

        /**
         * Get package name
         */
        val packageName: String
            get() {
                val context = ContextUtil.context
                return context.getPackageName()
            }

        /**
         * Get icon
         */
        val appIcon: Drawable?
            get() = getAppIcon(packageName)

        /**
         * Get app icon
         */
        fun getAppIcon(packageName: String): Drawable? {
            val context = ContextUtil.context
            try {
                val pm = context.getPackageManager()
                val info = pm.getApplicationInfo(packageName, 0)
                return info.loadIcon(pm)
            } catch (e: NameNotFoundException) {
                Logger.e(e.toString())
            }

            return null
        }

        /**
         * Get app version name
         */
        fun getVersionName(packageName: String): String? {
            val context = ContextUtil.context
            try {
                val pm = context.getPackageManager()
                val packageInfo = pm.getPackageInfo(packageName, 0)
                return packageInfo.versionName
            } catch (e: NameNotFoundException) {
                Logger.e(e.toString())
            }

            return null
        }

        /**
         * Get app version code
         */
        fun getVersionCode(packageName: String): Long {
            val context = ContextUtil.context
            try {
                val pm = context.getPackageManager()
                val packageInfo = pm.getPackageInfo(packageName, 0)
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.getLongVersionCode()
                } else {
                    packageInfo.versionCode.toLong()
                }
            } catch (e: NameNotFoundException) {
                Logger.e(e.toString())
            }

            return -1
        }

        /**
         * Get app name
         */
        fun getAppName(packageName: String): String? {
            val context = ContextUtil.context
            try {
                val pm = context.getPackageManager()
                val info = pm.getApplicationInfo(packageName, 0)
                return info.loadLabel(pm).toString()
            } catch (e: NameNotFoundException) {
                Logger.e(e.toString())
            }

            return null
        }

        /**
         * Get app name
         */
        val appName: String?
            get() {
                val context = ContextUtil.context
                try {
                    val pm = context.getPackageManager()
                    val info = pm.getApplicationInfo(packageName, 0)
                    return info.loadLabel(pm).toString()
                } catch (e: NameNotFoundException) {
                    Logger.e(e.toString())
                }

                return null
            }

        /**
         * Get app permission
         */
        fun getAppPermission(packageName: String): Array<String>? {
            val context = ContextUtil.context
            try {
                val pm = context.getPackageManager()
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                return packageInfo.requestedPermissions
            } catch (e: NameNotFoundException) {
                Logger.e(e.toString())
            }

            return null
        }

        /**
         * Get app signature
         */
        fun getAppSignature(packageName: String): String? {
            val context = ContextUtil.context
            try {
                val pm = context.getPackageManager()
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                return packageInfo.signatures[0].toCharsString()
            } catch (e: NameNotFoundException) {
                Logger.e(e.toString())
            }

            return null
        }

        /**
         * Judge whether an app is dubuggable
         */
        val isDebuggable: Boolean
            get() {
                val context = ContextUtil.context
                var debuggable = false
                try {
                    val packageInfo =
                        context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                    val signatures = packageInfo.signatures
                    for (signature in signatures) {
                        val cf = CertificateFactory.getInstance("X.509")
                        val stream = ByteArrayInputStream(signature.toByteArray())
                        val cert = cf
                            .generateCertificate(stream) as X509Certificate
                        debuggable = cert.subjectX500Principal == DEBUG_DN
                        if (debuggable) {
                            break
                        }
                    }

                } catch (e: Exception) {
                    Logger.e(e.toString())
                }

                return debuggable
            }

        /**
         * Judge whether an app is in background
         */
        val isAppInBackground: Boolean
            @TargetApi(Build.VERSION_CODES.Q)
            get() {
                val context = ContextUtil.context
                val am = context
                    .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val taskList: List<ActivityManager.RunningTaskInfo>?
                taskList = am.getRunningTasks(1)
                if (taskList != null && taskList.isNotEmpty()) {
                    val topActivity = taskList[0].topActivity
                    return topActivity != null && topActivity.packageName != context.getPackageName()
                }
                return false
            }

        /**
         * 获取应用运行的最大内存
         *
         * @return 最大内存
         */
        val maxMemory: Long
            get() = Runtime.getRuntime().maxMemory() / 1024

        /**
         * 获取设备的可用内存大小
         *
         * @param context 应用上下文对象context
         * @return 当前内存大小
         */
        fun getDeviceUsableMemory(context: Context): Int {
            val am = context.getSystemService(
                Context.ACTIVITY_SERVICE
            ) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            // 返回当前系统的可用内存
            return (mi.availMem / (1024 * 1024)).toInt()
        }


        fun getMobileModel(): String {
            var model: String? = Build.MODEL
            model = model?.trim { it <= ' ' } ?: ""
            return model
        }

        /**
         * 获取手机系统SDK版本
         *
         * @return 如API 17 则返回 17
         */
        val sdkVersion: Int
            get() = android.os.Build.VERSION.SDK_INT

        /**
         * 获取应用签名
         *
         * @param context 上下文
         * @param pkgName 包名
         * @return 返回应用的签名
         */
        @SuppressLint("PackageManagerGetSignatures")
        fun getSign(context: Context, pkgName: String): String? {
            return try {
                @SuppressLint("PackageManagerGetSignatures") val pis = context.packageManager
                    .getPackageInfo(pkgName, PackageManager.GET_SIGNATURES)
                hexDigest(pis.signatures[0].toByteArray())
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }

        }

        /**
         * 将签名字符串转换成需要的32位签名
         *
         * @param paramArrayOfByte 签名byte数组
         * @return 32位签名字符串
         */
        private fun hexDigest(paramArrayOfByte: ByteArray): String {
            val hexDigits = charArrayOf(
                48.toChar(),
                49.toChar(),
                50.toChar(),
                51.toChar(),
                52.toChar(),
                53.toChar(),
                54.toChar(),
                55.toChar(),
                56.toChar(),
                57.toChar(),
                97.toChar(),
                98.toChar(),
                99.toChar(),
                100.toChar(),
                101.toChar(),
                102.toChar()
            )
            try {
                val localMessageDigest = MessageDigest.getInstance("MD5")
                localMessageDigest.update(paramArrayOfByte)
                val arrayOfByte = localMessageDigest.digest()
                val arrayOfChar = CharArray(32)
                var i = 0
                var j = 0
                while (true) {
                    if (i >= 16) {
                        return String(arrayOfChar)
                    }
                    val k = arrayOfByte[i].toInt()
                    arrayOfChar[j] = hexDigits[0xF and k.ushr(4)]
                    arrayOfChar[++j] = hexDigits[k and 0xF]
                    i++
                    j++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }
    }
}
