package com.fphoenixcorneae.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.fphoenixcorneae.util.ImageUtil
import kotlin.math.hypot

fun View.visible(visible: Boolean) = when {
    visible -> visible()
    else -> gone()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

var lastClickTime = 0L

/**
 * 防止重复点击事件 默认1秒内不可重复点击
 * @param interval 时间间隔 默认1秒
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
 * @param interval 时间间隔 默认1秒
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
