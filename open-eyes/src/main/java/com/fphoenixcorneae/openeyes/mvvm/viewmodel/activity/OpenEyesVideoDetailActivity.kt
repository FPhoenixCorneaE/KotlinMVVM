package com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fphoenixcorneae.ext.durationFormat
import com.fphoenixcorneae.ext.loggerD
import com.fphoenixcorneae.ext.toast
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.constant.OpenEyesConstants
import com.fphoenixcorneae.openeyes.listener.OnVideoListener
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesVideoDetailContract
import com.fphoenixcorneae.openeyes.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesVideoDetailPresenter
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.adapter.OpenEyesVideoListAdapter
import com.fphoenixcorneae.text.style.AnimatedTypeWriterSpan
import com.fphoenixcorneae.util.ColorUtil
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import kotlinx.android.synthetic.main.open_eyes_activity_video_detail.*
import kotlinx.android.synthetic.main.open_eyes_layout_video_detail_author.*
import kotlinx.android.synthetic.main.open_eyes_layout_video_detail_info.*
import java.util.*

/**
 * @desc 视频详情 Activity
 */
class OpenEyesVideoDetailActivity :
    OpenEyesBaseDagger2Activity<OpenEyesVideoDetailContract.View, OpenEyesVideoDetailPresenter>(),
    OpenEyesVideoDetailContract.View {

    private val mVideoListAdapter by lazy {
        OpenEyesVideoListAdapter(mContext, arrayListOf())
    }

    /**
     * 视频详细数据
     */
    private lateinit var mVideoDetailData: OpenEyesHomeBean.Issue.Item
    private var mOrientationUtils: OrientationUtils? = null
    private var mIsPlay: Boolean = false
    private var mIsPause: Boolean = false
    private var mSharedElementEnterTransition: Transition? = null

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_video_detail

    /**
     * 初始化 View
     */
    override fun initView() {
        // 过渡动画
        initTransition()
        // RecyclerView
        initRecyclerView()
    }

    /**
     * 初始化过渡动画
     */
    private fun initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 延迟共享动画的执行
            postponeEnterTransition()
            ViewCompat.setTransitionName(
                mVpVideo,
                getString(R.string.open_eyes_transition_name_video)
            )
            ViewCompat.setTransitionName(
                mIvAvatar,
                getString(R.string.open_eyes_transition_name_avatar)
            )
            addTransitionListener()
            // 启动过渡动画
            startPostponedEnterTransition()
        } else {
            loadVideoInfo()
        }
    }

    private fun initRecyclerView() {
        with(mRvVideo) {
            // 设置相关视频 Item 的点击事件
            mVideoListAdapter.setOnItemClickListener { _, item, _ ->
                mPresenter.loadVideoInfo(item)
                mNsvScroll.scrollTo(0, 0)
            }
            layoutManager = LinearLayoutManager(mContext)
            adapter = mVideoListAdapter
            isNestedScrollingEnabled = false
        }
    }

    /**
     * 初始化数据
     */
    override fun initData(savedInstanceState: Bundle?) {
        mVideoDetailData =
            intent.getSerializableExtra(OpenEyesConstants.EXTRA_KEY_VIDEO_DATA)
                    as OpenEyesHomeBean.Issue.Item
        initVideoViewConfig()
    }

    /**
     * 初始化 VideoView 的配置
     */
    private fun initVideoViewConfig() {
        // 设置旋转
        mOrientationUtils = OrientationUtils(mContext, mVpVideo)
        // 是否旋转
        mVpVideo.isRotateViewAuto = false
        // 是否可以滑动调整
        mVpVideo.setIsTouchWiget(true)

        // 增加封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideUtil.setupImage(
            imageView,
            mVideoDetailData.data?.cover?.feed,
            ColorDrawable(ColorUtil.randomColor)
        )
        mVpVideo.thumbImageView = imageView

        mVpVideo.setStandardVideoAllCallBack(object : OnVideoListener {

            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                // 开始播放了才能旋转和全屏
                mOrientationUtils?.isEnable = true
                mIsPlay = true
            }

            override fun onAutoComplete(url: String, vararg objects: Any) {
                super.onAutoComplete(url, *objects)
            }

            override fun onPlayError(url: String, vararg objects: Any) {
                super.onPlayError(url, *objects)
                toast("播放失败！")
            }

            override fun onEnterFullscreen(url: String, vararg objects: Any) {
                super.onEnterFullscreen(url, *objects)
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                // 列表返回的样式判断
                mOrientationUtils?.backToProtVideo()
            }
        })
        // 设置返回按键功能
        mVpVideo.backButton.setOnClickListener { onBackPressed() }
        // 设置全屏按键功能
        mVpVideo.fullscreenButton.setOnClickListener {
            // 直接横屏
            mOrientationUtils?.resolveByClick()
            // 第一个true是否需要隐藏ActionBar，第二个true是否需要隐藏StatusBar
            mVpVideo.startWindowFullscreen(this, true, true)
        }
        // 锁屏事件
        mVpVideo.setLockClickListener { _, lock ->
            // 配合下方的onConfigurationChanged
            mOrientationUtils?.isEnable = !lock
        }
    }

    override fun isAlreadyLoadedData(): Boolean = true

    /**
     * 设置播放视频 URL
     */
    override fun setVideo(url: String) {
        loggerD("playUrl:$url")
        mVpVideo.setUp(url, false, "")
        // 开始自动播放
        mVpVideo.startPlayLogic()
    }

    /**
     * 设置视频信息
     */
    @SuppressLint("SetTextI18n")
    override fun setVideoInfo(itemInfo: OpenEyesHomeBean.Issue.Item) {
        mVideoDetailData = itemInfo
        mVideoDetailData.data?.apply {
            // title
            AnimatedTypeWriterSpan(title) {
                mTvTitle.text = it
            }
            // category
            AnimatedTypeWriterSpan(
                "#${category} / ${getString(R.string.open_eyes_eyepetizer_choiceness)} / ${
                    durationFormat(duration)
                }"
            ) {
                mTvCategory.text = it
            }
            // description
            AnimatedTypeWriterSpan(description) {
                mTvDescription.text = it
            }

            consumption.apply {
                // collectionCount
                mTvCollection.text = collectionCount.toString()
                // shareCount
                mTvShare.text = shareCount.toString()
                // replyCount
                mTvReply.text = replyCount.toString()
            }

            // avatar
            GlideUtil.setupImage(
                mIvAvatar,
                author.icon
            )
            // author name
            mTvAuthorName.text = author.name
            // author description
            mTvAuthorDescription.text = author.description
        }
    }

    /**
     * 设置相关的数据视频
     */
    override fun setRecentRelatedVideo(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mVideoListAdapter.setData(itemList)
    }

    /**
     * 设置背景颜色
     */
    override fun setBackground(url: String) {
        GlideUtil.setupImage(mIvVideoBackground, url)
    }

    /**
     * 设置错误信息
     */
    override fun setErrorMsg(errorMsg: String) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (mIsPlay && !mIsPause) {
            mVpVideo.onConfigurationChanged(mContext, newConfig, mOrientationUtils)
        }
    }

    /**
     * 加载视频信息
     */
    fun loadVideoInfo() {
        mPresenter.loadVideoInfo(mVideoDetailData)
    }

    /**
     * 监听返回键
     */
    override fun onBackPressed() {
        mOrientationUtils?.backToProtVideo()
        if (StandardGSYVideoPlayer.backFromWindowFull(mContext)) {
            return
        }
        // 释放所有
        mVpVideo.setStandardVideoAllCallBack(null)
        GSYVideoPlayer.releaseAllVideos()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> run {
                super.onBackPressed()
            }
            else -> {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCurPlay().onVideoResume()
        mIsPause = false
    }

    override fun onPause() {
        super.onPause()
        getCurPlay().onVideoPause()
        mIsPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        mOrientationUtils?.releaseListener()
    }

    override fun finish() {
        startPostponedEnterTransition()
        super.finish()
    }

    private fun getCurPlay(): GSYVideoPlayer {
        return when {
            mVpVideo.fullWindowPlayer != null -> mVpVideo.fullWindowPlayer
            else -> mVpVideo
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener() {
        mSharedElementEnterTransition = window.sharedElementEnterTransition
        mSharedElementEnterTransition?.addListener(object : Transition.TransitionListener {
            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
            }

            override fun onTransitionEnd(p0: Transition?) {
                loadVideoInfo()
                mSharedElementEnterTransition?.removeListener(this)
            }
        })
    }
}