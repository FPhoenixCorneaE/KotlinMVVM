package com.wkz.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

/**
 * @desc: 展开收起TextView
 * @date: 2019-10-18 19:14
 */
class ExpandCollapseTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    /** 展开状态 true：展开，false：收起 */
    private var mExpanded: Boolean = false
    /** 状态接口 */
    var mCallback: Callback? = null
    /** 源文字内容 */
    private var mSourceText: String? = ""
    /** 最多展示的行数 */
    var mMaxLineCount = 3
    /** 省略文字 */
    private var mEllipsizeText = "..."
    /** 展开文案文字 */
    var mExpandText = "全文"
    /** 展开文案文字颜色 */
    var mExpandTextColor: Int = 0x1C7FFD
    /** 收起文案文字 */
    var mCollapseText = "收起"
    /** 收起文案文字颜色 */
    var mCollapseTextColor: Int = 0x1C7FFD
    /** 是否支持收起功能 */
    var mCollapseEnable = false
    /** 是否添加下划线 */
    var mUnderlineEnable = true

    init {
        setLineSpacing(0.0f, 1.1f)
        movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 文字计算辅助工具
        if (mSourceText.isNullOrEmpty()) {
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
        // StaticLayout对象
        val sl = StaticLayout(
            mSourceText,
            paint,
            measuredWidth - paddingLeft - paddingRight,
            Layout.Alignment.ALIGN_CENTER,
            lineSpacingMultiplier,
            lineSpacingExtra,
            true
        )
        // 总计行数
        var lineCount = sl.lineCount
        // 总行数大于最大行数
        if (lineCount > mMaxLineCount) {
            if (mExpanded) {
                text = mSourceText
                // 是否支持收起功能
                if (mCollapseEnable) {
                    // 收起文案和源文字组成的新的文字
                    val newEndLineText = mSourceText + mCollapseText
                    // 收起文案和源文字组成的新的文字
                    val spannableString = SpannableString(newEndLineText)
                    // 给收起设成监听
                    spannableString.setSpan(
                        object : ClickableSpan() {
                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                // 给收起设置颜色
                                ds.color = mCollapseTextColor
                                // 是否给收起添加下划线
                                ds.isUnderlineText = mUnderlineEnable
                            }

                            override fun onClick(widget: View) {
                                if (mCallback != null) {
                                    mCallback!!.onCollapseClick()
                                }
                            }
                        },
                        mSourceText!!.length,
                        newEndLineText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    // 最终显示的文字
                    text = spannableString
                }
                if (mCallback != null) {
                    mCallback!!.onExpand()
                }
            } else {
                lineCount = mMaxLineCount
                // 省略文字和展开文案的宽度
                val dotWidth = paint.measureText(mEllipsizeText + mExpandText)
                // 找出显示最后一行的文字
                val start = sl.getLineStart(lineCount - 1)
                val end = sl.getLineEnd(lineCount - 1)
                val lineText = mSourceText!!.substring(start, end)
                // 将第最后一行最后的文字替换为 ellipsizeText和expandText
                var endIndex = 0
                for (i in lineText.length - 1 downTo 0) {
                    val str = lineText.substring(i, lineText.length)
                    // 找出文字宽度大于 ellipsizeText 的字符
                    if (paint.measureText(str) >= dotWidth) {
                        endIndex = i
                        break
                    }
                }
                // 新的文字
                val newEndLineText = mSourceText!!.substring(0, start) +
                        lineText.substring(
                            0,
                            endIndex
                        ) +
                        mEllipsizeText + mExpandText
                // 全部文字
                val spannableString = SpannableString(newEndLineText)
                // 给查看全文设成监听
                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            // 给查看全文设置颜色
                            ds.color = mExpandTextColor
                            // 是否给查看全文添加下划线
                            ds.isUnderlineText = mUnderlineEnable
                        }

                        override fun onClick(widget: View) {
                            if (mCallback != null) {
                                mCallback!!.onExpandClick()
                            }
                        }
                    },
                    newEndLineText.length - mExpandText.length,
                    newEndLineText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // 最终显示的文字
                text = spannableString
                if (mCallback != null) {
                    mCallback!!.onCollapse()
                }
            }
        } else {
            text = mSourceText
            if (mCallback != null) {
                mCallback!!.onLoss()
            }
        }
        // 重新计算高度
        var lineHeight = 0
        val lineBound = Rect()
        sl.getLineBounds(0, lineBound)
        lineHeight = lineBound.height() * lineCount
        // 展开状态下,若最后一行文字占满控件宽度,则需要增加一行显示收起文案
        if (mExpanded) {
            // 收起文案的宽度
            val collapseWidth = paint.measureText(mCollapseText)
            // 最后一行文字的宽度
            val lastLineWidth = sl.getLineWidth(lineCount - 1)
            if (lastLineWidth + collapseWidth >= measuredWidth - paddingLeft - paddingRight) {
                // 收起文案的高度
                val paint = Paint()
                paint.textSize = textSize
                val collapseHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
                lineHeight += collapseHeight.toInt()
            }
        }
        lineHeight = (paddingTop + paddingBottom + lineHeight * lineSpacingMultiplier).toInt()
        setMeasuredDimension(measuredWidth, lineHeight)
    }

    /**
     * 设置要显示的文字以及状态
     * @param text
     * @param expanded true：展开，false：收起
     * @param callback
     */
    fun setText(text: String, expanded: Boolean, callback: Callback) {
        mSourceText = text
        mExpanded = expanded
        mCallback = callback

        // 设置要显示的文字，这一行必须要，否则 onMeasure 宽度测量不正确
        setText(text)
    }

    /**
     * 展开收起状态变化
     * @param expanded
     */
    fun changeExpandedState(expanded: Boolean) {
        mExpanded = expanded
        requestLayout()
    }
}

interface Callback {
    /**
     * 展开状态
     */
    fun onExpand()

    /**
     * 收起状态
     */
    fun onCollapse()

    /**
     * 行数小于最小行数，不满足展开或者收起条件
     */
    fun onLoss()

    /**
     * 点击全文
     */
    fun onExpandClick()

    /**
     * 点击收起
     */
    fun onCollapseClick()
}