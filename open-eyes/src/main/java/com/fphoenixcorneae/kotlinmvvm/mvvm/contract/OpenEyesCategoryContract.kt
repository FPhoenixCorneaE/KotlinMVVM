package com.fphoenixcorneae.kotlinmvvm.mvvm.contract

import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesCategoryBean

/**
 * desc: 分类 契约类
 */
interface OpenEyesCategoryContract {

    interface View : IView {
        /**
         * 显示分类的信息
         */
        fun showCategory(categoryList: ArrayList<OpenEyesCategoryBean>)

        /**
         * 显示错误信息
         */
        fun showError(errorMsg: String, errorCode: Int)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取分类的信息
         */
        fun getCategoryData()
    }
}