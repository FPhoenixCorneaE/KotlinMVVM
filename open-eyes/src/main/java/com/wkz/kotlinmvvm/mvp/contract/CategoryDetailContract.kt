package com.wkz.kotlinmvvm.mvp.contract

import com.wkz.framework.base.IView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean

/**
 * @desc: 分类详情 契约类
 */
interface CategoryDetailContract {

    interface View : IView {
        /**
         *  设置列表数据
         */
        fun setCateDetailList(itemList: ArrayList<HomeBean.Issue.Item>)

        fun showError(errorMsg: String)


    }

    interface Presenter : IPresenter<View> {

        fun getCategoryDetailList(id: Long)

        fun loadMoreData()
    }
}