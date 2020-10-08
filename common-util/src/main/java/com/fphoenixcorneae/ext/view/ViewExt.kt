package com.fphoenixcorneae.ext.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.applyCanvas
import androidx.core.view.ViewCompat
import com.fphoenixcorneae.util.ImageUtil
import kotlin.math.hypot

fun View.visible() = run { visibility = View.VISIBLE }

fun View.invisible() = run { visibility = View.INVISIBLE }

fun View.gone() = run { visibility = View.GONE }

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) = if (value) visible() else gone()

var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) = if (value) invisible() else visible()

var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) = if (value) gone() else visible()

/**
 * 设置内边距
 * @param size 大小,单位：px
 */
fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}

inline fun View.postDelayed(delayInMillis: Long, crossinline action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    postDelayed(runnable, delayInMillis)
    return runnable
}

/**
 * 生成位图
 * @param config 配置,默认：[Bitmap.Config.ARGB_8888]
 */
fun View.createBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    if (!ViewCompat.isLaidOut(this)) {
        throw IllegalStateException("View needs to be laid out before calling toBitmap()")
    }
    return Bitmap.createBitmap(width, height, config).applyCanvas {
        translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(this)
    }
}

var lastClickTime = 0L

/**
 * 防止重复点击事件 默认1秒内不可重复点击
 * @param interval 时间间隔 默认：1秒
 * @param action   执行方法
 */
fun View.clickNoRepeat(
    interval: Long = 1000,
    action: (view: View) -> Unit
) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && currentTime - lastClickTime < interval) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}

/**
 * 设置防止重复点击事件
 * @param views 需要设置点击事件的view集合
 * @param interval 时间间隔 默认：1秒
 * @param onClick 点击触发的方法
 */
fun setOnclickNoRepeat(vararg views: View?, interval: Long = 1000, onClick: (View) -> Unit) {
    views.forEach {
        it?.clickNoRepeat(interval) { view ->
            onClick.invoke(view)
        }
    }
}

/**
 * @desc：揭露动画监听器
 * @date：2020-06-28 17:22
 */
interface OnRevealAnimationListener {
    fun onRevealHide() {}

    fun onRevealShow() {}
}

/**
 * 揭露动画显示
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.animateRevealShow(
    startRadius: Int,
    @ColorRes color: Int,
    onRevealAnimationListener: OnRevealAnimationListener? = null
) {
    val cx = (left + right) / 2
    val cy = (top + bottom) / 2

    val finalRadius = hypot(width.toDouble(), height.toDouble()).toFloat()

    // 设置圆形显示动画
    val anim = ViewAnimationUtils.createCircularReveal(
        this,
        cx,
        cy,
        startRadius.toFloat(),
        finalRadius
    )
    anim.duration = 300
    anim.interpolator = AccelerateDecelerateInterpolator()
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            visible()
            onRevealAnimationListener?.onRevealShow()
        }

        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            setBackgroundColor(ContextCompat.getColor(context, color))
        }
    })
    anim.start()
}

/**
 * 圆圈凝聚效果
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun View.animateRevealHide(
    finalRadius: Int,
    @ColorRes color: Int,
    onRevealAnimationListener: OnRevealAnimationListener? = null
) {
    val cx = (left + right) / 2
    val cy = (top + bottom) / 2
    val initialRadius = width
    // 与入场动画的区别就是圆圈起始和终止的半径相反
    val anim = ViewAnimationUtils.createCircularReveal(
        this,
        cx,
        cy,
        initialRadius.toFloat(),
        finalRadius.toFloat()
    )
    anim.duration = 300
    anim.interpolator = AccelerateDecelerateInterpolator()
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            setBackgroundColor(ContextCompat.getColor(context, color))
        }

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            setBackgroundColor(Color.TRANSPARENT)
            onRevealAnimationListener?.onRevealHide()
            invisible()
        }
    })
    anim.start()
}

fun View.dpToPx(dp: Float): Float {
    val scale = resources.displayMetrics.density
    return dp * scale + 0.5f
}

fun View.dp2px(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun View.pxToDp(px: Float): Float {
    val scale = resources.displayMetrics.density
    return px / scale + 0.5f
}

fun View.px2dp(px: Float): Int {
    val scale = resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

fun ImageView.setTintColor(
    tintColor: Int
) {
    ImageUtil.setTintColor(this, tintColor)
}

fun ImageView.setTintColor(
    @DrawableRes drawableResId: Int,
    tintColor: Int
) {
    ImageUtil.setTintColor(this, drawableResId, tintColor)
}
