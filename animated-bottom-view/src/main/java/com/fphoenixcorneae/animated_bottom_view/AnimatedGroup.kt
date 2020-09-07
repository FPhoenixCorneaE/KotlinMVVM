package com.fphoenixcorneae.animated_bottom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.fphoenixcorneae.util.ImageUtil
import kotlinx.android.synthetic.main.animated_layout_circle_animation.view.*

/**
 *  执行动画的View(上下左右移动+旋转)
 */
class AnimatedGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    fun setImageResource(resource: Int, selectedColor: Int) {
        ImageUtil.setTintColor(ivRotate, resource, selectedColor)
    }

    fun getAnimationView(): View {
        return ivRotate
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.animated_layout_circle_animation, this)
    }
}