package com.wkz.kotlinmvvm.mvp.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvp.contract.CategoryContract
import com.wkz.kotlinmvvm.mvp.model.CategoryModel
import com.wkz.rxretrofit.network.exception.ExceptionHandle

/**
 * @desc: 分类 Presenter
 */
class CategoryPresenter : BasePresenter<CategoryContract.View>(), CategoryContract.Presenter {

    private val categoryModel: CategoryModel by lazy {
        CategoryModel()
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