package com.fphoenixcorneae.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.fphoenixcorneae.ext.loggerE

/**
 * Activity工具类
 */
class ActivityUtil private constructor() {

    companion object {
        val activityList: MutableList<Activity>
            get() = ContextUtil.getActivityList()

        val launcherActivity: String
            get() = getLauncherActivity(AppUtil.packageName)

        val topActivity: Activity?
            get() {
                if (activityList.isNotEmpty()) {
                    for (i in activityList.indices.reversed()) {
                        val activity = activityList[i]
                        if (activity.isFinishing || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed)
                        ) {
                            continue
                        }
                        return activity
                    }
                }
                val topActivityByReflect = topActivityByReflect
                topActivityByReflect?.let { setTopActivity(it) }
                return topActivityByReflect
            }

        fun setTopActivity(activity: Activity) {
            if (activityList.contains(activity)) {
                if (activityList.last() != activity) {
                    activityList.remove(activity)
                    activityList.add(activity)
                }
            } else {
                activityList.add(activity)
            }
        }

        fun getLauncherActivity(pkg: String): String {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(pkg)
            val pm: PackageManager = ContextUtil.context.packageManager
            val info = pm.queryIntentActivities(intent, 0)
            val size = info.size
            if (size == 0) return ""
            for (i in 0 until size) {
                val ri = info[i]
                if (ri.activityInfo.processName == pkg) {
                    return ri.activityInfo.name
                }
            }
            return info[0].activityInfo.name
        }

        private val topActivityByReflect: Activity?
            get() {
                try {
                    @SuppressLint("PrivateApi") val activityThreadClass =
                        Class.forName("android.app.ActivityThread")
                    val currentActivityThreadMethod =
                        activityThreadClass.getMethod("currentActivityThread").invoke(null)
                    val mActivityListField =
                        activityThreadClass.getDeclaredField("mActivityList")
                    mActivityListField.isAccessible = true
                    val activities =
                        mActivityListField[currentActivityThreadMethod] as Map<*, *>
                    for (activityRecord in activities.values) {
                        val activityRecordClass: Class<*> = activityRecord!!::class.java
                        val pausedField =
                            activityRecordClass.getDeclaredField("paused")
                        pausedField.isAccessible = true
                        if (!pausedField.getBoolean(activityRecord)) {
                            val activityField =
                                activityRecordClass.getDeclaredField("activity")
                            activityField.isAccessible = true
                            return activityField[activityRecord] as Activity
                        }
                    }
                } catch (e: Exception) {
                    loggerE(e.toString())
                }
                return null
            }

        fun recreate() {
            if (activityList.isNotEmpty()) {
                for (activity in activityList) {
                    activity.recreate()
                }
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}