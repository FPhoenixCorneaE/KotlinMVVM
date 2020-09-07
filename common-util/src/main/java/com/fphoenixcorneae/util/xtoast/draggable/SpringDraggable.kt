package com.fphoenixcorneae.util.xtoast.draggable

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import com.fphoenixcorneae.util.xtoast.XToast

/**
 * 拖拽后回弹处理实现类
 */
class SpringDraggable : BaseDraggable() {
    private var mScreenWidth = 0f
    private var mViewDownX = 0f
    private var mViewDownY = 0f

    override fun start(toast: XToast) {
        super.start(toast)
        mScreenWidth = screenWidth.toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // 获取当前触摸点在 屏幕 的位置
        val rawMoveX = event.rawX
        val rawMoveY = event.rawY - statusBarHeight
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 获取当前触摸点在 View 的位置
                mViewDownX = event.x
                mViewDownY = event.y
                mWhetherMove = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // 更新移动的位置
                mWhetherMove = true
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY)
            }
            MotionEvent.ACTION_UP -> {
                if (!mWhetherMove) {
                    onClickListener?.onClick(xToast, v)
                } else {
                    // 自动回弹吸附
                    val rawFinalX: Float = when {
                        rawMoveX < mScreenWidth / 2 -> {
                            0f
                        }
                        else -> {
                            mScreenWidth
                        }
                    }
                    // 从移动的点回弹到边界上
                    startAnimation(
                        rawMoveX - mViewDownX,
                        rawFinalX - mViewDownX,
                        rawMoveY - mViewDownY
                    )
                    return true
                }
            }
            else -> {
            }
        }
        return false
    }

    /**
     * 获取屏幕的宽度
     */
    private val screenWidth: Int
        private get() {
            val manager = windowManager
            val outMetrics = DisplayMetrics()
            manager!!.defaultDisplay.getMetrics(outMetrics)
            return outMetrics.widthPixels
        }

    /**
     * 执行动画
     *
     * @param startX X轴起点坐标
     * @param endX   X轴终点坐标
     * @param y      Y轴坐标
     */
    private fun startAnimation(
        startX: Float,
        endX: Float,
        y: Float
    ) {
        val animator = ValueAnimator.ofFloat(startX, endX)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            updateLocation(
                animation.animatedValue as Float,
                y
            )
        }
        animator.start()
    }
}