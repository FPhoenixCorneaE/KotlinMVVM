package com.fphoenixcorneae.openeyes.mvvm.contract

import com.fphoenixcorneae.framework.base.IView
import com.fphoenixcorneae.framework.base.IPresenter
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesCategoryBean

/**
 * desc: 分类 契约类
 */
interface OpenEyesCategoryContract {

    interface View : IView {
        /**
         * 显示分类的信息
         */
        fun showCategory(categoryList: ArrayList<OpenEyesCategoryBean>)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取分类的信息
         */
        fun getCategoryData()
    }
}