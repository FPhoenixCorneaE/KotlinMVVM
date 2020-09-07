package com.fphoenixcorneae.widget.magicindicator


import com.fphoenixcorneae.widget.magicindicator.model.PositionData

/**
 * 抽象的viewpager指示器，适用于CommonNavigator
 */
interface IPagerIndicator {
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageSelected(position: Int)

    fun onPageScrollStateChanged(state: Int)

    fun onPositionDataProvide(dataList: List<PositionData>)
}
