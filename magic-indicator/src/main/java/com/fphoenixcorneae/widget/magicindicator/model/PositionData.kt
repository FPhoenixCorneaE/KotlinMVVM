package com.fphoenixcorneae.widget.magicindicator.model

/**
 * 保存指示器标题的坐标
 */
class PositionData {
    var mLeft: Int = 0
    var mTop: Int = 0
    var mRight: Int = 0
    var mBottom: Int = 0
    var mContentLeft: Int = 0
    var mContentTop: Int = 0
    var mContentRight: Int = 0
    var mContentBottom: Int = 0

    fun width(): Int {
        return mRight - mLeft
    }

    fun height(): Int {
        return mBottom - mTop
    }

    fun contentWidth(): Int {
        return mContentRight - mContentLeft
    }

    fun contentHeight(): Int {
        return mContentBottom - mContentTop
    }

    fun horizontalCenter(): Int {
        return mLeft + width() / 2
    }

    fun verticalCenter(): Int {
        return mTop + height() / 2
    }
}
