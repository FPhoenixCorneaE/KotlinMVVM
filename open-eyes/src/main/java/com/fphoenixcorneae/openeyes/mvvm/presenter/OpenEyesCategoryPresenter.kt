package com.fphoenixcorneae.openeyes.mvvm.presenter

import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesCategoryContract
import com.fphoenixcorneae.openeyes.mvvm.model.OpenEyesCategoryModel
import com.uber.autodispose.autoDisposable
import javax.inject.Inject

/**
 * @desc: 分类 Presenter
 */
class OpenEyesCategoryPresenter @Inject constructor() :
    BasePresenter<OpenEyesCategoryContract.View>(), OpenEyesCategoryContract.Presenter {

    @Inject
    lateinit var categoryModel: OpenEyesCategoryModel

    /**
     * 获取分类
     */
    override fun getCategoryData() {
        mView.showLoading()
        categoryModel.getCategoryData()
            .autoDisposable(mScopeProvider)
            .subscribe({ categoryList ->
                mView.apply {
                    showContent()
                    showCategory(categoryList)
                }
            }, { throwable ->
                mView.apply {
                    // 处理异常
                    showErrorMsg(throwable)
                }
            })
    }
}