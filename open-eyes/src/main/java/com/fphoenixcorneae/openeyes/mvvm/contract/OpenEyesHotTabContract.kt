package com.fphoenixcorneae.openeyes.mvvm.contract

import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesTabInfoBean

/**
 * @desc:热门标签 契约类
 */
interface OpenEyesHotTabContract {

    interface View : IView {
        /**
         * 设置 TabInfo
         */
        fun setTabInfo(tabInfoBean: OpenEyesTabInfoBean)
    }


    interface Presenter : IPresenter<View> {
        /**
         * 获取 TabInfo
         */
        fun getTabInfo()
    }
}