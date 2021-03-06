package com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment

import android.graphics.Color
import android.os.Bundle
import com.fphoenixcorneae.viewpager.FragmentPagerItems
import com.fphoenixcorneae.viewpager.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.constant.OpenEyesConstants
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
            mVpPager,
            true
        )
    }

    override fun initListener() {
        childFragmentManager.setFragmentResultListener(
            OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA,
            this,
            { requestKey, result ->
                when (requestKey) {
                    OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA -> {
                        val alpha = result.getInt(OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA)
                        mTbTitleBar.setBackgroundColor(
                            ColorUtil.setAlphaComponent(Color.WHITE, alpha)
                        )
                    }
                }
            })
    }

    override fun lazyLoadData() {
    }

    override fun isAlreadyLoadedData(): Boolean = true
}