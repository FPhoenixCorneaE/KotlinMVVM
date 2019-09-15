package com.wkz.kotlinmvvm.mvvm.contract

import com.wkz.framework.base.IView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean


/**
 * @desc: 排行榜 契约类
 */
interface OpenEyesRankContract {

    interface View : IView {
        /**
         * 设置排行榜的数据
         */
        fun setRankList(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>)

        fun showError(errorMsg: String, errorCode: Int)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取 TabInfo
         */
        fun requestRankList(apiUrl: String)
    }
}