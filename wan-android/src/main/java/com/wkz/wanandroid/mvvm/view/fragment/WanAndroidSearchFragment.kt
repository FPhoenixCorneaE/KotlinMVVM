package com.wkz.wanandroid.mvvm.view.fragment

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.transition.Fade
import android.view.animation.AnimationUtils
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.wkz.extension.*
import com.wkz.util.KeyboardUtil
import com.wkz.wanandroid.R
import kotlinx.android.synthetic.main.wan_android_fragment_search.*

/**
 * @desc: 搜索Fragment
 * @date: 2020-06-28 14:31
 */
class WanAndroidSearchFragment : WanAndroidBaseFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_fragment_search

    override fun initView() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                setUpEnterAnimation()
                setUpExitAnimation()
            }
            else -> {
                setUpView()
            }
        }
    }

    private fun setUpView() {
        mClRoot.setBackgroundColor(Color.TRANSPARENT)
        mFabCircle.gone()
        mClSearch.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in))
        mClSearch.visible()
        // 打开软键盘
        KeyboardUtil.openKeyboard(mTbTitleBar.centerSearchEditText)
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true

    /**
     * 进场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpEnterAnimation() {
        sharedElementEnterTransition = TransitionInflater.from(mContext)
            .inflateTransition(R.transition.wan_android_transition_arc_motion)
            .addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                }

                override fun onTransitionEnd(transition: Transition) {
                    transition.removeListener(this)
                    animateRevealShow()
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }
            })
    }

    /**
     * 退场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpExitAnimation() {
        sharedElementReturnTransition = Fade().apply {
            duration = 300
        }
    }

    /**
     * 揭露动画显示
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateRevealShow() {
        mClRoot.animateRevealShow(
            mFabCircle.width / 2,
            R.color.wan_android_colorPrimary,
            object : OnRevealAnimationListener {
                override fun onRevealShow() {
                    setUpView()
                }
            })
    }

    /**
     * 圆圈凝聚效果
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateRevealHide() {
        mClRoot.animateRevealHide(
            mFabCircle.width / 2,
            R.color.wan_android_colorPrimary,
            object : OnRevealAnimationListener {
                override fun onRevealHide() {
                    mFabCircle.visible()
                    mClSearch.gone()
                    defaultBackPressed()
                }
            })
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateRevealHide()
        } else {
            defaultBackPressed()
        }
    }

    /**
     * 默认返回
     */
    private fun defaultBackPressed() {
        // 关闭软键盘
        KeyboardUtil.closeKeyboard(mTbTitleBar.centerSearchEditText)
        popBackStack()
    }

    override fun onDestroy() {
        KeyboardUtil.fixSoftInputLeaks(mContext.window)
        super.onDestroy()
    }
}