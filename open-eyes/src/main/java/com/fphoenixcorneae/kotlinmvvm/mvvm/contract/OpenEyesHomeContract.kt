package com.fphoenixcorneae.kotlinmvvm.mvvm.contract

import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc:首页精选 契约类
 */

interface OpenEyesHomeContract {

    interface View : IView {

        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(homeBean: OpenEyesHomeBean)

        /**
         * 设置加载更多的数据
         */
        fun setMoreData(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>)

    }

    interface Presenter : IPresenter<View> {

        /**
         * 获取首页精选数据
         */
        fun requestHomeData(num: Int)

        /**
         * 加载更多数据
         */
        fun loadMoreData()
    }
}
