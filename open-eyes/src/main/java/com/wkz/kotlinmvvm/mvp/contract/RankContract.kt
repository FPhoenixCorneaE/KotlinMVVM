package com.wkz.kotlinmvvm.mvp.contract

import com.wkz.framework.base.IBaseView
import com.wkz.framework.base.IPresenter
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean


/**
 * @desc: 排行榜 契约类
 */
interface RankContract {

    interface View : IBaseView {
        /**
         * 设置排行榜的数据
         */
        fun setRankList(itemList: ArrayList<HomeBean.Issue.Item>)

        fun showError(errorMsg: String, errorCode: Int)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取 TabInfo
         */
        fun requestRankList(apiUrl: String)
    }
}