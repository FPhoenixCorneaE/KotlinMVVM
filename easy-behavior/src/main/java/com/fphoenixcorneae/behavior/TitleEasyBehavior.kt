package com.fphoenixcorneae.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Behavior for TitleBar
 */
class TitleEasyBehavior(context: Context?, attrs: AttributeSet?) : AbstractEasyBehavior(context, attrs) {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (canInit) {
            iEasyBehavior = TranslateEasyBehaviorImpl[child]
            canInit = false
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onNestPreScrollInit(child: View) {
        if (isFirstMove) {
            isFirstMove = false
            iEasyBehavior!!.setStartY(child.y)
            iEasyBehavior!!.setMode(TranslateEasyBehaviorImpl.MODE_TITLE)
        }
    }
}