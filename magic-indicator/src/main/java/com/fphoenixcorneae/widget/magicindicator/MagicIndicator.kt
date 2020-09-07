package com.fphoenixcorneae.widget.magicindicator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * 整个框架的入口，核心
 */
class MagicIndicator : FrameLayout {
    var navigator: IPagerNavigator? = null
        set(navigator) {
            if (this.navigator === navigator) {
                return
            }
            if (this.navigator != null) {
                this.navigator!!.onDetachFromMagicIndicator()
            }
            field = navigator
            removeAllViews()
            if (this.navigator is View) {
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                addView(this.navigator as View?, lp)
                this.navigator!!.onAttachToMagicIndicator()
            }
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (navigator != null) {
            navigator!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    fun onPageSelected(position: Int) {
        if (navigator != null) {
            navigator!!.onPageSelected(position)
        }
    }

    fun onPageScrollStateChanged(state: Int) {
        if (navigator != null) {
            navigator!!.onPageScrollStateChanged(state)
        }
    }
}
