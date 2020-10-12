package com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.animation.AnimationUtils
import com.fphoenixcorneae.ext.view.*
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesSearchContract
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesSearchPresenter
import com.fphoenixcorneae.ext.closeKeyboard
import com.fphoenixcorneae.ext.openKeyboard
import com.fphoenixcorneae.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.open_eyes_activity_search.*

/**
 * @desc 搜索 Activity
 */
class OpenEyesSearchActivity :
    OpenEyesBaseDagger2Activity<OpenEyesSearchContract.View, OpenEyesSearchPresenter>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_search

    override fun initView() {
        setUpEnterAnimation()
        setUpExitAnimation()
    }

    override fun initListener() {
        mTbTitleBar.init { _, action, _ ->
            when (action) {
                CommonTitleBar.MotionAction.ACTION_RIGHT_TEXT -> onBackPressed()
            }
        }
    }

    /**
     * 进场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpEnterAnimation() {
        window.sharedElementEnterTransition = TransitionInflater.from(mContext)
            .inflateTransition(R.transition.open_eyes_transition_arc_motion)
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
        window.returnTransition = Fade().apply {
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
            R.color.open_eyes_color_bg_default,
            object : OnRevealAnimationListener {
                override fun onRevealShow() {
                    setUpView()
                }
            })
    }

    private fun setUpView() {
        mClRoot.setBackgroundColor(Color.TRANSPARENT)
        mFabCircle.gone()
        mClSearch.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in))
        mClSearch.visible()
        // 打开软键盘
        mTbTitleBar.centerSearchEditText.openKeyboard()
    }

    override fun onBackPressed() {
        animateRevealHide()
    }

    /**
     * 圆圈凝聚效果
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateRevealHide() {
        mClRoot.animateRevealHide(
            mFabCircle.width / 2,
            R.color.open_eyes_color_bg_default,
            object : OnRevealAnimationListener {
                override fun onRevealHide() {
                    mFabCircle.visible()
                    mClSearch.gone()
                    defaultBackPressed()
                }
            })
    }

    /**
     * 默认返回
     */
    private fun defaultBackPressed() {
        // 关闭软键盘
        mTbTitleBar.centerSearchEditText.closeKeyboard()
        super.onBackPressed()
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}