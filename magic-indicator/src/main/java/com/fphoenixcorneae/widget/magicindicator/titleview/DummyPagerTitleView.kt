package com.fphoenixcorneae.widget.magicindicator.titleview

import android.content.Context
import android.view.View

import com.fphoenixcorneae.widget.magicindicator.IPagerTitleView

/**
 * 空指示器标题，用于只需要指示器而不需要title的需求
 */
class DummyPagerTitleView(context: Context) : View(context), IPagerTitleView {

    override fun onSelected(index: Int, totalCount: Int) {}

    override fun onDeselected(index: Int, totalCount: Int) {}

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {}

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {}
}
