package com.wkz.kotlinmvvm.mvp.contract

import com.wkz.framework.base.IBaseModel
import com.wkz.framework.base.IBaseView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean

/**
 * @desc: 分类详情 契约类
 */
interface CategoryDetailContract {

    interface View : IBaseView {
        /**
         *  设置列表数据
         */
        fun setCateDetailList(itemList: ArrayList<HomeBean.Issue.Item>)

        fun showError(errorMsg: String)


    }

    interface Presenter : IPresenter<View, IBaseModel> {

        fun getCategoryDetailList(id: Long)

        fun loadMoreData()
    }
}