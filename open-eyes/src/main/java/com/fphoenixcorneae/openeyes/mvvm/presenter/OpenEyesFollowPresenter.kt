package com.fphoenixcorneae.openeyes.mvvm.presenter

import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesFollowContract
import com.fphoenixcorneae.openeyes.mvvm.model.OpenEyesFollowModel
import com.uber.autodispose.autoDisposable
import javax.inject.Inject

/**
 * @desc: 关注 Presenter
 */
class OpenEyesFollowPresenter @Inject constructor() : BasePresenter<OpenEyesFollowContract.View>(),
    OpenEyesFollowContract.Presenter {

    @Inject
    lateinit var followModel: OpenEyesFollowModel

    private var nextPageUrl: String? = null

    /**
     *  请求关注数据
     */
    override fun requestFollowList() {
        followModel.requestFollowList()
            .autoDisposable(mScopeProvider)
            .subscribe({ issue ->
                mView.apply {
                    nextPageUrl = issue.nextPageUrl
                    setFollowInfo(issue)
                }
            }, { throwable ->
                mView.apply {
                    //处理异常
                    showErrorMsg(throwable)
                }
            })
    }

    /**
     * 加载更多
     */
    override fun loadMoreData() {
        nextPageUrl?.let {
            followModel.loadMoreData(it)
                .autoDisposable(mScopeProvider)
                .subscribe({ issue ->
                    mView.apply {
                        nextPageUrl = issue.nextPageUrl
                        loadMoreFollowInfo(issue)
                    }
                }, { throwable ->
                    mView.apply {
                        //处理异常
                        showErrorMsg(throwable)
                    }
                })
        }
    }
}