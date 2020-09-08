package com.fphoenixcorneae.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.ext.loggerI
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 上下文工具类,very important：必须首先初始化！
 * 1、在Application的onCreate()中进行初始化！
 * 2、在ContentProvider的onCreate()中进行初始化,然后在AndroidManifest.xml中配置Provider！
 * @date 2019/06/20 21:33
 */
class ContextUtil private constructor() {

    init {
        throw UnsupportedOperationException("You can't initialize me...")
    }

    companion object {

        private val sActivityLifecycleCallbacks = ActivityLifecycleCallbacksImpl()
        private val sThreadPool = ThreadUtil.getCachedPool()
        private val sHandler = Handler(Looper.getMainLooper())

        @SuppressLint("StaticFieldLeak")
        private var sContext: Application? = null

        /**
         * 初始化上下文
         *
         * @param context 上下文
         */
        fun init(context: Context) {
            sContext = context.applicationContext as Application
            // 注册应用生命周期回调
            sContext?.registerActivityLifecycleCallbacks(sActivityLifecycleCallbacks)
            sThreadPool?.execute { AdaptScreenUtil.preLoad() }
        }

        /**
         * 获取ApplicationContext
         *
         * @return ApplicationContext
         */
        val context: Application
            get() {
                if (sContext == null) {
                    throw NullPointerException("U should call ContextUtil.init(context: Context) first!")
                }
                return sContext as Application
            }

        fun getActivityLifecycle(): ActivityLifecycleCallbacksImpl {
            return sActivityLifecycleCallbacks
        }

        fun getActivityList(): MutableList<Activity> {
            return sActivityLifecycleCallbacks.mActivityList
        }

        fun runOnUiThread(runnable: Runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run()
            } else {
                sHandler.post(runnable)
            }
        }

        fun runOnUiThreadDelayed(
            runnable: Runnable,
            delayMillis: Long
        ) {
            sHandler.postDelayed(runnable, delayMillis)
        }

        /**
         * 销毁
         */
        fun dispose() {
            sContext = null
        }
    }
}

/**
 * ActivityLifecycleCallbacks接口实现
 * 1、每一个Activity的生命周期都会回调到这里的对应方法;
 * 2、可以统计Activity的个数等;
 */
class ActivityLifecycleCallbacksImpl : ActivityLifecycleCallbacks {
    val mActivityList = Collections.synchronizedList(LinkedList<Activity>())
    val mStatusListeners: MutableList<OnAppStatusChangedListener?> =
        ArrayList()
    val mDestroyedListenerMap: MutableMap<Activity, MutableList<OnActivityDestroyedListener?>> =
        HashMap()
    private var mForegroundCount = 0
    private var mConfigCount = 0
    private var mIsBackground = false

    /**
     * onActivityCreated
     */
    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        logActivityLifecycle("onCreated(): ", activity)
        LanguageUtil.applyLanguage()
        setAnimatorsEnabled()
        ActivityUtil.setTopActivity(activity)
    }

    /**
     * onActivityStarted
     */
    override fun onActivityStarted(activity: Activity) {
        logActivityLifecycle("onStarted(): ", activity)
        if (!mIsBackground) {
            ActivityUtil.setTopActivity(activity)
        }
        if (mConfigCount < 0) {
            ++mConfigCount
        } else {
            ++mForegroundCount
        }
    }

    /**
     * onActivityResumed
     */
    override fun onActivityResumed(activity: Activity) {
        logActivityLifecycle("onResumed(): ", activity)
        ActivityUtil.setTopActivity(activity)
        if (mIsBackground) {
            mIsBackground = false
            postStatus(activity, true)
        }
        processHideSoftInputOnActivityDestroy(activity, false)
    }

    /**
     * onActivityPaused
     */
    override fun onActivityPaused(activity: Activity) {
        logActivityLifecycle("onPaused(): ", activity)
    }

    /**
     * onActivityStopped
     */
    override fun onActivityStopped(activity: Activity) {
        logActivityLifecycle("onStopped(): ", activity)
        if (activity.isChangingConfigurations) {
            --mConfigCount
        } else {
            --mForegroundCount
            if (mForegroundCount <= 0) {
                mIsBackground = true
                postStatus(activity, false)
            }
        }
        processHideSoftInputOnActivityDestroy(activity, true)
    }

    /**
     * onActivitySaveInstanceState
     */
    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
        logActivityLifecycle("onSaveInstanceState(): ", activity)
    }

    /**
     * onActivityDestroyed
     */
    override fun onActivityDestroyed(activity: Activity) {
        logActivityLifecycle("onDestroyed(): ", activity)
        mActivityList.remove(activity)
        consumeOnActivityDestroyedListener(activity)
        KeyboardUtil.fixSoftInputLeaks(activity.window)
    }

    /**
     * 打印activity生命周期
     */
    private fun logActivityLifecycle(message: String, activity: Activity) {
        loggerD(message + activity.componentName.className, "ActivityLifecycle")
    }

    fun addOnAppStatusChangedListener(listener: OnAppStatusChangedListener?) {
        mStatusListeners.add(listener)
    }

    fun removeOnAppStatusChangedListener(listener: OnAppStatusChangedListener?) {
        mStatusListeners.remove(listener)
    }

    fun removeOnActivityDestroyedListener(activity: Activity?) {
        if (activity == null) return
        mDestroyedListenerMap.remove(activity)
    }

    fun addOnActivityDestroyedListener(
        activity: Activity?,
        listener: OnActivityDestroyedListener?
    ) {
        if (activity == null || listener == null) return
        var listeners: MutableList<OnActivityDestroyedListener?>? =
            mDestroyedListenerMap[activity]
        if (listeners == null) {
            listeners =
                CopyOnWriteArrayList()
            mDestroyedListenerMap[activity] = listeners
        } else {
            if (listeners.contains(listener)) return
        }
        listeners.add(listener)
    }

    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    private fun processHideSoftInputOnActivityDestroy(
        activity: Activity,
        isSave: Boolean
    ) {
        if (isSave) {
            val attrs = activity.window.attributes
            val softInputMode = attrs.softInputMode
            activity.window.decorView.setTag(-123, softInputMode)
            activity.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        } else {
            val tag = activity.window.decorView.getTag(-123) as? Int ?: return
            ContextUtil.runOnUiThreadDelayed(Runnable {
                val window = activity.window
                window?.setSoftInputMode(tag)
            }, 100)
        }
    }

    private fun postStatus(activity: Activity, isForeground: Boolean) {
        if (mStatusListeners.isEmpty()) return
        for (statusListener in mStatusListeners) {
            if (isForeground) {
                statusListener?.onForeground(activity)
            } else {
                statusListener?.onBackground(activity)
            }
        }
    }

    private fun consumeOnActivityDestroyedListener(activity: Activity) {
        val iterator: MutableIterator<Map.Entry<Activity, List<OnActivityDestroyedListener?>>> =
            mDestroyedListenerMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry: Map.Entry<Activity, List<OnActivityDestroyedListener?>> =
                iterator.next()
            if (entry.key === activity) {
                val value: List<OnActivityDestroyedListener?> =
                    entry.value
                for (listener in value) {
                    listener?.onActivityDestroyed(activity)
                }
                iterator.remove()
            }
        }
    }

    /**
     * Set animators enabled.
     */
    private fun setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
            return
        }
        try {
            val sDurationScaleField =
                ValueAnimator::class.java.getDeclaredField("sDurationScale")
            sDurationScaleField.isAccessible = true
            val sDurationScale = sDurationScaleField[null] as Float
            if (sDurationScale == 0f) {
                sDurationScaleField[null] = 1f
                loggerI("setAnimatorsEnabled: Animators are enabled now!")
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

interface OnAppStatusChangedListener {
    fun onForeground(activity: Activity?)
    fun onBackground(activity: Activity?)
}

interface OnActivityDestroyedListener {
    fun onActivityDestroyed(activity: Activity?)
}

interface Func1<Ret, Par> {
    fun call(param: Par): Ret
}
