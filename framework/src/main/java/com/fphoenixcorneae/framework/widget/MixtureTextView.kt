package com.fphoenixcorneae.framework.widget

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @desc：图文混排、文字环绕图片等效果
 * @date：2020-06-17 17:06
 */
class MixtureTextView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    private var mStaticLayout: StaticLayout? = null

    /**
     * 行高
     */
    private var mLineHeight = 0
    private var mTextColor = Color.BLACK
    private var mTextSize = sp2px(14)
    private var mText: CharSequence? = null
    private val mTextPaint = TextPaint()
    private var mDestRects: MutableList<List<Rect>> = ArrayList()
    private var mCorYs: List<Int>? = null
    private val mCorYHashes = HashSet<Int>()
    private var mMaxHeight = 0
    private var mHeightMeasureSpec = 0
    private var mOriginHeightMeasureMode = 0
    private var mHeightReMeasureSpec = 0
    private var mNeedReMeasure = false
    private var mNeedRenderText: Boolean
    private var mMinHeight = 0
    private val mViewBounds: MutableMap<Int, Point> = HashMap()
    private fun readAttrs(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, ATTRS)
        mTextSize = ta.getDimensionPixelSize(INDEX_ATTR_TEXT_SIZE, mTextSize)
        mTextColor = ta.getColor(INDEX_ATTR_TEXT_COLOR, mTextColor)
        mText = ta.getString(INDEX_ATTR_TEXT)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!mNeedRenderText) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        mHeightMeasureSpec = heightMeasureSpec
        mTextPaint.textSize = mTextSize.toFloat()
        calculateLineHeight()
        if (mNeedReMeasure) {
            super.onMeasure(widthMeasureSpec, mHeightReMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun calculateLineHeight() {
        mStaticLayout = generateLayout(mText, 0)
        mLineHeight = mStaticLayout!!.getLineBottom(0) - mStaticLayout!!.getLineTop(0)
    }

    private var mFirstInLayout = true
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mFirstInLayout) {
            mOriginHeightMeasureMode = MeasureSpec.getMode(mHeightMeasureSpec)
            mFirstInLayout = false
            mMinHeight = measuredHeight
        }
        super.onLayout(changed, l, t, r, b)
        if (!mNeedRenderText) {
            return
        }
        allYCors
    }

    private fun tryDraw(canvas: Canvas?): Boolean {
        val kidding = canvas == null
        val lineHeight = mLineHeight
        val destRects: List<List<Rect>> = mDestRects
        var start = 0
        var lineSum = 0
        val fullSize = mText!!.length
        for (i in destRects.indices) {
            val rs = destRects[i]
            val r = rs[0]
            val rectWidth = r.width()
            val rectHeight = r.height()
            mStaticLayout = generateLayout(mText!!.substring(start), rectWidth)
            var lineCount = rectHeight / lineHeight
            lineCount = min(mStaticLayout!!.lineCount, lineCount)
            if (!kidding) {
                canvas!!.save()
                canvas.translate(r.left.toFloat(), r.top.toFloat())
                canvas.clipRect(0, 0, r.width(), mStaticLayout!!.getLineBottom(lineCount - 1) - mStaticLayout!!.getLineTop(0))
                mStaticLayout!!.draw(canvas)
                canvas.restore()
            }
            start += mStaticLayout!!.getLineEnd(lineCount - 1)
            lineSum += lineCount
            if (start >= fullSize) {
                break
            }
        }
        if (kidding) {
            mMaxHeight += lineSum * lineHeight
            if (mMaxHeight > mMinHeight && height != mMaxHeight && mOriginHeightMeasureMode != MeasureSpec.EXACTLY) {
                mHeightReMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY)
                mNeedReMeasure = true
                requestLayout()
                return true
            }
        }
        return false
    }//获得所有的y轴坐标
    //排序

    /**
     * 获取所有的y坐标
     */
    private val allYCors: Unit
        get() {
            val lineHeight = mLineHeight
            val corYSet: MutableSet<Int> = mCorYHashes
            corYSet.clear()
            mViewBounds.clear()

            //获得所有的y轴坐标
            val cCount = childCount
            for (i in 0 until cCount) {
                val c = getChildAt(i)
                if (c.visibility == View.GONE) {
                    continue
                }
                var availableTop = c.top - paddingTop
                availableTop = availableTop / lineHeight * lineHeight
                val top: Int = availableTop + paddingTop
                corYSet.add(top)
                var bottom = c.bottom
                var availableBottom = bottom - paddingTop
                availableBottom = if (availableBottom % lineHeight == 0) availableBottom else (availableBottom / lineHeight + 1) * lineHeight
                bottom = availableBottom + paddingTop
                corYSet.add(bottom)
                mViewBounds[i] = Point(top, bottom)
            }
            corYSet.add(paddingTop)
            if (mOriginHeightMeasureMode == MeasureSpec.EXACTLY) {
                corYSet.add(height)
            } else {
                corYSet.add(Int.MAX_VALUE)
            }
            //排序
            val corYs: List<Int> = ArrayList(corYSet)
            Collections.sort(corYs)
            mCorYs = corYs
        }

    override fun dispatchDraw(canvas: Canvas) {
        mMaxHeight = paddingBottom + paddingTop
        initAllNeedRenderRect()
        val skipDraw = tryDraw(null)
        if (skipDraw) {
            return
        }
        tryDraw(canvas)
        super.dispatchDraw(canvas)
    }

    private fun initAllNeedRenderRect() {
        val lineHeight = mLineHeight
        val destRects = mDestRects
        val corYs = mCorYs
        destRects.clear()
        val minLeft = paddingLeft
        val maxRight = width - paddingRight

        // find rect between y1 and y2
        var viewRectBetween2Y: MutableList<Rect>
        for (i in 0 until corYs!!.size - 1) {
            val y1 = corYs[i]
            val y2 = corYs[i + 1]
            viewRectBetween2Y = ArrayList()
            val rs = calculateViewYBetween(y1, y2)
            var leftFirst: Rect?
            when (rs.size) {
                0 -> viewRectBetween2Y.add(Rect(minLeft, y1, maxRight, y2))
                1 -> {
                    leftFirst = rs[0]
                    //添加第一个Rect
                    tryAddFirst(leftFirst, viewRectBetween2Y, y1, y2, minLeft)
                    tryAddLast(leftFirst, viewRectBetween2Y, y1, y2, maxRight)
                }
                else -> {
                    //add first
                    leftFirst = rs[0]
                    tryAddFirst(leftFirst, viewRectBetween2Y, y1, y2, minLeft)
                    //add mid
                    var j = 0
                    while (j < rs.size - 1) {
                        val ra = rs[j]
                        val rb = rs[j + 1]
                        if (ra.right < rb.left) {
                            viewRectBetween2Y.add(Rect(ra.right, y1, rb.left, y2))
                        }
                        j++
                    }
                    //add last
                    val lastRect = rs[rs.size - 1]
                    tryAddLast(lastRect, viewRectBetween2Y, y1, y2, maxRight)
                }
            }
            destRects.add(viewRectBetween2Y)
        }

        //split
        val bak: MutableList<List<Rect>> = ArrayList(destRects)
        val destRectSize = destRects.size
        //索引增量
        var inc = 0
        for (i in 0 until destRectSize) {
            val rs = destRects[i]
            if (rs.size > 1) {
                var index = inc + i
                bak.remove(rs)
                inc--
                val rect1 = rs[0]
                val lh = rect1.height() / lineHeight
                mMaxHeight -= lh * (rs.size - 1) * lineHeight
                for (k in 0 until lh) {
                    for (j in rs.indices) {
                        inc++
                        bak.add(index++, listOf(Rect(
                                rs[j].left,
                                rect1.top + lineHeight * k,
                                rs[j].right,
                                rect1.top + lineHeight * k + lineHeight)))
                    }
                }
            }
        }
        mDestRects = bak
    }

    private fun tryAddLast(leftFirst: Rect?, viewRectBetween2Y: MutableList<Rect>, y1: Int, y2: Int, maxRight: Int) {
        if (leftFirst!!.right < maxRight) {
            viewRectBetween2Y.add(Rect(leftFirst.right, y1, maxRight, y2))
        }
    }

    private fun tryAddFirst(leftFirst: Rect?, viewRectBetween2Y: MutableList<Rect>, y1: Int, y2: Int, minLeft: Int) {
        if (leftFirst!!.left > minLeft) {
            viewRectBetween2Y.add(Rect(minLeft, y1, leftFirst.left, y2))
        }
    }

    private fun generateLayout(text: CharSequence?, width: Int): StaticLayout {
        return StaticLayout(text, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false)
    }

    private fun setTextSize(unit: Int, size: Int) {
        when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> mTextSize = size
            TypedValue.COMPLEX_UNIT_DIP -> mTextSize = dp2px(size)
            TypedValue.COMPLEX_UNIT_SP -> mTextSize = sp2px(size)
        }
        mTextPaint.textSize = mTextSize.toFloat()
        requestLayout()
        invalidate()
    }

    /**
     * 计算包含在y1到y2间的矩形区域
     *
     * @param y1
     * @param y2
     * @return
     */
    private fun calculateViewYBetween(y1: Int, y2: Int): List<Rect> {
        var rs: MutableList<Rect> = ArrayList()
        var tmp: Rect? = null
        val childCount = childCount
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            val p = mViewBounds[i]
            val top = p!!.x
            val bottom = p.y
            if (top <= y1 && bottom >= y2) {
                tmp = Rect(v.left, y1, v.right, y2)
                rs.add(tmp)
            }
        }
        rs.sortWith(Comparator { lhs, rhs ->
            when {
                lhs.left > rhs.left -> {
                    1
                }
                else -> -1
            }
        })
        when {
            rs.size >= 2 -> {
                val res: MutableList<Rect> = ArrayList(rs)
                var pre = rs[0]
                var next = rs[1]
                //合并
                loop@ for (i in 1 until rs.size) {
                    //if相交
                    when {
                        Rect.intersects(pre, next) -> {
                            val left = min(pre.left, next.left)
                            val right = max(pre.right, next.right)
                            res.remove(pre)
                            res.remove(next)
                            res.add(Rect(left, y1, right, y2))
                            when {
                                res.size >= 2 -> {
                                    pre = rs[0]
                                    next = rs[1]
                                }
                                else -> {
                                    break@loop
                                }
                            }
                        }
                        else -> {
                            when {
                                res.size - i >= 2 -> {
                                    pre = next
                                    next = rs[i + 1]
                                }
                                else -> {
                                    break@loop
                                }
                            }
                        }
                    }
                }
                rs = res
            }
        }
        return rs
    }

    private fun sp2px(spVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal.toFloat(), resources.displayMetrics).toInt()
    }

    private fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal.toFloat(), resources.displayMetrics).toInt()
    }

    var textSize: Int
        get() = mTextSize
        set(pxSize) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, pxSize)
        }

    var textColor: Int
        get() = mTextColor
        set(color) {
            mTextPaint.color = color
            mTextColor = color
            invalidate()
        }

    var text: CharSequence?
        get() = mText
        set(text) {
            if (TextUtils.isEmpty(text)) {
                mNeedRenderText = false
                requestLayout()
                return
            }
            mNeedRenderText = true
            mText = text
            requestLayout()
            invalidate()
        }

    companion object {
        private val ATTRS = intArrayOf(
                R.attr.textSize,
                R.attr.textColor,
                R.attr.text
        )
        private const val INDEX_ATTR_TEXT_SIZE = 0
        private const val INDEX_ATTR_TEXT_COLOR = 1
        private const val INDEX_ATTR_TEXT = 2
    }

    init {
        readAttrs(context, attrs)
        // just fot text
        if (TextUtils.isEmpty(mText)) {
            mText = " "
        }
        mNeedRenderText = true
        mTextPaint.isDither = true
        mTextPaint.isAntiAlias = true
        mTextPaint.color = mTextColor
    }
}