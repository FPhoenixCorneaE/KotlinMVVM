package com.wkz.wanandroid.mvvm.view.fragment

import android.view.View
import android.view.animation.LinearInterpolator
import com.bumptech.glide.GenericTransitionOptions
import com.wkz.animation_dsl.animSet
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.wanandroid.R
import kotlinx.android.synthetic.main.wan_android_fragment_mine.*

/**
 * @desc：我的Fragment
 * @date：2020-04-26 12:53
 */
class WanAndroidMineFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_mine

    override fun initView() {
    }

    override fun lazyLoadData() {
        mIvUserAvatar.load(R.mipmap.wan_android_ic_avatar_default/*,
            GenericTransitionOptions.with { view: View ->
                // 设置加载动画
                animSet {
                    objectAnim {
                        target = view
                        alpha = floatArrayOf(0f, 1f)
                        duration = 200L
                        interpolator = LinearInterpolator()
                    }
                    start()
                }
            }*/)
    }

    override fun isAlreadyLoadedData(): Boolean = true

    companion object {
        fun getInstance(): WanAndroidMineFragment {
            return WanAndroidMineFragment()
        }
    }
}