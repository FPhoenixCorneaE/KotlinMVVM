package com.fphoenixcorneae.openeyes.mvvm.contract

import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean

/**
 * @desc: 搜索 契约类
 */
interface OpenEyesSearchContract {

    interface View : IView {
        /**
         * 设置热门关键词数据
         */
        fun setHotWordData(hotWords: ArrayList<String>)

        /**
         * 设置搜索关键词返回的结果
         */
        fun setSearchResult(issue: OpenEyesHomeBean.Issue)

        /**
         * 关闭软件盘
         */
        fun closeSoftKeyboard()

        /**
         * 设置空 View
         */
        fun setEmptyView()
    }


    interface Presenter : IPresenter<View> {
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