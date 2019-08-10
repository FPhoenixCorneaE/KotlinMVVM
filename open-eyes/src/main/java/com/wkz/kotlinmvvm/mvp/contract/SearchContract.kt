package com.wkz.kotlinmvvm.mvp.contract

import com.wkz.framework.base.IBaseModel
import com.wkz.framework.base.IBaseView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean

/**
 * @desc: 搜索 契约类
 */
interface SearchContract {

    interface View : IBaseView {
        /**
         * 设置热门关键词数据
         */
        fun setHotWordData(string: ArrayList<String>)

        /**
         * 设置搜索关键词返回的结果
         */
        fun setSearchResult(issue: HomeBean.Issue)

        /**
         * 关闭软件盘
         */
        fun closeSoftKeyboard()

        /**
         * 设置空 View
         */
        fun setEmptyView()


        fun showError(errorMsg: String, errorCode: Int)
    }


    interface Presenter : IPresenter<View, IBaseModel> {
        /**
         * 获取热门关键字的数据
         */
        fun requestHotWordData()

        /**
         * 查询搜索
         */
        fun querySearchData(words: String)

        /**
         * 加载更多
         */
        fun loadMoreData()
    }
}