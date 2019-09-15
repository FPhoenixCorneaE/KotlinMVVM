package com.wkz.kotlinmvvm.mvvm.contract

import com.wkz.framework.base.IView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesTabInfoBean

/**
 * @desc:热门标签 契约类
 */
interface OpenEyesHotTabContract {

    interface View : IView {
        /**
         * 设置 TabInfo
         */
        fun setTabInfo(tabInfoBean: OpenEyesTabInfoBean)

        fun showError(errorMsg: String, errorCode: Int)
    }


    interface Presenter : IPresenter<View> {
        /**
         * 获取 TabInfo
         */
        fun getTabInfo()
    }
}