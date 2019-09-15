package com.wkz.kotlinmvvm.mvvm.presenter

import com.uber.autodispose.autoDisposable
import com.wkz.extension.dataFormat
import com.wkz.extension.showToast
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesVideoDetailContract
import com.wkz.kotlinmvvm.mvvm.model.OpenEyesVideoDetailModel
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import com.wkz.util.NetworkUtil
import com.wkz.util.ScreenUtil
import com.wkz.util.SizeUtil
import javax.inject.Inject

/**
 * @desc: 视频详情Presenter
 */
class OpenEyesVideoDetailPresenter @Inject constructor() : BasePresenter<OpenEyesVideoDetailContract.View>(),
    OpenEyesVideoDetailContract.Presenter {

    @Inject
    lateinit var videoDetailModel: OpenEyesVideoDetailModel

    /**
     * 加载视频相关的数据
     */
    override fun loadVideoInfo(itemInfo: OpenEyesHomeBean.Issue.Item) {

        val playInfo = itemInfo.data?.playInfo

        val netType = NetworkUtil.isWifiConnected

        if (playInfo!!.size > 1) {
            // 当前网络是 Wifi环境下选择高清的视频
            if (netType) {
                for (i in playInfo) {
                    if (i.type == "high") {
                        val playUrl = i.url
                        mView?.setVideo(playUrl)
                        break
                    }
                }
            } else {
                //否则就选标清的视频
                for (i in playInfo) {
                    if (i.type == "normal") {
                        val playUrl = i.url
                        mView?.setVideo(playUrl)
                        showToast("本次消耗${dataFormat(i.urlList[0].size)}流量")

                        break
                    }
                }
            }
        } else {
            mView?.setVideo(itemInfo.data.playUrl)
        }

        //设置背景
        val backgroundUrl =
            itemInfo.data.cover.blurred + "/thumbnail/${ScreenUtil.screenHeight - SizeUtil.dp2px(250f)}x${ScreenUtil.screenWidth}"
        backgroundUrl.let { mView?.setBackground(it) }

        mView?.setVideoInfo(itemInfo)
    }

    /**
     * 请求相关的视频数据
     */
    override fun requestRelatedVideo(id: Long) {
        mView?.showLoading()
        videoDetailModel.requestRelatedData(id)
            .autoDisposable(mScopeProvider!!)
            .subscribe({ issue ->
                mView?.apply {
                    showContent()
                    setRecentRelatedVideo(issue.itemList)
                }
            }, { t ->
                mView?.apply {
                    showContent()
                    setErrorMsg(ExceptionHandle.handleException(t))
                }
            })
    }
}