package com.wkz.kotlinmvvm.mvp.presenter

import android.app.Activity
import com.wkz.framework.base.BasePresenter
import com.wkz.framework.base.IBaseModel
import com.wkz.kotlinmvvm.mvp.contract.VideoDetailContract
import com.wkz.kotlinmvvm.mvp.model.VideoDetailModel
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.util.ContextUtil
import com.wkz.util.NetworkUtil

/**
 * Created by xuhao on 2017/11/25.
 * desc:
 */
class VideoDetailPresenter : BasePresenter<VideoDetailContract.View,IBaseModel>(), VideoDetailContract.Presenter {

    private val videoDetailModel: VideoDetailModel by lazy {

        VideoDetailModel()
    }

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
                        //Todo 待完善
                        (mView as Activity).showToast("本次消耗${(mView as Activity)
                                .dataFormat(i.urlList[0].size)}流量")

                        break
                    }
                }
            }
        } else {
            mView?.setVideo(itemInfo.data.playUrl)
        }

        //设置背景
        val backgroundUrl = itemInfo.data.cover.blurred + "/thumbnail/${DisplayManager.getScreenHeight()!! - DisplayManager.dip2px(250f)!!}x${DisplayManager.getScreenWidth()}"
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