package com.fphoenixcorneae.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.fphoenixcorneae.ext.isNull
import com.fphoenixcorneae.ext.isSpace
import com.fphoenixcorneae.ext.loggerE
import com.fphoenixcorneae.ext.loggerI
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.security.auth.x500.X500Principal
import kotlin.system.exitProcess

/**
 * App 相关信息，包括版本名称、版本号、包名等等
 */
class AppUtil private constructor() {

    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }

    companion object {

        private val DEBUG_DN = X500Principal("CN=Android Debug,O=Android,C=US")

        /**
         * Get package name
         */
        val packageName: String
            get() {
                return ContextUtil.context.packageName
            }

        /**
         * Get version name
         */
        val versionName: String
            get() = getVersionName(packageName)

        /**
         * Get version code
         */
        val versionCode: Long
            get() = getVersionCode(packageName)

        /**
         * Get icon
         */
        val appIcon: Drawable?
            get() = getAppIcon(packageName)

        /**
         * Get app name
         */
        val appName: String
            get() = getAppName(packageName)

        /**
         * Get app icon
         */
        private fun getAppIcon(packageName: String): Drawable? {
            val context = ContextUtil.context
            try {
                val pm = context.packageManager
                val info = pm.getApplicationInfo(packageName, 0)
                return info.loadIcon(pm)
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
            }
            return null
        }

        /**
         * Get app version name
         */
        fun getVersionName(packageName: String): String {
            try {
                val packageInfo = ContextUtil.context.packageManager.getPackageInfo(packageName, 0)
                return packageInfo.versionName
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
            }
            return ""
        }

        /**
         * Get app version code
         */
        fun getVersionCode(packageName: String): Long {
            val context = ContextUtil.context
            try {
                val pm = context.packageManager
                val packageInfo = pm.getPackageInfo(packageName, 0)
                return when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> packageInfo.longVersionCode
                    else -> packageInfo.versionCode.toLong()
                }
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
            }
            return 0
        }

        /**
         * Get app name
         */
        fun getAppName(packageName: String): String {
            val context = ContextUtil.context
            try {
                val pm = context.packageManager
                val info = pm.getApplicationInfo(packageName, 0)
                return info.loadLabel(pm).toString()
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
            }
            return ""
        }

        /**
         * Get app permission
         */
        fun getAppPermission(packageName: String): Array<String>? {
            val context = ContextUtil.context
            try {
                val pm = context.packageManager
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                return packageInfo.requestedPermissions
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
            }
            return null
        }

        /**
         * Get app signature
         */
        fun getAppSignature(pkgName: String = packageName): String {
            val context = ContextUtil.context
            try {
                val pm = context.packageManager
                val packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES)
                return packageInfo.signatures[0].toString()
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
            }
            return ""
        }

        /**
         * Return the application's signature for SHA1 value.
         *
         * @param pkgName The name of the package.
         * @return the application's signature for SHA1 value
         */
        fun getAppSignatureSHA1(pkgName: String = packageName): String? {
            return getAppSignatureHash(pkgName, "SHA1")
        }

        /**
         * Return the application's signature for SHA256 value.
         *
         * @param pkgName The name of the package.
         * @return the application's signature for SHA256 value
         */
        fun getAppSignatureSHA256(pkgName: String = packageName): String? {
            return getAppSignatureHash(pkgName, "SHA256")
        }

        /**
         * Return the application's signature for MD5 value.
         *
         * @param pkgName The name of the package.
         * @return the application's signature for MD5 value
         */
        fun getAppSignatureMD5(pkgName: String = packageName): String? {
            return getAppSignatureHash(pkgName, "MD5")
        }

        /**
         * Return the application's user-ID.
         *
         * @param pkgName The name of the package.
         * @return the application's signature for MD5 value
         */
        fun getAppUid(pkgName: String = packageName): Int {
            try {
                val applicationInfo: ApplicationInfo =
                    ContextUtil.context.packageManager.getApplicationInfo(pkgName, 0)
                return applicationInfo.uid
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return -1
        }

        /**
         * Return the application's path.
         *
         * @param pkgName The name of the package.
         * @return the application's path
         */
        fun getAppPath(pkgName: String = packageName): String? {
            return when {
                pkgName.isSpace() -> ""
                else -> try {
                    val pm: PackageManager = ContextUtil.context.packageManager
                    val pi = pm.getPackageInfo(pkgName, 0)
                    pi?.applicationInfo?.sourceDir
                } catch (e: NameNotFoundException) {
                    e.printStackTrace()
                    ""
                }
            }
        }

        /**
         * Judge whether an app is debuggable
         */
        val isDebuggable: Boolean
            get() {
                val context = ContextUtil.context
                var debuggable = false
                try {
                    val packageInfo =
                        context.packageManager.getPackageInfo(
                            packageName,
                            PackageManager.GET_SIGNATURES
                        )
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
                    loggerE(e.toString())
                }
                return debuggable
            }

        /**
         * Return whether it is a system application.
         *
         * @param pkgName The name of the package.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isSystemApp(pkgName: String? = packageName): Boolean {
            return when {
                pkgName.isNullOrBlank() -> false
                else -> try {
                    val pm: PackageManager = ContextUtil.context.packageManager
                    val ai = pm.getApplicationInfo(pkgName, 0)
                    ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
                } catch (e: NameNotFoundException) {
                    e.printStackTrace()
                    false
                }
            }
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
                    return topActivity != null && topActivity.packageName != context.packageName
                }
                return false
            }

        /**
         * Judge whether an app is in foreground
         */
        val isAppInForeground: Boolean
            get() {
                val context = ContextUtil.context
                val am =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = am.runningAppProcesses
                if (info == null || info.size == 0) return false
                for (aInfo in info) {
                    if (aInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        if (aInfo.processName == context.packageName) {
                            return true
                        }
                    }
                }
                return false
            }

        /**
         * Return whether application is foreground.
         *
         * Target APIs greater than 21 must hold
         * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
         *
         * @param packageName The name of the package.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isAppInForeground(packageName: String): Boolean {
            return !packageName.isBlank() && packageName == getForegroundProcessName()
        }

        /**
         * Return whether application is running.
         *
         * @param pkgName The name of the package.
         * @return `true`: yes<br></br>`false`: no
         */
        @SuppressLint("NewApi")
        fun isAppRunning(pkgName: String): Boolean {
            val context = ContextUtil.context
            val uid: Int
            val packageManager: PackageManager = context.getPackageManager()
            uid = try {
                val ai = packageManager.getApplicationInfo(pkgName, 0) ?: return false
                ai.uid
            } catch (e: NameNotFoundException) {
                e.printStackTrace()
                return false
            }
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskInfo =
                am.getRunningTasks(Int.MAX_VALUE)
            if (taskInfo != null && taskInfo.size > 0) {
                for (aInfo in taskInfo) {
                    if (pkgName == aInfo.baseActivity!!.packageName) {
                        return true
                    }
                }
            }
            val serviceInfo =
                am.getRunningServices(Int.MAX_VALUE)
            if (serviceInfo != null && serviceInfo.size > 0) {
                for (aInfo in serviceInfo) {
                    if (uid == aInfo.uid) {
                        return true
                    }
                }
            }
            return false
        }

        /**
         * Launch the application.
         *
         * @param pkgName The name of the package.
         */
        fun launchApp(pkgName: String = packageName) {
            if (pkgName.isSpace()) return
            val launchAppIntent: Intent? =
                IntentUtil.getLaunchAppIntent(pkgName, true)
            if (launchAppIntent.isNull()) {
                loggerE("Launcher activity isn't exist.")
                return
            }
            ContextUtil.context.startActivity(launchAppIntent)
        }

        /**
         * Launch the application.
         *
         * @param activity    The activity.
         * @param pkgName The name of the package.
         * @param requestCode If &gt;= 0, this code will be returned in
         * onActivityResult() when the activity exits.
         */
        fun launchApp(
            activity: Activity,
            pkgName: String,
            requestCode: Int
        ) {
            if (pkgName.isSpace()) return
            val launchAppIntent: Intent? =
                IntentUtil.getLaunchAppIntent(pkgName)
            if (launchAppIntent.isNull()) {
                loggerE("Launcher activity isn't exist.")
                return
            }
            activity.startActivityForResult(launchAppIntent, requestCode)
        }

        /**
         * Relaunch the application.
         *
         * @param isKillProcess True to kill the process, false otherwise.
         */
        fun relaunchApp(isKillProcess: Boolean = true) {
            val intent: Intent? = IntentUtil.getLaunchAppIntent(
                packageName,
                true
            )
            if (intent.isNull()) {
                loggerE("Launcher activity isn't exist.")
                return
            }
            intent!!.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            ContextUtil.context.startActivity(intent)
            if (!isKillProcess) return
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }

        /**
         * Exit the application.
         */
        fun exitApp() {
            val activityList = ContextUtil.getActivityList()
            for (i in activityList.indices.reversed()) {
                // remove from top
                val activity = activityList[i]
                // sActivityList remove the index activity at onActivityDestroyed
                activity.finish()
            }
            exitProcess(0)
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
        fun getDeviceUsableMemory(): Int {
            val context = ContextUtil.context
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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
            get() = Build.VERSION.SDK_INT

        /**
         * 获取应用签名
         * @return 返回应用的签名
         */
        @SuppressLint("PackageManagerGetSignatures")
        fun getSign(): String {
            return try {
                @SuppressLint("PackageManagerGetSignatures")
                val pis = ContextUtil.context.packageManager
                    .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                hexDigest(pis.signatures[0].toByteArray())
            } catch (e: NameNotFoundException) {
                loggerE(e.toString())
                ""
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
                loggerE(e.toString())
            }
            return ""
        }

        private fun getAppSignatureHash(
            packageName: String,
            algorithm: String
        ): String? {
            if (packageName.isSpace()) return ""
            val signature = getAppSignature(packageName)
            return when {
                signature.isEmpty() -> ""
                else -> ConvertUtil.bytes2HexString(
                    hashTemplate(
                        signature.toByteArray(),
                        algorithm
                    )
                )
                    .replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
            }
        }

        private fun hashTemplate(
            data: ByteArray?,
            algorithm: String
        ): ByteArray? {
            return when {
                data.isNull() || data!!.isEmpty() -> null
                else -> try {
                    val md =
                        MessageDigest.getInstance(algorithm)
                    md.update(data)
                    md.digest()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                    null
                }
            }
        }

        private fun getForegroundProcessName(): String? {
            val context = ContextUtil.context
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val pInfo = am.runningAppProcesses
            if (pInfo != null && pInfo.size > 0) {
                for (aInfo in pInfo) {
                    if (aInfo.importance
                        == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
                        return aInfo.processName
                    }
                }
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                val pm: PackageManager = context.packageManager
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                val list =
                    pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                loggerI(list.toString())
                if (list.size <= 0) {
                    loggerI("getForegroundProcessName: noun of access to usage information.")
                    return ""
                }
                try { // Access to usage information.
                    val info =
                        pm.getApplicationInfo(context.packageName, 0)
                    val aom =
                        context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    if (aom.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                    if (aom.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        loggerI("getForegroundProcessName: refuse to device usage stats.")
                        return ""
                    }
                    val usageStatsManager =
                        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    val usageStatsList = usageStatsManager
                        .queryUsageStats(
                            UsageStatsManager.INTERVAL_BEST,
                            beginTime, endTime
                        )
                    if (usageStatsList == null || usageStatsList.isEmpty()) return null
                    var recentStats: UsageStats? = null
                    for (usageStats in usageStatsList) {
                        if (recentStats == null
                            || usageStats.lastTimeUsed > recentStats.lastTimeUsed
                        ) {
                            recentStats = usageStats
                        }
                    }
                    return recentStats?.packageName
                } catch (e: NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            return ""
        }

        /**
         * Register the status of application changed listener.
         *
         * @param listener The status of application changed listener
         */
        fun registerAppStatusChangedListener(listener: OnAppStatusChangedListener?) {
            ContextUtil.getActivityLifecycle().addOnAppStatusChangedListener(listener)
        }

        /**
         * Unregister the status of application changed listener.
         *
         * @param listener The status of application changed listener
         */
        fun unregisterAppStatusChangedListener(listener: OnAppStatusChangedListener?) {
            ContextUtil.getActivityLifecycle().removeOnAppStatusChangedListener(listener)
        }
    }
}
