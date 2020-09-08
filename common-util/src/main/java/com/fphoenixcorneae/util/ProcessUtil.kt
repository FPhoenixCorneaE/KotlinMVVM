package com.fphoenixcorneae.util

import android.Manifest.permission
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * 进程工具类
 */
class ProcessUtil private constructor() {

    companion object {
        /**
         * Return the foreground process name.
         *
         * Target APIs greater than 21 must hold
         * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
         *
         * @return the foreground process name
         */
        val foregroundProcessName: String?
            get() {
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
                    val pm = context.packageManager
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    val list =
                        pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    loggerD(list.toString(), "ProcessUtil")
                    if (list.size <= 0) {
                        loggerD(
                            "getForegroundProcessName: noun of access to usage information.",
                            "ProcessUtil"
                        )
                        return ""
                    }
                    try {
                        // Access to usage information.
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
                            loggerD(
                                "getForegroundProcessName: refuse to device usage stats.",
                                "ProcessUtil"
                            )
                            return ""
                        }
                        val usageStatsManager = context
                            .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                        var usageStatsList: List<UsageStats>? = null
                        val endTime = System.currentTimeMillis()
                        val beginTime = endTime - 86400000 * 7
                        usageStatsList = usageStatsManager
                            .queryUsageStats(
                                UsageStatsManager.INTERVAL_BEST,
                                beginTime, endTime
                            )
                        if (usageStatsList == null || usageStatsList.isEmpty()) {
                            return ""
                        }
                        var recentStats: UsageStats? = null
                        for (usageStats in usageStatsList) {
                            if (recentStats == null
                                || usageStats.lastTimeUsed > recentStats.lastTimeUsed
                            ) {
                                recentStats = usageStats
                            }
                        }
                        return recentStats?.packageName
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }
                return ""
            }

        /**
         * Return all background processes.
         *
         * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
         *
         * @return all background processes
         */
        @get:RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        val allBackgroundProcesses: Set<String>
            get() {
                val am =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = am.runningAppProcesses
                val set: MutableSet<String> = HashSet()
                if (info != null) {
                    for (aInfo in info) {
                        Collections.addAll(set, *aInfo.pkgList)
                    }
                }
                return set
            }

        /**
         * Kill all background processes.
         *
         * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
         *
         * @return background processes were killed
         */
        @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        fun killAllBackgroundProcesses(): Set<String> {
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var info = am.runningAppProcesses
            val set: MutableSet<String> = HashSet()
            if (info == null) {
                return set
            }
            for (aInfo in info) {
                for (pkg in aInfo.pkgList) {
                    am.killBackgroundProcesses(pkg)
                    set.add(pkg)
                }
            }
            info = am.runningAppProcesses
            for (aInfo in info) {
                for (pkg in aInfo.pkgList) {
                    set.remove(pkg)
                }
            }
            return set
        }

        /**
         * Kill background processes.
         *
         * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
         *
         * @param packageName The name of the package.
         * @return `true`: success<br></br>`false`: fail
         */
        @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
        fun killBackgroundProcesses(packageName: String): Boolean {
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var info = am.runningAppProcesses
            if (info == null || info.size == 0) {
                return true
            }
            for (aInfo in info) {
                if (listOf(*aInfo.pkgList).contains(packageName)) {
                    am.killBackgroundProcesses(packageName)
                }
            }
            info = am.runningAppProcesses
            if (info == null || info.size == 0) {
                return true
            }
            for (aInfo in info) {
                if (listOf(*aInfo.pkgList).contains(packageName)) {
                    return false
                }
            }
            return true
        }

        /**
         * Return whether app running in the main process.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isMainProcess: Boolean
            get() = context.packageName == currentProcessName

        /**
         * Return the name of current process.
         *
         * @return the name of current process
         */
        val currentProcessName: String
            get() {
                var name = currentProcessNameByFile
                if (!TextUtils.isEmpty(name)) {
                    return name
                }
                name = currentProcessNameByAms
                if (!TextUtils.isEmpty(name)) {
                    return name
                }
                name = currentProcessNameByReflect
                return name
            }

        private val currentProcessNameByFile: String
            get() = try {
                val file =
                    File("/proc/" + Process.myPid() + "/" + "cmdline")
                val mBufferedReader = BufferedReader(FileReader(file))
                val processName = mBufferedReader.readLine().trim { it <= ' ' }
                mBufferedReader.close()
                processName
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }

        private val currentProcessNameByAms: String
            get() {
                val am =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = am.runningAppProcesses
                if (info == null || info.size == 0) {
                    return ""
                }
                val pid = Process.myPid()
                for (aInfo in info) {
                    if (aInfo.pid == pid) {
                        if (aInfo.processName != null) {
                            return aInfo.processName
                        }
                    }
                }
                return ""
            }

        private val currentProcessNameByReflect: String
            get() {
                var processName = ""
                try {
                    val app = context
                    val loadedApkField = app.javaClass.getField("mLoadedApk")
                    loadedApkField.isAccessible = true
                    val loadedApk = loadedApkField[app]
                    val activityThreadField =
                        loadedApk.javaClass.getDeclaredField("mActivityThread")
                    activityThreadField.isAccessible = true
                    val activityThread = activityThreadField[loadedApk]
                    val getProcessName =
                        activityThread.javaClass.getDeclaredMethod("getProcessName")
                    processName = getProcessName.invoke(activityThread) as String
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return processName
            }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}