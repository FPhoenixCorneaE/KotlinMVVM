package com.fphoenixcorneae.wanandroid.application

import android.content.pm.PackageManager
import android.os.Process
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.fphoenixcorneae.framework.base.BaseApplication
import com.fphoenixcorneae.wanandroid.BuildConfig
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


class WanAndroidApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        // 初始化Night mode,设置Theme随系统变化,应用的Theme继承DayNight Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // 初始化Bugly
        initBugly()
    }

    /**
     * 初始化Bugly
     */
    private fun initBugly() {
        val context = applicationContext
        // 获取当前包名
        val packageName: String = context.packageName
        // 获取当前进程名
        val processName = getProcessName(Process.myPid())
        // 设置是否为上报进程
        val strategy = UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
        // 获取BuglyAppId
        val applicationInfo =
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val buglyAppId = applicationInfo.metaData.getString("BUGLY_APPID")
        CrashReport.initCrashReport(this, buglyAppId, BuildConfig.DEBUG, strategy)
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName: String = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }
}