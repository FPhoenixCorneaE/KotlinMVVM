package com.fphoenixcorneae.util.xtoast

import android.R
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fphoenixcorneae.util.xtoast.draggable.BaseDraggable
import com.fphoenixcorneae.util.xtoast.draggable.MovingDraggable
import com.fphoenixcorneae.util.xtoast.listener.OnClickListener
import com.fphoenixcorneae.util.xtoast.listener.OnToastListener
import com.fphoenixcorneae.util.xtoast.listener.OnTouchListener
import com.fphoenixcorneae.util.xtoast.wrapper.ViewTouchWrapper

/**
 * 超级 Toast（能做 Toast 做不到的事，应付项目中的特殊需求）
 */
class XToast private constructor(
    /**
     * 上下文
     */
    val context: Context
) {
    /**
     * 根布局
     */
    var view: View? = null
        private set
    /**
     * 获取 WindowManager 对象
     */
    /**
     * 悬浮窗口
     */
    val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    /**
     * 获取 WindowManager 参数集
     */
    /**
     * 窗口参数
     */
    var windowParams: WindowManager.LayoutParams?
        private set
    /**
     * 当前是否已经显示
     */
    /**
     * 当前是否已经显示
     */
    var isShow = false
        private set
    /**
     * 窗口显示时长
     */
    private var mDuration = 0
    /**
     * Toast 生命周期管理
     */
    private var mLifecycle: ToastLifecycle? = null
    /**
     * 自定义拖动处理
     */
    private var mDraggable: BaseDraggable? = null
    /**
     * 吐司显示和取消监听
     */
    private var mListener: OnToastListener? = null
    var onClickListener: OnClickListener? = null
        private set

    /**
     * 创建一个局部悬浮窗
     */
    constructor(activity: Activity) : this(activity as Context) {
        // 如果当前 Activity 是全屏模式，那么添加这个标记，否则会导致 WindowManager 在某些机型上移动不到状态栏位置上
        if (activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0) {
            addWindowFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        // 跟随 Activity 的生命周期
        mLifecycle = ToastLifecycle(this, activity)
    }

    /**
     * 创建一个全局悬浮窗
     */
    constructor(application: Application) : this(application as Context) {
        // 设置成全局的悬浮窗，注意需要先申请悬浮窗权限，推荐使用：https://github.com/getActivity/XXPermissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            setWindowType(WindowManager.LayoutParams.TYPE_PHONE)
        }
    }

    /**
     * 是否有这个标志位
     */
    fun hasWindowFlags(flags: Int): Boolean {
        return windowParams!!.flags and flags != 0
    }

    /**
     * 添加一个标志位
     */
    fun addWindowFlags(flags: Int): XToast {
        windowParams!!.flags = windowParams!!.flags or flags
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 移除一个标志位
     */
    fun removeWindowFlags(flags: Int): XToast {
        windowParams!!.flags = windowParams!!.flags and flags.inv()
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置标志位
     */
    fun setWindowFlags(flags: Int): XToast {
        windowParams!!.flags = flags
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置窗口类型
     */
    fun setWindowType(type: Int): XToast {
        windowParams!!.type = type
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置动画样式
     */
    fun setAnimStyle(id: Int): XToast {
        windowParams!!.windowAnimations = id
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置随意拖动
     */
    fun setDraggable(): XToast {
        return setDraggable(MovingDraggable())
    }

    /**
     * 设置拖动规则
     */
    fun setDraggable(draggable: BaseDraggable): XToast {
        // 当前是否设置了不可触摸，如果是就擦除掉
        if (hasWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)) {
            removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        // WindowManager 几个焦点总结：https://blog.csdn.net/zjx2014430/article/details/51776128
        // 设置触摸范围为当前的 RootView，而不是整个 WindowManager
        addWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        mDraggable = draggable
        if (isShow) {
            update()
            mDraggable!!.start(this)
        }
        return this
    }

    /**
     * 设置宽度
     */
    fun setWidth(width: Int): XToast {
        windowParams!!.width = width
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置高度
     */
    fun setHeight(height: Int): XToast {
        windowParams!!.height = height
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 限定显示时长
     */
    fun setDuration(duration: Int): XToast {
        mDuration = duration
        if (isShow) {
            if (mDuration != 0) {
                removeCallbacks()
                postDelayed(ToastDismissRunnable(this), mDuration.toLong())
            }
        }
        return this
    }

    /**
     * 设置监听
     */
    fun setOnToastListener(listener: OnToastListener?): XToast {
        mListener = listener
        return this
    }

    /**
     * 设置重心
     */
    fun setGravity(gravity: Int): XToast {
        windowParams!!.gravity = gravity
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置 X 轴偏移量
     */
    fun setXOffset(x: Int): XToast {
        windowParams!!.x = x
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置 Y 轴偏移量
     */
    fun setYOffset(y: Int): XToast {
        windowParams!!.y = y
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置 WindowManager 参数集
     */
    fun setWindowParams(params: WindowManager.LayoutParams?): XToast {
        windowParams = params
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 设置布局
     */
    fun setView(id: Int): XToast {
        return setView(LayoutInflater.from(context).inflate(id, FrameLayout(context), false))
    }

    fun setView(view: View): XToast {
        this.view = view
        val params = view.layoutParams
        if (params != null
            && windowParams!!.width == WindowManager.LayoutParams.WRAP_CONTENT
            && windowParams!!.height == WindowManager.LayoutParams.WRAP_CONTENT
        ) { // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
            setWidth(params.width)
            setHeight(params.height)
        }
        // 如果当前没有设置重心，就自动获取布局重心
        if (windowParams!!.gravity == Gravity.NO_GRAVITY) {
            when (params) {
                is FrameLayout.LayoutParams -> {
                    setGravity(params.gravity)
                }
                is LinearLayout.LayoutParams -> {
                    setGravity(params.gravity)
                }
                else -> { // 默认重心是居中
                    setGravity(Gravity.CENTER)
                }
            }
        }
        if (isShow) {
            update()
        }
        return this
    }

    /**
     * 显示
     */
    fun show(): XToast {
        require(!(view == null || windowParams == null)) { "WindowParams and view cannot be empty" }
        // 如果当前已经显示取消上一次显示
        if (isShow) {
            cancel()
        }
        try {
            // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
            // java.lang.IllegalStateException: View android.widget.TextView
            // {3d2cee7 V.ED..... ......ID 0,0-312,153} has already been added to the window manager.
            windowManager.addView(view, windowParams)
            // 当前已经显示
            isShow = true
            // 如果当前限定了显示时长
            if (mDuration != 0) {
                postDelayed(ToastDismissRunnable(this), mDuration.toLong())
            }
            // 如果设置了拖拽规则
            if (mDraggable != null) {
                mDraggable!!.start(this)
            }
            // 注册 Activity 生命周期
            if (mLifecycle != null) {
                mLifecycle!!.register()
            }
            // 回调监听
            if (mListener != null) {
                mListener!!.onShow(this)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: BadTokenException) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * 取消
     */
    fun cancel(): XToast {
        if (isShow) {
            try { // 反注册 Activity 生命周期
                if (mLifecycle != null) {
                    mLifecycle!!.unregister()
                }
                // 如果当前 WindowManager 没有附加这个 View 则会抛出异常
                // java.lang.IllegalArgumentException: View=android.widget.TextView
                // {3d2cee7 V.ED..... ........ 0,0-312,153} not attached to window manager
                windowManager.removeView(view)
                // 回调监听
                if (mListener != null) {
                    mListener!!.onDismiss(this)
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            // 当前没有显示
            isShow = false
        }
        return this
    }

    /**
     * 更新
     */
    fun update() { // 更新 WindowManger 的显示
        windowManager.updateViewLayout(view, windowParams)
    }

    /**
     * 根据 ViewId 获取 View
     */
    fun <V : View?> findViewById(id: Int): V {
        checkNotNull(view) { "Please setup view" }
        return view!!.findViewById<View>(id) as V
    }

    /**
     * 跳转 Activity
     */
    fun startActivity(clazz: Class<out Activity?>?) {
        startActivity(Intent(context, clazz))
    }

    fun startActivity(intent: Intent) {
        if (context !is Activity) {
            // 如果当前的上下文不是 Activity，调用 startActivity 必须加入新任务栈的标记
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * 设置可见状态
     */
    fun setVisibility(id: Int, visibility: Int): XToast {
        findViewById<View>(id).visibility = visibility
        return this
    }

    /**
     * 设置文本
     */
    fun setText(id: Int): XToast {
        return setText(R.id.message, id)
    }

    fun setText(viewId: Int, stringId: Int): XToast {
        return setText(viewId, context.resources.getString(stringId))
    }

    fun setText(text: CharSequence?): XToast {
        return setText(R.id.message, text)
    }

    fun setText(id: Int, text: CharSequence?): XToast {
        (findViewById<View>(id) as TextView).text = text
        return this
    }

    /**
     * 设置文本颜色
     */
    fun setTextColor(id: Int, color: Int): XToast {
        (findViewById<View>(id) as TextView).setTextColor(color)
        return this
    }

    /**
     * 设置提示
     */
    fun setHint(viewId: Int, stringId: Int): XToast {
        return setHint(viewId, context.resources.getString(stringId))
    }

    fun setHint(id: Int, text: CharSequence?): XToast {
        (findViewById<View>(id) as TextView).hint = text
        return this
    }

    /**
     * 设置背景
     */
    fun setBackground(viewId: Int, drawableId: Int): XToast {
        val drawable: Drawable?
        drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableId)
        } else {
            context.resources.getDrawable(drawableId)
        }
        return setBackground(viewId, drawable)
    }

    fun setBackground(id: Int, drawable: Drawable?): XToast {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById<View>(id).background = drawable
        } else {
            findViewById<View>(id).setBackgroundDrawable(drawable)
        }
        return this
    }

    /**
     * 设置图片
     */
    fun setImageDrawable(viewId: Int, drawableId: Int): XToast {
        val drawable: Drawable?
        drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableId)
        } else {
            context.resources.getDrawable(drawableId)
        }
        return setImageDrawable(viewId, drawable)
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable?): XToast {
        (findViewById<View>(viewId) as ImageView).setImageDrawable(
            drawable
        )
        return this
    }

    /**
     * 设置点击事件
     */
    fun setOnClickListener(listener: OnClickListener?): XToast {
        onClickListener = listener
        // 当前是否设置了不可触摸，如果是就擦除掉
        if (hasWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)) {
            removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (isShow) {
                update()
            }
        }
        return this
    }

    /**
     * 设置触摸事件
     */
    fun setOnTouchListener(id: Int, listener: OnTouchListener): XToast {
        ViewTouchWrapper(this, findViewById(id), listener)
        // 当前是否设置了不可触摸，如果是就擦除掉
        if (hasWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)) {
            removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (isShow) {
                update()
            }
        }
        return this
    }

    /**
     * 延迟执行
     */
    fun post(r: Runnable?): Boolean {
        return postDelayed(r, 0)
    }

    /**
     * 延迟一段时间执行
     */
    fun postDelayed(r: Runnable?, delayMillis: Long): Boolean {
        var delayMillis = delayMillis
        if (delayMillis < 0) {
            delayMillis = 0
        }
        return postAtTime(r, SystemClock.uptimeMillis() + delayMillis)
    }

    /**
     * 在指定的时间执行
     */
    fun postAtTime(
        r: Runnable?,
        uptimeMillis: Long
    ): Boolean { // 发送和这个 WindowManager 相关的消息回调
        return handler.postAtTime(r, this, uptimeMillis)
    }

    /**
     * 移除消息回调
     */
    fun removeCallbacks() {
        handler.removeCallbacksAndMessages(this)
    }

    companion object {
        /**
         * 获取 Handler
         */
        val handler = Handler(Looper.getMainLooper())
    }

    init {
        windowParams = WindowManager.LayoutParams()
        // 配置一些默认的参数
        windowParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowParams!!.format = PixelFormat.TRANSLUCENT
        windowParams!!.windowAnimations = R.style.Animation_Toast
        windowParams!!.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        windowParams!!.packageName = context.packageName
    }
}