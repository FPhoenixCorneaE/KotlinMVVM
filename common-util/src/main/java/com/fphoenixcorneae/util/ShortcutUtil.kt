package com.fphoenixcorneae.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.fphoenixcorneae.util.CloseUtil.Companion.closeIOQuietly


/**
 * 创建删除快捷图标工具类
 * 需要权限:
 * android.permission.INSTALL_SHORTCUT
 * android.permission.UNINSTALL_SHORTCUT
 */
class ShortcutUtil private constructor() {

    companion object {
        /**
         * 检测是否存在快捷键
         *
         * @param activity     Activity
         * @param shortcutName 快捷方式的名称
         * @return 是否存在桌面图标
         */
        @SuppressLint("ObsoleteSdkInt")
        fun hasShortcut(activity: FragmentActivity, shortcutName: String): Boolean {
            var isInstallShortcut = false
            val cr = activity.contentResolver
            val uriStr = StringBuilder()
            uriStr.append("content://")
            var permission = "com.android.launcher.permission.READ_SETTINGS"
            var authority = getAuthorityFromPermission(
                activity,
                permission
            )
            if (authority.isEmpty()) {
                // 需要把这里的权限添加到清单配置文件中,否则查询失败
                permission = getCurrentLauncherPackageName(activity) + ".permission.READ_SETTINGS"
                authority = getAuthorityFromPermission(
                    activity,
                    permission
                )
            }
            if (authority.isEmpty()) {
                when {
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO -> {
                        uriStr.append("com.android.launcher.settings")
                    }
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT -> {
                        uriStr.append("com.android.launcher2.settings")
                    }
                    else -> {
                        uriStr.append("com.android.launcher3.settings")
                    }
                }
            } else {
                uriStr.append(authority)
            }
            uriStr.append("/favorites?notify=true")
            val contentUri = Uri.parse(uriStr.toString())
            var cursor: Cursor? = null
            try {
                cursor = cr.query(
                    contentUri,
                    arrayOf("title", "iconResource"),
                    "title=?",
                    arrayOf(shortcutName.trim { it <= ' ' }),
                    null
                )
                if (cursor != null && cursor.count > 0) {
                    isInstallShortcut = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                closeIOQuietly(cursor)
            }
            return isInstallShortcut
        }

        /**
         * get the current Launcher's Package Name
         */
        private fun getCurrentLauncherPackageName(context: Context): String {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            val res = context.packageManager.resolveActivity(intent, 0)
            return when {
                res?.activityInfo == null -> {
                    ""
                }
                res.activityInfo.packageName == "android" -> {
                    ""
                }
                else -> {
                    res.activityInfo.packageName
                }
            }
        }

        private fun getAuthorityFromPermission(context: Context, permission: String): String {
            val packageInfoList =
                context.packageManager.getInstalledPackages(PackageManager.GET_PROVIDERS)
            packageInfoList.forEach { packageInfo ->
                val providerInfoArray = packageInfo.providers
                providerInfoArray?.forEach { providerInfo ->
                    when (permission) {
                        providerInfo.readPermission -> {
                            return providerInfo.authority
                        }
                        providerInfo.writePermission -> {
                            return providerInfo.authority
                        }
                    }
                }
            }
            return ""
        }

        /**
         * 为程序创建桌面快捷方式
         * 添加权限"com.android.launcher.permission.INSTALL_SHORTCUT"
         * 需要在权限管理里边将桌面快捷方式权限打开
         *
         * @param activity     Activity
         * @param shortcutName 快捷方式的名称
         */
        fun createShortcut(
            activity: FragmentActivity,
            shortcutName: String?,
            id: String = "",
            shortcutBitmap: Bitmap? = null,
            bundle: Bundle? = null
        ) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    val shortcutManager =
                        activity.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
                    when {
                        shortcutManager.isRequestPinShortcutSupported -> {
                            val shortcutIntent = Intent()
                            shortcutIntent.component =
                                ComponentName(activity, activity::class.java)
                            //action 必须设置,不然报错
                            shortcutIntent.action = Intent.ACTION_VIEW
                            shortcutIntent.putExtras(BundleBuilder.of(bundle).get())
                            val shortcutInfo = ShortcutInfo.Builder(activity, id)
                                .setShortLabel(shortcutName.toString())
                                .setIcon(Icon.createWithBitmap(shortcutBitmap))
                                .setIntent(shortcutIntent)
                                .build()
                            // 当添加快捷方式的确认弹框弹出来时,将被回调
                            val pendingIntent = PendingIntent.getBroadcast(
                                activity,
                                0,
                                Intent(activity, BroadcastReceiver::class.java),
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            shortcutManager.requestPinShortcut(
                                shortcutInfo,
                                pendingIntent.intentSender
                            )
                        }
                    }
                }
                else -> {
                    val shortcut = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
                    // 快捷方式的名称
                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName)
                    // 快捷方式的图标
                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutBitmap)
                    // 不允许重复创建
                    shortcut.putExtra("duplicate", false)
                    val shortcutIntent = Intent(Intent.ACTION_MAIN)
                    shortcutIntent.setClassName(activity, activity.javaClass.name)
                    // 添加category:CATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
                    shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shortcutIntent.putExtras(BundleBuilder.of(bundle).get())
                    shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                    activity.sendBroadcast(shortcut)
                }
            }
        }

        /**
         * 删除程序的快捷方式
         * 添加权限"com.android.launcher.permission.UNINSTALL_SHORTCUT"
         *
         * @param activity     Activity
         * @param shortcutName 快捷方式的名称
         */
        fun deleteShortcut(activity: FragmentActivity, shortcutName: String?) {
            val shortcut = Intent("com.android.launcher.action.UNINSTALL_SHORTCUT")
            // 快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName)
            val appClass =
                activity.packageName + "." + activity.localClassName
            val comp = ComponentName(activity.packageName, appClass)
            shortcut.putExtra(
                Intent.EXTRA_SHORTCUT_INTENT,
                Intent(Intent.ACTION_MAIN).setComponent(comp)
            )
            activity.sendBroadcast(shortcut)
        }
    }

    /**
     * Don't let anyone instantiate this class.
     */
    init {
        throw UnsupportedOperationException("U can't initialize me...")
    }
}