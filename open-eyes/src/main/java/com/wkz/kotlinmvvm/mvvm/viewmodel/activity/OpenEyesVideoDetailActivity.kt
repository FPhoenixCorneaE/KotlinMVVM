package com.wkz.kotlinmvvm.mvvm.viewmodel.activity

import android.annotation.TargetApi
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.orhanobut.logger.Logger
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.wkz.extension.showToast
import com.wkz.framework.base.activity.Dagger2InjectionActivity
import com.wkz.framework.glide.GlideUtil
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.constant.OpenEyesConstants
import com.wkz.kotlinmvvm.listener.OnVideoListener
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesVideoDetailContract
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.kotlinmvvm.mvvm.presenter.OpenEyesVideoDetailPresenter
import com.wkz.kotlinmvvm.mvvm.viewmodel.adapter.OpenEyesVideoDetailAdapter
import com.wkz.util.ColorUtil
import com.wkz.util.ImageUtil
import com.wkz.util.ScreenUtil
import com.wkz.util.statusbar.StatusBarUtil
import kotlinx.android.synthetic.main.open_eyes_activity_video_detail.*
import java.util.*

/**
 * @desc: 视频详情
 */
class OpenEyesVideoDetailActivity :
    Dagger2InjectionActivity<OpenEyesVideoDetailContract.View, OpenEyesVideoDetailPresenter>(),
    OpenEyesVideoDetailContract.View {

    private var mVideoList = ArrayList<OpenEyesHomeBean.Issue.Item>()
    private val mVideoDetailAdapter by lazy {
        OpenEyesVideoDetailAdapter(mContext, mVideoList)
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
        // 状态栏透明和间距处理
        initStatusBar()
        mVpVideo.also {
            val layoutParams = it.layoutParams
            layoutParams.height = ScreenUtil.screenHeight / 3
        }
    }

    /**
     * 初始化过渡动画
     */
    private fun initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
            ViewCompat.setTransitionName(
                mVpVideo,
                getString(R.string.open_eyes_transition_name_video)
            )
            addTransitionListener()
            startPostponedEnterTransition()
        } else {
            loadVideoInfo()
        }
    }

    private fun initRecyclerView() {
        // 设置相关视频 Item 的点击事件
        mVideoDetailAdapter.setOnItemDetailClick {
            mPresenter.loadVideoInfo(it)
        }
        mRvVideo.layoutManager = LinearLayoutManager(mContext)
        mRvVideo.adapter = mVideoDetailAdapter
    }

    private fun initStatusBar() {
        StatusBarUtil.transparentStatusBar(window)
        StatusBarUtil.setSmartPadding(mContext, mVpVideo)
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
                Logger.d("***** onAutoPlayComplete **** ")
            }

            override fun onPlayError(url: String, vararg objects: Any) {
                super.onPlayError(url, *objects)
                showToast("播放失败")
            }

            override fun onEnterFullscreen(url: String, vararg objects: Any) {
                super.onEnterFullscreen(url, *objects)
                Logger.d("***** onEnterFullscreen **** ")
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                Logger.d("***** onQuitFullscreen **** ")
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
            // 第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
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
        Logger.d("playUrl:$url")
        mVpVideo.setUp(url, false, "")
        // 开始自动播放
        mVpVideo.startPlayLogic()
    }

    /**
     * 设置视频信息
     */
    override fun setVideoInfo(itemInfo: OpenEyesHomeBean.Issue.Item) {
        mVideoDetailData = itemInfo
        mVideoDetailAdapter.addData(itemInfo)
        // 请求相关的最新等视频
        mPresenter.requestRelatedVideo(itemInfo.data?.id ?: 0)
    }


    /**
     * 设置相关的数据视频
     */
    override fun setRecentRelatedVideo(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mVideoDetailAdapter.addData(itemList)
        mVideoList = itemList
    }

    /**
     * 设置背景颜色
     */
    override fun setBackground(url: String) {
        GlideUtil.setupImage(
            mIvVideoBackground,
            url,
            RequestOptions().centerCrop(),
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // 根据图片背景设置状态栏颜色
                    StatusBarUtil.setStatusBarColor(
                        window,
                        resource?.run {
                            ColorUtil.getColorFromBitmap(ImageUtil.drawable2Bitmap(resource))
                        } ?: Color.TRANSPARENT
                    )
                    return false
                }
            })
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
     * 1.加载视频信息
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) run {
            super.onBackPressed()
        } else {
            finish()
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