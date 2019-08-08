package com.wkz.bottomnavigation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView

object BottomNavigationUtils {

    fun changeImageColorFilter(image: ImageView, fromColor: Int, toColor: Int) {
        val imageColorChangeAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        imageColorChangeAnimation.addUpdateListener { animator -> image.setColorFilter(animator.animatedValue as Int) }
        imageColorChangeAnimation.duration = 150
        imageColorChangeAnimation.start()
    }

    fun changeViewBackgroundColor(view: View, fromColor: Int, toColor: Int) {
        val imageColorChangeAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        imageColorChangeAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        imageColorChangeAnimation.duration = 150
        imageColorChangeAnimation.start()
    }

    fun changeViewTopPadding(view: View, fromPadding: Float, toPadding: Float) {
        val animator = ValueAnimator.ofFloat(fromPadding, toPadding)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            view.setPadding(view.paddingLeft,
                    animatedValue.toInt(),
                    view.paddingRight,
                    view.paddingBottom)
        }
        animator.start()
    }

    fun changeRightPadding(view: View, fromPadding: Float, toPadding: Float) {
        val animator = ValueAnimator.ofFloat(fromPadding, toPadding)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            view.setPadding(view.paddingLeft,
                    view.paddingTop,
                    animatedValue.toInt(),
                    view.paddingBottom)
        }
        animator.start()
    }

    fun changeViewLeftPadding(view: View, fromMargin: Float, toMargin: Float) {
        val animator = ValueAnimator.ofFloat(fromMargin, toMargin)
        animator.duration = 3000
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            view.setPadding(animatedValue.toInt(),
                    view.paddingTop,
                    view.paddingRight,
                    view.paddingBottom)
            view.requestLayout()
        }
        animator.start()
    }

    fun changeTextSize(textView: TextView, from: Float, to: Float) {
        val textSizeChangeAnimator = ValueAnimator.ofFloat(from, to)
        textSizeChangeAnimator.duration = 150
        textSizeChangeAnimator.addUpdateListener { valueAnimator -> textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueAnimator.animatedValue as Float) }
        textSizeChangeAnimator.start()
    }

    fun changeTextColor(textView: TextView, fromColor: Int, toColor: Int) {
        val changeTextColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        changeTextColorAnimation.duration = 150
        changeTextColorAnimation.addUpdateListener { animator -> textView.setTextColor(animator.animatedValue as Int) }
        changeTextColorAnimation.start()
    }

    fun getActionbarSize(context: Context): Int {
        var actionbarSize = -1
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionbarSize = TypedValue.complexToDimensionPixelSize(typedValue.data, context.resources.displayMetrics)
        }
        return actionbarSize
    }

    fun pxToDp(px: Int, context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}
