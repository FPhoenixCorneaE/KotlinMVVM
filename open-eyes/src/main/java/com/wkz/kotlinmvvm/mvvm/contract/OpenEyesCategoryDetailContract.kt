package com.wkz.kotlinmvvm.mvvm.contract

import com.wkz.framework.base.IView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 分类详情 契约类
 */
interface OpenEyesCategoryDetailContract {

    interface View : IView {
        /**
         *  设置列表数据
         */
        fun setCateDetailList(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>)

        fun showError(errorMsg: String)


    }

    interface Presenter : IPresenter<View> {

        fun getCategoryDetailList(id: Long)

        fun loadMoreData()
    }
}