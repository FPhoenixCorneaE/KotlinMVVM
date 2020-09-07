package com.fphoenixcorneae.framework.web

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.coolindicator.sdk.CoolIndicator
import com.just.agentweb.AgentWebUtils
import com.just.agentweb.BaseIndicatorView
import com.fphoenixcorneae.framework.R

class CoolIndicatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : BaseIndicatorView(context, attrs, defStyleAttr) {
    private var mCoolIndicator: CoolIndicator? = null
    override fun show() {
        this.visibility = View.VISIBLE
        mCoolIndicator!!.start()
    }

    override fun setProgress(newProgress: Int) {}
    override fun hide() {
        mCoolIndicator!!.complete()
    }

    override fun offerLayoutParams(): LayoutParams {
        return LayoutParams(-1, AgentWebUtils.dp2px(context, 3f))
    }

    init {
        mCoolIndicator = CoolIndicator.create(context as Activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCoolIndicator!!.progressDrawable = context.getResources().getDrawable(
                R.drawable.default_drawable_indicator,
                context.getTheme()
            )
        } else {
            mCoolIndicator!!.progressDrawable = context.getResources().getDrawable(
                R.drawable.default_drawable_indicator
            )
        }
        this.addView(mCoolIndicator, offerLayoutParams())
    }
}