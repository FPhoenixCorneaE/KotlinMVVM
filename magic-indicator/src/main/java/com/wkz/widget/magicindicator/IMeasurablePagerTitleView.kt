package com.wkz.widget.magicindicator

/**
 * 可测量内容区域的指示器标题
 */
interface IMeasurablePagerTitleView : IPagerTitleView {
    val contentLeft: Int

    val contentTop: Int

    val contentRight: Int

    val contentBottom: Int
}
