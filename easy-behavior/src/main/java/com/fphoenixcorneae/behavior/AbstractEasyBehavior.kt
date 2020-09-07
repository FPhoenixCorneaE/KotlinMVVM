package com.fphoenixcorneae.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import kotlin.math.abs

/**
 * Base Behavior
 */
abstract class AbstractEasyBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attrs) {
    protected val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    protected var isFirstMove = true
    protected var canInit = true
    protected var iEasyBehavior: IEasyBehavior? = null
    /**
     * on Scroll Started
     *
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes
     * @return
     */
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View,
                                     directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View,
                                   dx: Int, dy: Int, consumed: IntArray) {
        onNestPreScrollInit(child)
        if (abs(dy) > 2) {
            if (dy < 0) {
                if (iEasyBehavior!!.state == IEasyBehavior.STATE_HIDE) {
                    iEasyBehavior!!.show()
                }
            } else if (dy > 0) {
                if (iEasyBehavior!!.state == IEasyBehavior.STATE_SHOW) {
                    iEasyBehavior!!.hide()
                }
            }
        }
    }

    protected abstract fun onNestPreScrollInit(child: View)

    fun show() {
        iEasyBehavior!!.show()
    }

    fun hide() {
        iEasyBehavior!!.hide()
    }

    companion object {
        @JvmStatic
        fun from(view: View): AbstractEasyBehavior {
            val params = view.layoutParams
            require(params is CoordinatorLayout.LayoutParams) {
                "The view is not a child of CoordinatorLayout"
            }
            val behavior = params.behavior
            require(behavior is AbstractEasyBehavior) {
                "The view is not associated with AbstractEasyBehavior"
            }
            return behavior
        }
    }

}