package com.wkz.kotlinmvvm.mvvm.viewmodel.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.wkz.extension.showToast
import com.wkz.framework.base.activity.Dagger2InjectionActivity
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.listener.OnVideoListener
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesVideoDetailContract
import com.wkz.kotlinmvvm.mvvm.model.bean.OpenEyesHomeBean
import com.wkz.kotlinmvvm.mvvm.presenter.OpenEyesVideoDetailPresenter
import com.wkz.kotlinmvvm.mvvm.viewmodel.adapter.OpenEyesVideoDetailAdapter
import kotlinx.android.synthetic.main.open_eyes_activity_video_detail.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
/**
 * @desc: 视频详情
 */
class OpenEyesVideoDetailActivity :
    Dagger2InjectionActivity<OpenEyesVideoDetailContract.View, OpenEyesVideoDetailPresenter>(),
    OpenEyesVideoDetailContract.View {


    companion object {
        const val IMG_TRANSITION = "IMG_TRANSITION"
        const val TRANSITION = "TRANSITION"
    }

    private val mAdapter by lazy { OpenEyesVideoDetailAdapter(mContext, itemList) }

    private val mFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss"); }


    /**
     * Item 详细数据
     */
    private lateinit var itemData: OpenEyesHomeBean.Issue.Item
    private var orientationUtils: OrientationUtils? = null

    private var itemList = ArrayList<OpenEyesHomeBean.Issue.Item>()

    private var isPlay: Boolean = false
    private var isPause: Boolean = false


    private var isTransition: Boolean = false

    private var transition: Transition? = null
    private var mMaterialHeader: MaterialHeader? = null

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_video_detail

    /**
     * 初始化 View
     */
    override fun initView() {
        //过渡动画
//        initTransition()
        initVideoViewConfig()

        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = mAdapter

        //设置相关视频 Item 的点击事件
        mAdapter.setOnItemDetailClick { mPresenter.loadVideoInfo(it) }

//        //状态栏透明和间距处理
//        StatusBarUtil.immersive(this)
//        StatusBarUtil.setPaddingSmart(this, mVideoView)

        /***  下拉刷新  ***/
        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            loadVideoInfo()
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(
            R.color.open_eyes_color_black_alpha50,
            R.color.open_eyes_color_bg_title
        )
    }


    /**
     * 初始化 VideoView 的配置
     */
    private fun initVideoViewConfig() {
        //设置旋转
        orientationUtils = OrientationUtils(this, mVideoView)
        //是否旋转
        mVideoView.isRotateViewAuto = false
        //是否可以滑动调整
        mVideoView.setIsTouchWiget(true)

        //增加封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        Glide.with(this)
//            .load(itemData.data?.cover?.feed)
//            .centerCrop()
//            .into(imageView)
        mVideoView.thumbImageView = imageView

        mVideoView.setStandardVideoAllCallBack(object : OnVideoListener {

            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils?.isEnable = true
                isPlay = true
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
                //列表返回的样式判断
                orientationUtils?.backToProtVideo()
            }
        })
        //设置返回按键功能
        mVideoView.backButton.setOnClickListener({ onBackPressed() })
        //设置全屏按键功能
        mVideoView.fullscreenButton.setOnClickListener {
            //直接横屏
            orientationUtils?.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            mVideoView.startWindowFullscreen(this, true, true)
        }
        //锁屏事件
        mVideoView.setLockClickListener(object : LockClickListener {
            override fun onClick(view: View?, lock: Boolean) {
                //配合下方的onConfigurationChanged
                orientationUtils?.isEnable = !lock
            }

        })
    }

    /**
     * 初始化数据
     */
    override fun initData(savedInstanceState: Bundle?) {
//        itemData = intent.getSerializableExtra(OpenEyesConstants.BUNDLE_VIDEO_DATA) as OpenEyesHomeBean.Issue.Item
        itemData = OpenEyesHomeBean.Issue.Item("", null, "")
        isTransition = intent.getBooleanExtra(TRANSITION, false)

        showContent()
        mPresenter.requestRelatedVideo(169617)
    }


    override fun showLoading() {

    }

     override fun showContent() {
        super.showContent()
        mRefreshLayout.finishRefresh()
    }

    override fun isAlreadyLoadedData(): Boolean = true

    /**
     * 设置播放视频 URL
     */
    override fun setVideo(url: String) {
        Logger.d("playUrl:$url")
        mVideoView.setUp(url, false, "")
        //开始自动播放
        mVideoView.startPlayLogic()
    }

    /**
     * 设置视频信息
     */
    override fun setVideoInfo(itemInfo: OpenEyesHomeBean.Issue.Item) {
        itemData = itemInfo
        mAdapter.addData(itemInfo)
        // 请求相关的最新等视频
        mPresenter.requestRelatedVideo(itemInfo.data?.id ?: 0)

    }


    /**
     * 设置相关的数据视频
     */
    override fun setRecentRelatedVideo(itemList: ArrayList<OpenEyesHomeBean.Issue.Item>) {
        mAdapter.addData(itemList)
        this.itemList = itemList
    }


    /**
     * 设置背景颜色
     */
    override fun setBackground(url: String) {
        Glide.with(this)
            .load(url)
            .centerCrop()
            .format(DecodeFormat.PREFER_ARGB_8888)
            .transition(DrawableTransitionOptions().crossFade())
            .into(mVideoBackground)
    }

    /**
     * 设置错误信息
     */
    override fun setErrorMsg(errorMsg: String) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils)
        }
    }

    /**
     * 1.加载视频信息
     */
    fun loadVideoInfo() {
        mPresenter.loadVideoInfo(itemData)
    }

    /**
     * 监听返回键
     */
    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (StandardGSYVideoPlayer.backFromWindowFull(this))
            return
        //释放所有
        mVideoView.setStandardVideoAllCallBack(null)
        GSYVideoPlayer.releaseAllVideos()
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) run {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        getCurPlay().onVideoResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        getCurPlay().onVideoPause()
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        orientationUtils?.releaseListener()
    }

    private fun getCurPlay(): GSYVideoPlayer {
        return if (mVideoView.fullWindowPlayer != null) {
            mVideoView.fullWindowPlayer
        } else mVideoView
    }

    private fun initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
            ViewCompat.setTransitionName(mVideoView, IMG_TRANSITION)
            addTransitionListener()
            startPostponedEnterTransition()
        } else {
            loadVideoInfo()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener() {
        transition = window.sharedElementEnterTransition
        transition?.addListener(object : Transition.TransitionListener {
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
                transition?.removeListener(this)
            }

        })
    }
}