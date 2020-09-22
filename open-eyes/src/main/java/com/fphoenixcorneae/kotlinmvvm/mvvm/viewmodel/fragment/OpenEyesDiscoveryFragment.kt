package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment

import android.graphics.Color
import android.os.Bundle
import com.fphoenixcorneae.adapter.app.FragmentPagerItems
import com.fphoenixcorneae.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_fragment_discovery.*
import kotlinx.android.synthetic.main.open_eyes_title_bar_custom_center.*

/**
 * @desc 发现Fragment
 * @date 2020-09-18 11:20
 */
class OpenEyesDiscoveryFragment : OpenEyesBaseFragment() {

    companion object {

        fun getInstance(): OpenEyesDiscoveryFragment {
            val fragment = OpenEyesDiscoveryFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_discovery

    override fun initView() {
        mFlMagicIndicator.bindViewPager2(
            FragmentStatePager2ItemAdapter(
                this,
                FragmentPagerItems.with(mContext)
                    .add(
                        getString(R.string.open_eyes_follow),
                        OpenEyesFollowFragment::class.java
                    )
                    .add(
                        getString(R.string.open_eyes_category),
                        OpenEyesCategoryFragment::class.java
                    )
                    .create()
            ),
            mVpDiscovery,
            true
        )
    }

    override fun initListener() {
        childFragmentManager.setFragmentResultListener(
            "discoveryTitle",
            this,
            { requestKey, result ->
                when (requestKey) {
                    "discoveryTitle" -> {
                        val alpha = result.getInt("alpha")
                        mTbTitleBar.setBackgroundColor(
                            ColorUtil.setAlphaComponent(
                                Color.WHITE,
                                alpha
                            )
                        )
                    }
                }
            })
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}