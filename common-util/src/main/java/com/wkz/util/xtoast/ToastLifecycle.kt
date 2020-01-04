package com.wkz.util.xtoast

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/**
 * Toast 生命周期管理，防止内存泄露
 */
internal class ToastLifecycle(private val mToast: XToast, private val mActivity: Activity) :
    ActivityLifecycleCallbacks {
    /**
     * 注册监听
     */
    fun register() {
        mActivity.application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 取消监听
     */
    fun unregister() {
        mActivity.application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle
    ) {
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) { // 一定要在 onPaused 方法中销毁掉，如果在 onDestroyed 方法中还是会导致内存泄露
        if (mActivity === activity && activity.isFinishing && mToast.isShow) {
            mToast.cancel()
        }
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
    }

    override fun onActivityDestroyed(activity: Activity) {}

}