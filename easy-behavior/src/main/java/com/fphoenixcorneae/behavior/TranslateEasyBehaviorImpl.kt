package com.fphoenixcorneae.behavior

import android.animation.ValueAnimator
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * TranslateEasyBehaviorImpl using for TitleBar or BottomNavigationView
 */
class TranslateEasyBehaviorImpl private constructor(var mTarget: View) : IEasyBehavior {
    var mStartY = 0f
    override var state: Int = IEasyBehavior.STATE_SHOW
        private set
    var mMode = MODE_TITLE
    private val mFirstY: Float = mTarget.y
    private val mMargin: Float = ((mTarget.layoutParams as CoordinatorLayout.LayoutParams).topMargin
            + (mTarget.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin).toFloat()

    override fun show() {
        if (mMode == MODE_TITLE) {
            showTitle()
        } else if (mMode == MODE_BOTTOM) {
            showBottom()
        }
    }

    private fun hideTitle() {
        val va = ValueAnimator.ofFloat(mTarget.y, -mTarget.height.toFloat())
        va.duration = 400
        va.addUpdateListener { valueAnimator -> mTarget.y = (valueAnimator.animatedValue as Float) }
        va.start()
        state = IEasyBehavior.STATE_HIDE
    }

    private fun showTitle() {
        val va = ValueAnimator.ofFloat(mTarget.y, 0f)
        va.duration = 400
        va.addUpdateListener { valueAnimator -> mTarget.y = (valueAnimator.animatedValue as Float) }
        va.start()
        state = IEasyBehavior.STATE_SHOW
    }

    override fun hide() {
        if (mMode == MODE_TITLE) {
            hideTitle()
        } else if (mMode == MODE_BOTTOM) {
            hideBottom()
        }
    }

    private fun showBottom() {
        val va = ValueAnimator.ofFloat(mTarget.y, mFirstY)
        va.duration = 400
        va.addUpdateListener { valueAnimator -> mTarget.y = (valueAnimator.animatedValue as Float) }
        va.start()
        state = IEasyBehavior.STATE_SHOW
    }

    private fun hideBottom() {
        val va = ValueAnimator.ofFloat(mTarget.y, mFirstY + mTarget.height + mMargin)
        va.duration = 400
        va.addUpdateListener { valueAnimator -> mTarget.y = (valueAnimator.animatedValue as Float) }
        va.start()
        state = IEasyBehavior.STATE_HIDE
    }

    override fun setStartY(y: Float) {
        mStartY = y
    }

    override fun setMode(mode: Int) {
        mMode = mode
    }

    companion object {
        var MODE_TITLE = 0X01
        var MODE_BOTTOM = 0X02
        operator fun get(target: View): TranslateEasyBehaviorImpl {
            return TranslateEasyBehaviorImpl(target)
        }
    }

}