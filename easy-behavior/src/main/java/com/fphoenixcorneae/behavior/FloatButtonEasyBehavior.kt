package com.fphoenixcorneae.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Behavior for FloatButton
 */
class FloatButtonEasyBehavior(context: Context?, attrs: AttributeSet?) : AbstractEasyBehavior(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (canInit) {
            iEasyBehavior = ScaleEasyBehaviorImpl[child]
            canInit = false
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onNestPreScrollInit(child: View) {}
}