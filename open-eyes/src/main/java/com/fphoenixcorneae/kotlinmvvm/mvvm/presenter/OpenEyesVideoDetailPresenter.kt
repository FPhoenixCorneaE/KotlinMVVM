package com.fphoenixcorneae.kotlinmvvm.mvvm.presenter

import com.fphoenixcorneae.ext.dataFormat
import com.fphoenixcorneae.ext.toast
import com.fphoenixcorneae.framework.base.BasePresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesVideoDetailContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesVideoDetailModel
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.rxretrofit.network.exception.ExceptionHandle
import com.fphoenixcorneae.util.NetworkUtil
import com.fphoenixcorneae.util.ScreenUtil
import com.uber.autodispose.autoDisposable
import javax.inject.Inject

/**
 * @desc: 视频详情Presenter
 */
class OpenEyesVideoDetailPresenter @Inject constructor() :
    BasePresenter<OpenEyesVideoDetailContract.View>(),
    OpenEyesVideoDetailContract.Presenter {

    @Inject
    lateinit var videoDetailModel: OpenEyesVideoDetailModel

    /**
     * 加载视频相关的数据
     */
    override fun loadVideoInfo(itemInfo: OpenEyesHomeBean.Issue.Item) {
        val playInfo = itemInfo.data?.playInfo
        val isWifiConnected = NetworkUtil.isWifiConnected
        if (playInfo!!.size > 1) {
            // 当前网络是 Wifi环境下选择高清的视频
            if (isWifiConnected) {
                for (i in playInfo) {
                    if (i.type == "high") {
                        val playUrl = i.url
                        mView.setVideo(playUrl)
                        break
                    }
                }
            } else {
                // 否则就选标清的视频
                for (i in playInfo) {
                    if (i.type == "normal") {
                        val playUrl = i.url
                        mView.setVideo(playUrl)
                        toast("本次消耗${dataFormat(i.urlList[0].size)}流量")
                        break
                    }
                }
            }
        } else {
            mView.setVideo(itemInfo.data.playUrl)
        }

        // 设置背景
        val backgroundUrl =
            itemInfo.data.cover.blurred +
                    "/thumbnail/${(ScreenUtil.screenHeight - ScreenUtil.screenWidth / 1.8f).toInt()}x${ScreenUtil.screenWidth}"
        backgroundUrl.let { mView.setBackground(it) }

        mView.setVideoInfo(itemInfo)

        // 请求相关的最新等视频
        requestRelatedVideo(itemInfo.data.id)
    }

    /**
     * 请求相关的视频数据
     */
    override fun requestRelatedVideo(id: Long) {
        videoDetailModel.requestRelatedData(id)
            .autoDisposable(mScopeProvider)
            .subscribe({ issue ->
                mView.apply {
                    setRecentRelatedVideo(issue.itemList)
                }
            }, { t ->
                mView.apply {
                    setErrorMsg(ExceptionHandle.handleException(t))
                }
            })
    }
}