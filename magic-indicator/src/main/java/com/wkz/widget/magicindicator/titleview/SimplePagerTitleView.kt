package com.wkz.widget.magicindicator.titleview

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity

import androidx.appcompat.widget.AppCompatTextView

import com.wkz.widget.magicindicator.UIUtil
import com.wkz.widget.magicindicator.IMeasurablePagerTitleView


/**
 * 带文本的指示器标题
 */
open class SimplePagerTitleView(context: Context) : AppCompatTextView(context, null), IMeasurablePagerTitleView {
    var selectedColor: Int = 0
    var normalColor: Int = 0

    override val contentLeft: Int
        get() {
            val bound = Rect()
            var longestString = ""
            if (text.toString().contains("\n")) {
                val brokenStrings = text.toString().split("\\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                for (each in brokenStrings) {
                    if (each.length > longestString.length) {
                        longestString = each
                    }
                }
            } else {
                longestString = text.toString()
            }
            paint.getTextBounds(longestString, 0, longestString.length, bound)
            val contentWidth = bound.width()
            return left + width / 2 - contentWidth / 2
        }

    override val contentTop: Int
        get() {
            val metrics = paint.fontMetrics
            val contentHeight = metrics.bottom - metrics.top
            return (height / 2 - contentHeight / 2).toInt()
        }

    override val contentRight: Int
        get() {
            val bound = Rect()
            var longestString = ""
            if (text.toString().contains("\n")) {
                val brokenStrings = text.toString().split("\\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                for (each in brokenStrings) {
                    if (each.length > longestString.length) {
                        longestString = each
                    }
                }
            } else {
                longestString = text.toString()
            }
            paint.getTextBounds(longestString, 0, longestString.length, bound)
            val contentWidth = bound.width()
            return left + width / 2 + contentWidth / 2
        }

    override val contentBottom: Int
        get() {
            val metrics = paint.fontMetrics
            val contentHeight = metrics.bottom - metrics.top
            return (height / 2 + contentHeight / 2).toInt()
        }

    init {
        init(context)
    }

    private fun init(context: Context) {
        gravity = Gravity.CENTER
        val padding = UIUtil.dip2px(context, 10.0)
        setPadding(padding, 0, padding, 0)
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
    }

    override fun onSelected(index: Int, totalCount: Int) {
        setTextColor(selectedColor)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        setTextColor(normalColor)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {}

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {}
}
