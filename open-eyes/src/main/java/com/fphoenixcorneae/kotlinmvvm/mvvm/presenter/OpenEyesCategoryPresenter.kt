package com.fphoenixcorneae.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesCategoryContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesCategoryModel
import com.fphoenixcorneae.rxretrofit.network.exception.ExceptionHandle

/**
 * @desc: 分类 Presenter
 */
class OpenEyesCategoryPresenter : BasePresenter<OpenEyesCategoryContract.View>(), OpenEyesCategoryContract.Presenter {

    private val categoryModel: OpenEyesCategoryModel by lazy {
        OpenEyesCategoryModel()
    }

    /**
     * 获取分类
     */
    override fun getCategoryData() {
        mView?.showLoading()
        categoryModel.getCategoryData()
            .autoDisposable(mScopeProvider!!)
            .subscribe({ categoryList ->
                mView?.apply {
                    showContent()
                    showCategory(categoryList)
                }
            }, { t ->
                mView?.apply {
                    //处理异常
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }

            })
    }
}