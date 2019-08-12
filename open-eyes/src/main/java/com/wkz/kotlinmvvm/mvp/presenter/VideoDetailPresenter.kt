package com.wkz.kotlinmvvm.mvp.presenter

import com.wkz.extension.dataFormat
import com.wkz.extension.showToast
import com.wkz.framework.base.BasePresenter
import com.wkz.kotlinmvvm.mvp.contract.VideoDetailContract
import com.wkz.kotlinmvvm.mvp.model.VideoDetailModel
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.rxretrofit.network.exception.ExceptionHandle
import com.wkz.util.NetworkUtil
import com.wkz.util.ScreenUtil
import com.wkz.util.SizeUtil
import javax.inject.Inject

/**
 * @desc: 视频详情Presenter
 */
class VideoDetailPresenter @Inject constructor() : BasePresenter<VideoDetailContract.View>(),
    VideoDetailContract.Presenter {

    @Inject
    lateinit var videoDetailModel: VideoDetailModel

    /**
     * 加载视频相关的数据
     */
    override fun loadVideoInfo(itemInfo: HomeBean.Issue.Item) {

        val playInfo = itemInfo.data?.playInfo

        val netType = NetworkUtil.isWifiConnected
        // 检测是否绑定 View
        checkViewAttached()
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
        val disposable = videoDetailModel.requestRelatedData(id)
            .subscribe({ issue ->
                mView?.apply {
                    dismissLoading()
                    setRecentRelatedVideo(issue.itemList)
                }
            }, { t ->
                mView?.apply {
                    dismissLoading()
                    setErrorMsg(ExceptionHandle.handleException(t))
                }
            })
        addSubscription(disposable)
    }
}