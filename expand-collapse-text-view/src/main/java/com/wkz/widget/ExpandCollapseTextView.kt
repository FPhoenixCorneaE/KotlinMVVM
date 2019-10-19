package com.wkz.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewTreeObserver
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

    private var mCollapsedLines: Int = 4
    private var mPromptExpanded: String = ""
    private var mPromptCollapsed: String = ""

    init {
        if (attrs != null) {
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ExpandCollapseTextView)
            try {
                mCollapsedLines = typedArray.getInteger(
                    R.styleable.ExpandCollapseTextView_ectv_collapsed_lines,
                    4
                )
                mPromptExpanded =
                    typedArray.getString(R.styleable.ExpandCollapseTextView_ectv_prompt_expanded)
                        .toString()
                mPromptCollapsed =
                    typedArray.getString(R.styleable.ExpandCollapseTextView_ectv_prompt_collapsed)
                        .toString()
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setContent(text: CharSequence?) {
        setText(text)
        maxLines = mCollapsedLines
        ellipsize = TextUtils.TruncateAt.END
        viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                viewTreeObserver.removeGlobalOnLayoutListener(this)
                val lineWidth = width - paddingLeft - paddingRight
                val ellipsize = TextUtils.ellipsize(
                    text,
                    paint,
                    (lineWidth * mCollapsedLines).toFloat(),
                    TextUtils.TruncateAt.END
                )

                if (TextUtils.equals(ellipsize, text)) {

                } else {

                }
            }
        })
    }
}