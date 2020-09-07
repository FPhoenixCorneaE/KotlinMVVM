package com.fphoenixcorneae.util.toast

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.view.WindowManager

/**
 * WindowManager 辅助类（用于获取当前 Activity 的 WindowManager 对象）
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
internal class WindowHelper private constructor(
    /**
     * 用于 Activity 暂停时移除 WindowManager
     */
    private val mToastHelper: ToastHelper
) : ActivityLifecycleCallbacks {
    /**
     * Activity 存放集合
     */
    private val mActivitySet =
        ArrayMap<String?, Activity>()
    /**
     * 当前 Activity 对象标记
     */
    private var mCurrentTag: String? = null
    // 如果使用的 WindowManager 对象不是当前 Activity 创建的，则会抛出异常
    // android.view.WindowManager$BadTokenException: Unable to add window -- token null is not for an application

    /**
     * 获取一个WindowManager对象
     *
     * @return 如果获取不到则抛出空指针异常
     */
    @get:Throws(NullPointerException::class)
    val windowManager: WindowManager
        get() {
            if (mCurrentTag != null) {
                // 如果使用的 WindowManager 对象不是当前 Activity 创建的，则会抛出异常
                // android.view.WindowManager$BadTokenException: Unable to add window -- token null is not for an application
                val activity = mActivitySet[mCurrentTag]
                if (activity != null) {
                    return getWindowManagerObject(activity)
                }
            }
            throw NullPointerException()
        }

    /**
     * [Application.ActivityLifecycleCallbacks]
     */
    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        mCurrentTag = getObjectTag(activity)
        mActivitySet[mCurrentTag] = activity
    }

    override fun onActivityStarted(activity: Activity) {
        mCurrentTag = getObjectTag(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        mCurrentTag = getObjectTag(activity)
    }

    // A 跳转 B 页面的生命周期方法执行顺序：
    // onPause(A) ---> onCreate(B) ---> onStart(B) ---> onResume(B) ---> onStop(A) ---> onDestroyed(A)
    override fun onActivityPaused(activity: Activity) {
        // 取消这个吐司的显示
        mToastHelper.cancel()
        // 不能放在 onStop 或者 onDestroyed 方法中，因为此时新的 Activity 已经创建完成，必须在这个新的 Activity 未创建之前关闭这个 WindowManager
        // 调用取消显示会直接导致新的 Activity 的 onCreate 调用显示吐司可能显示不出来的问题（又或者有时候会立马显示然后立马消失的那种效果）
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        // 移除对这个 Activity 的引用
        mActivitySet.remove(getObjectTag(activity))
        // 如果当前的 Activity 是最后一个的话
        if (getObjectTag(activity) == mCurrentTag) {
            // 清除当前标记
            mCurrentTag = null
        }
    }

    companion object {
        fun register(toast: ToastHelper, application: Application): WindowHelper {
            val window = WindowHelper(toast)
            application.registerActivityLifecycleCallbacks(window)
            return window
        }

        /**
         * 获取一个对象的独一无二的标记
         */
        private fun getObjectTag(`object`: Any): String {
            // 对象所在的包名 + 对象的内存地址
            return `object`.javaClass.name + Integer.toHexString(`object`.hashCode())
        }

        /**
         * 获取一个 WindowManager 对象
         */
        private fun getWindowManagerObject(activity: Activity): WindowManager {
            return activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
    }

}