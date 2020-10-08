package com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity

import android.view.View
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.framework.base.activity.Dagger2InjectionActivity
import com.fphoenixcorneae.titlebar.CommonTitleBar

abstract class OpenEyesBaseDagger2Activity<V : IView, P : IPresenter<V>> :
    Dagger2InjectionActivity<V, P>() {

    /**
     * 初始化标题栏
     */
    protected fun CommonTitleBar.init(
        onTitleBarClickListener: CommonTitleBar.OnTitleBarClickListener? = null
    ) {
        apply {
            setOnTitleBarClickListener(object : CommonTitleBar.OnTitleBarClickListener {
                override fun onClicked(v: View?, action: Int, extra: String?) {
                    when (action) {
                        CommonTitleBar.MotionAction.ACTION_LEFT_BUTTON -> finish()
                    }
                    onTitleBarClickListener?.onClicked(v, action, extra)
                }
            })
        }
    }
}