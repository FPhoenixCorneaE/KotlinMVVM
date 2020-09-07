package com.fphoenixcorneae.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Behavior for NavigationBar
 */
class BottomEasyBehavior(context: Context?, attrs: AttributeSet?) : AbstractEasyBehavior(context, attrs) {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (canInit) {
            canInit = false
            iEasyBehavior = TranslateEasyBehaviorImpl[child]
            iEasyBehavior!!.setStartY(child.y)
            iEasyBehavior!!.setMode(TranslateEasyBehaviorImpl.MODE_BOTTOM)
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onNestPreScrollInit(child: View) {}
}