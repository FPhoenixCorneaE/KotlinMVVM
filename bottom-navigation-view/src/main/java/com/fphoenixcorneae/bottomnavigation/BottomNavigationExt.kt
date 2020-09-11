package com.fphoenixcorneae.bottomnavigation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView

fun ImageView.changeImageColorFilter(fromColor: Int, toColor: Int) =
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).run {
        duration = 150
        addUpdateListener { animator -> setColorFilter(animator.animatedValue as Int) }
        start()
    }

fun View.changeViewBackgroundColor(fromColor: Int, toColor: Int) =
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).run {
        duration = 150
        addUpdateListener { animator -> setBackgroundColor(animator.animatedValue as Int) }
        start()
    }


fun View.changeViewTopPadding(fromPadding: Float, toPadding: Float) =
    ValueAnimator.ofFloat(fromPadding, toPadding).run {
        duration = 150
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            setPadding(paddingLeft, animatedValue.toInt(), paddingRight, paddingBottom)
        }
        start()
    }


fun View.changeRightPadding(fromPadding: Float, toPadding: Float) =
    ValueAnimator.ofFloat(fromPadding, toPadding).run {
        duration = 150
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            setPadding(paddingLeft, paddingTop, animatedValue.toInt(), paddingBottom)
        }
        start()
    }

fun TextView.changeTextSize(from: Float, to: Float) =
    ValueAnimator.ofFloat(from, to).run {
        duration = 150
        addUpdateListener { animator ->
            setTextSize(TypedValue.COMPLEX_UNIT_PX, animator.animatedValue as Float)
        }
        start()
    }

fun TextView.changeTextColor(fromColor: Int, toColor: Int) =
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).run {
        duration = 150
        addUpdateListener { animator -> setTextColor(animator.animatedValue as Int) }
        start()
    }

fun View.getActionbarSize(): Int = kotlin.run {
    var actionbarSize = -1
    val typedValue = TypedValue()
    if (context.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
        actionbarSize = TypedValue.complexToDimensionPixelSize(
            typedValue.data,
            context.resources.displayMetrics
        )
    }
    actionbarSize
}