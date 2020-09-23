package com.fphoenixcorneae.kotlinmvvm.mvvm.contract

import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean


/**
 * @desc: 排行榜 契约类
 */
interface OpenEyesRankContract {

    interface View : IView {
        /**
         * 设置排行榜的数据
         */
        fun setRankList(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取 TabInfo
         */
        fun requestRankList(apiUrl: String)
    }
}