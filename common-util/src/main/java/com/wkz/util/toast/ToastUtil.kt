package com.wkz.util.toast

import android.R
import android.app.AppOpsManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.wkz.util.toast.style.ToastAliPayStyle
import com.wkz.util.toast.style.ToastBlackStyle
import com.wkz.util.toast.style.ToastQQStyle
import com.wkz.util.toast.style.ToastWhiteStyle
import java.lang.reflect.InvocationTargetException

/**
 * Toast 工具类
 */
object ToastUtil {

    private var sInterceptor: IToastInterceptor? = null
    private var sStrategy: IToastStrategy? = null
    private var sStyle: IToastStyle? = null
    private var sToast: Toast? = null
    /**
     * 初始化 ToastUtil 及样式
     */
    fun init(application: Application, style: IToastStyle?) {
        initStyle(style)
        init(application)
    }

    /**
     * 初始化 ToastUtil，在Application中初始化
     *
     * @param application 应用的上下文
     */
    fun init(application: Application) {
        // 初始化 Toast 拦截器
        if (sInterceptor == null) {
            setToastInterceptor(ToastInterceptor())
        }
        // 初始化 Toast 显示处理器
        if (sStrategy == null) {
            setToastHandler(ToastStrategy())
        }
        // 初始化 Toast 样式
        if (sStyle == null) {
            initStyle(ToastBlackStyle(application))
        }
        // 初始化吐司
        toast = if (isNotificationEnabled(application)) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                // 解决 Android 7.1 上主线程被阻塞后吐司会报错的问题
                SafeToast(application)
            } else {
                BaseToast(application)
            }
        } else { // 解决关闭通知栏权限后 Toast 不显示的问题
            SupportToast(application)
        }
        // 初始化布局
        setView(createTextView(application.applicationContext))
        // 初始化位置
        setGravity(
            sStyle!!.gravity,
            sStyle!!.xOffset,
            sStyle!!.yOffset
        )
    }

    /**
     * 显示一个对象的吐司
     *
     * @param object 对象
     */
    fun show(`object`: Any?) {
        show(`object`?.toString() ?: "null")
    }

    /**
     * 显示一个吐司
     *
     * @param id 如果传入的是正确的string id就显示对应字符串
     * 如果不是则显示一个整数的string
     */
    fun show(id: Int) {
        checkToastState()
        try { // 如果这是一个资源 id
            show(
                sToast!!.view.context.resources.getText(
                    id
                )
            )
        } catch (ignored: Resources.NotFoundException) { // 如果这是一个 int 数据
            show(id.toString())
        }
    }

    /**
     * 显示一个吐司
     *
     * @param text 需要显示的文本
     */
    @Synchronized
    fun show(text: CharSequence?) {
        checkToastState()
        if (sInterceptor!!.intercept(
                sToast,
                text
            )
        ) {
            return
        }
        sStrategy!!.show(text)
    }

    /**
     * 取消吐司的显示
     */
    @Synchronized
    fun cancel() {
        checkToastState()
        sStrategy!!.cancel()
    }

    /**
     * 设置吐司的位置
     *
     * @param gravity 重心
     * @param xOffset x轴偏移
     * @param yOffset y轴偏移
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        var gravity = gravity
        checkToastState()
        // 适配 Android 4.2 新特性，布局反方向（开发者选项 - 强制使用从右到左的布局方向）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            gravity = Gravity.getAbsoluteGravity(
                gravity,
                sToast!!.view.resources.configuration.layoutDirection
            )
        }
        sToast!!.setGravity(gravity, xOffset, yOffset)
    }

    /**
     * 给当前Toast设置新的布局，具体实现可看[BaseToast.setView]
     */
    fun setView(layoutId: Int) {
        checkToastState()
        setView(
            View.inflate(
                sToast!!.view.context.applicationContext,
                layoutId,
                null
            )
        )
    }

    fun setView(view: View) {
        checkToastState()
        // 当前必须用 Application 的上下文创建的 View，否则可能会导致内存泄露
        val context = view.context
        require(context is Application) { "The view must be initialized using the context of the application" }
        // 如果吐司已经创建，就重新初始化吐司
        if (sToast != null) {
            // 取消原有吐司的显示
            sToast!!.cancel()
            sToast!!.view = view
        }
    }

    /**
     * 获取当前 Toast 的视图
     */
    fun <V : View?> getView(): V {
        checkToastState()
        return sToast!!.view as V
    }

    /**
     * 初始化全局的Toast样式
     *
     * @param style 样式实现类，框架已经实现四种不同的样式
     * 支付宝样式：[ToastAliPayStyle]
     * 黑色样式：[ToastBlackStyle]
     * 白色样式：[ToastWhiteStyle]
     * 仿QQ样式：[ToastQQStyle]
     */
    fun initStyle(style: IToastStyle?) {
        style?.let {
            sStyle = it
            // 如果吐司已经创建，就重新初始化吐司
            if (sToast != null) {
                // 取消原有吐司的显示
                sToast!!.cancel()
                sToast!!.view = createTextView(
                    sToast!!.view.context.applicationContext
                )
                sToast!!.setGravity(
                    sStyle!!.gravity,
                    sStyle!!.xOffset,
                    sStyle!!.yOffset
                )
            }
        }
    }

    /**
     * 设置 Toast 显示规则
     */
    fun setToastHandler(handler: IToastStrategy?) {
        handler?.let {
            sStrategy = it
            if (sToast != null) {
                sStrategy!!.bind(sToast)
            }
        }
    }

    /**
     * 设置 Toast 拦截器（可以根据显示的内容决定是否拦截这个Toast）
     * 场景：打印 Toast 内容日志、根据 Toast 内容是否包含敏感字来动态切换其他方式显示（这里可以使用另外一套框架 XToast）
     */
    fun setToastInterceptor(interceptor: IToastInterceptor?) {
        interceptor?.let {
            sInterceptor = it
        }
    }

    /**
     * 获取当前Toast对象
     * 设置当前Toast对象
     */
    var toast: Toast?
        get() = sToast
        set(toast) {
            toast?.let {
                sToast = toast
                if (sStrategy != null) {
                    sStrategy!!.bind(sToast)
                }
            }
        }

    /**
     * 检查吐司状态，如果未初始化请先调用[ToastUtil.init]
     */
    private fun checkToastState() {
        // 吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        checkNotNull(sToast) { "ToastUtil has not been initialized" }
    }

    /**
     * 生成默认的 TextView 对象
     */
    private fun createTextView(context: Context): TextView {
        val drawable = GradientDrawable()
        // 设置背景色
        drawable.setColor(sStyle!!.backgroundColor)
        // 设置圆角大小
        drawable.cornerRadius = sStyle!!.cornerRadius.toFloat()
        val textView = TextView(context)
        textView.id = R.id.message
        textView.setTextColor(sStyle!!.textColor)
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            sStyle!!.textSize
        )
        // 适配布局反方向
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setPaddingRelative(
                sStyle!!.paddingStart,
                sStyle!!.paddingTop,
                sStyle!!.paddingEnd,
                sStyle!!.paddingBottom
            )
        } else {
            textView.setPadding(
                sStyle!!.paddingStart,
                sStyle!!.paddingTop,
                sStyle!!.paddingEnd,
                sStyle!!.paddingBottom
            )
        }
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // setBackground API 版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.background = drawable
        } else {
            textView.setBackgroundDrawable(drawable)
        }
        // 设置 Z 轴阴影
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.z = sStyle!!.z.toFloat()
        }
        // 设置最大显示行数
        if (sStyle!!.maxLines > 0) {
            textView.maxLines = sStyle!!.maxLines
        }
        return textView
    }

    /**
     * 检查通知栏权限有没有开启
     * 参考 SupportCompat 包中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled();
     */
    private fun isNotificationEnabled(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                val appOps =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val appInfo = context.applicationInfo
                val pkg = context.applicationContext.packageName
                val uid = appInfo.uid
                try {
                    val appOpsClass =
                        Class.forName(AppOpsManager::class.java.name)
                    val checkOpNoThrowMethod = appOpsClass.getMethod(
                        "checkOpNoThrow",
                        Integer.TYPE,
                        Integer.TYPE,
                        String::class.java
                    )
                    val opPostNotificationValue =
                        appOpsClass.getDeclaredField("OP_POST_NOTIFICATION")
                    val value = opPostNotificationValue[Int::class.java] as Int
                    checkOpNoThrowMethod.invoke(
                        appOps,
                        value,
                        uid,
                        pkg
                    ) as Int == AppOpsManager.MODE_ALLOWED
                } catch (ignored: ClassNotFoundException) {
                    true
                } catch (ignored: NoSuchMethodException) {
                    true
                } catch (ignored: NoSuchFieldException) {
                    true
                } catch (ignored: InvocationTargetException) {
                    true
                } catch (ignored: IllegalAccessException) {
                    true
                } catch (ignored: RuntimeException) {
                    true
                }
            }
            else -> {
                true
            }
        }
    }
}