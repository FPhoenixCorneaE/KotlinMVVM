package com.wkz.widget.magicindicator

/**
 * 抽象的ViewPager导航器
 */
interface IPagerNavigator {

    /**
     * ViewPager的onPageScrolled
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    /**
     * ViewPager的onPageSelected
     *
     * @param position
     */
    fun onPageSelected(position: Int)

    /**
     * ViewPager的onPageScrollStateChanged
     *
     * @param state
     */
    fun onPageScrollStateChanged(state: Int)

    /**
     * 当IPagerNavigator被添加到MagicIndicator时调用
     */
    fun onAttachToMagicIndicator()

    /**
     * 当IPagerNavigator从MagicIndicator上移除时调用
     */
    fun onDetachFromMagicIndicator()

    /**
     * ViewPager内容改变时需要先调用此方法，自定义的IPagerNavigator应当遵守此约定
     */
    fun notifyDataSetChanged()
}
