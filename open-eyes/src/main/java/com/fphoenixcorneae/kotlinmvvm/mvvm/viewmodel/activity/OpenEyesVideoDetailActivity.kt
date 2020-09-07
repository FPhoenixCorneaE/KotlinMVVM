package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.activity

import android.annotation.SuppressLint
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
import com.fphoenixcorneae.extension.durationFormat
import com.fphoenixcorneae.extension.showToast
import com.fphoenixcorneae.framework.base.activity.Dagger2InjectionActivity
import com.fphoenixcorneae.framework.glide.GlideUtil
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.constant.OpenEyesConstants
import com.fphoenixcorneae.kotlinmvvm.listener.OnVideoListener
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesVideoDetailContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.fphoenixcorneae.kotlinmvvm.mvvm.presenter.OpenEyesVideoDetailPresenter
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.adapter.OpenEyesVideoListAdapter
import com.fphoenixcorneae.util.ColorUtil
import com.fphoenixcorneae.util.ImageUtil
import com.fphoenixcorneae.util.ResourceUtil
import com.fphoenixcorneae.util.ScreenUtil
import com.fphoenixcorneae.util.statusbar.StatusBarUtil
import com.fphoenixcorneae.widget.Callback
import kotlinx.android.synthetic.main.open_eyes_activity_video_detail.*
import kotlinx.android.synthetic.main.open_eyes_layout_video_detail_info.*
import java.util.*

/**
 * @desc: 视频详情
 */
class OpenEyesVideoDetailActivity :
    Dagger2InjectionActivity<OpenEyesVideoDetailContract.View, OpenEyesVideoDetailPresenter>(),
    OpenEyesVideoDetailContract.View {

    private val mVideoListAdapter by lazy {
        OpenEyesVideoListAdapter(mContext, ArrayList())
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
            mVideoListAdapter.setOnItemDetailClick {
                mPresenter.loadVideoInfo(it)
            }
            layoutManager = LinearLayoutManager(mContext)
            adapter = mVideoListAdapter
            isNestedScrollingEnabled = false
        }
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
            }

            override fun onPlayError(url: String, vararg objects: Any) {
                super.onPlayError(url, *objects)
                showToast("播放失败")
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
    @SuppressLint("SetTextI18n")
    override fun setVideoInfo(itemInfo: OpenEyesHomeBean.Issue.Item) {
        mVideoDetailData = itemInfo
        // avatar
        GlideUtil.setupCircleImage(
            mIvAvatar,
            itemInfo.data?.author?.icon ?: itemInfo.data?.provider?.icon
        )
        // author name
        mTvAuthorName.text = itemInfo.data?.author?.name
        // author description
        mTvAuthorDescription.text = itemInfo.data?.author?.description
        // title
        mTvTitle.text = itemInfo.data?.title
        // description
        mTvDescription.apply {
            // 设置最大显示行数
            mMaxLineCount = 3
            // 收起文案
            mCollapseText = ResourceUtil.getString(R.string.open_eyes_collapse_text)
            // 展开文案
            mExpandText = ResourceUtil.getString(R.string.open_eyes_expand_text)
            // 是否支持收起功能
            mCollapseEnable = true
            // 是否给展开收起添加下划线
            mUnderlineEnable = false
            // 收起文案颜色
            mCollapseTextColor = ColorUtil.randomColor
            // 展开文案颜色
            mExpandTextColor = ColorUtil.randomColor
            itemInfo.data?.description?.let {
                setText(it, itemInfo.data.expanded, object : Callback {
                    override fun onExpand() {

                    }

                    override fun onCollapse() {
                    }

                    override fun onLoss() {
                    }

                    override fun onExpandClick() {
                        itemInfo.data.expanded = !itemInfo.data.expanded
                        changeExpandedState(itemInfo.data.expanded)
                    }

                    override fun onCollapseClick() {
                        itemInfo.data.expanded = !itemInfo.data.expanded
                        changeExpandedState(itemInfo.data.expanded)
                    }
                })
            }
        }

        // category
        mTvCategory.text = "#${itemInfo.data?.category}"
        // duration
        mTvDuration.text = durationFormat(itemInfo.data?.duration ?: 0)
        // collectionCount
        mTvCollection.text = itemInfo.data?.consumption?.collectionCount.toString()
        // shareCount
        mTvShare.text = itemInfo.data?.consumption?.shareCount.toString()
        // replyCount
        mTvReply.text = itemInfo.data?.consumption?.replyCount.toString()
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