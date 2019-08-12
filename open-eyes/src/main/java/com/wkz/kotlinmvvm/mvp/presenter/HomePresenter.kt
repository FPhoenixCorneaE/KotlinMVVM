package com.wkz.kotlinmvvm.mvp.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvp.contract.HomeContract
import com.wkz.kotlinmvvm.mvp.model.HomeModel
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.rxretrofit.network.exception.ExceptionHandle


/**
 * @desc: 首页精选的 Presenter
 * (数据是 Banner 数据和一页数据组合而成的 HomeBean,查看接口然后在分析就明白了)
 */

class HomePresenter : BasePresenter<HomeContract.View>(), HomeContract.Presenter {


    private var bannerHomeBean: HomeBean? = null

    private var nextPageUrl: String? = null     //加载首页的Banner 数据+一页数据合并后，nextPageUrl没 add

    private val homeModel: HomeModel by lazy {

        HomeModel()
    }

    /**
     * 获取首页精选数据 banner 加 一页数据
     */
    override fun requestHomeData(num: Int) {
        mView?.showLoading()
        homeModel.requestHomeData(num)
            .flatMap { homeBean ->

                //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                val bannerItemList = homeBean.issueList[0].itemList

                bannerItemList.filter { item ->
                    item.type == "banner2" || item.type == "horizontalScrollCard"
                }.forEach { item ->
                    //移除 item
                    bannerItemList.remove(item)
                }

                bannerHomeBean = homeBean //记录第一页是当做 banner 数据


                //根据 nextPageUrl 请求下一页数据
                homeModel.loadMoreData(homeBean.nextPageUrl)
            }
            .autoDisposable(mScopeProvider!!)
            .subscribe({ homeBean ->
                mView?.apply {
                    showContent()

                    nextPageUrl = homeBean.nextPageUrl
                    //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                    val newBannerItemList = homeBean.issueList[0].itemList

                    newBannerItemList.filter { item ->
                        item.type == "banner2" || item.type == "horizontalScrollCard"
                    }.forEach { item ->
                        //移除 item
                        newBannerItemList.remove(item)
                    }
                    // 重新赋值 Banner 长度
                    bannerHomeBean!!.issueList[0].count = bannerHomeBean!!.issueList[0].itemList.size

                    //赋值过滤后的数据 + banner 数据
                    bannerHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)

                    setHomeData(bannerHomeBean!!)

                }

            }, { t ->
                mView?.apply {
                    showContent()
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
    }

    /**
     * 加载更多
     */

    override fun loadMoreData() {
        nextPageUrl?.let {
            homeModel.loadMoreData(it)
                .autoDisposable(mScopeProvider!!)
                .subscribe({ homeBean ->
                    mView?.apply {
                        //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                        val newItemList = homeBean.issueList[0].itemList

                        newItemList.filter { item ->
                            item.type == "banner2" || item.type == "horizontalScrollCard"
                        }.forEach { item ->
                            //移除 item
                            newItemList.remove(item)
                        }

                        nextPageUrl = homeBean.nextPageUrl
                        setMoreData(newItemList)
                    }

                }, { t ->
                    mView?.apply {
                        showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                    }
                })


        }
    }
}
