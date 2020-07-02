package com.wkz.wanandroid.mvvm.view.fragment

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.animation.AnimationUtils
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.wkz.extension.OnRevealAnimationListener
import com.wkz.extension.animateRevealShow
import com.wkz.extension.loggerD
import com.wkz.extension.visible
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
        mClSearch.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in))
        mClSearch.visible()
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true

    /**
     * 进场动画
     */
    private fun setUpEnterAnimation() {
        sharedElementEnterTransition = TransitionInflater.from(mContext)
            .inflateTransition(R.transition.wan_android_transition_arc_motion)
            .addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    loggerD("setUpEnterAnimation-----onTransitionStart")
                }

                override fun onTransitionEnd(transition: Transition) {
                    loggerD("setUpEnterAnimation-----onTransitionEnd")
                    transition.removeListener(this)
                    animateRevealShow()
                }

                override fun onTransitionCancel(transition: Transition) {
                    loggerD("setUpEnterAnimation-----onTransitionCancel")
                }

                override fun onTransitionPause(transition: Transition) {
                    loggerD("setUpEnterAnimation-----onTransitionPause")
                }

                override fun onTransitionResume(transition: Transition) {
                    loggerD("setUpEnterAnimation-----onTransitionResume")
                }
            })
    }

    /**
     * 退场动画
     */
    private fun setUpExitAnimation() {
        returnTransition = Fade().apply {
            duration = 300
        }
    }

    private fun animateRevealShow() {
        mClRoot.animateRevealShow(
            mFabCircle.width / 2,
            R.color.wan_android_colorPrimary,
            object : OnRevealAnimationListener {
                override fun onRevealHide() {

                }

                override fun onRevealShow() {
                    setUpView()
                }
            })
    }
}