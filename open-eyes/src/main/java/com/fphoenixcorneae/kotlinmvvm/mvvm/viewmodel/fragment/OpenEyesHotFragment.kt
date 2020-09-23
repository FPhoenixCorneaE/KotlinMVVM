package com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment

import android.graphics.Color
import android.os.Bundle
import com.fphoenixcorneae.adapter.app.FragmentPagerItems
import com.fphoenixcorneae.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.kotlinmvvm.R
import com.fphoenixcorneae.kotlinmvvm.constant.OpenEyesConstants
import com.fphoenixcorneae.kotlinmvvm.mvvm.contract.OpenEyesHotTabContract
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.bean.OpenEyesTabInfoBean
import com.fphoenixcorneae.kotlinmvvm.mvvm.presenter.OpenEyesHotPresenter
import com.fphoenixcorneae.util.ColorUtil
import kotlinx.android.synthetic.main.open_eyes_fragment_discovery.*
import kotlinx.android.synthetic.main.open_eyes_title_bar_custom_center.*

/**
 * @desc 热门Fragment
 * @date 2020-09-23 13:54
 */
class OpenEyesHotFragment :
    OpenEyesBaseDagger2Fragment<OpenEyesHotTabContract.View, OpenEyesHotPresenter>(),
    OpenEyesHotTabContract.View {

    companion object {

        fun getInstance(): OpenEyesHotFragment {
            val fragment = OpenEyesHotFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_discovery

    override fun initView() {

    }

    override fun initListener() {
        childFragmentManager.setFragmentResultListener(
            OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA,
            this,
            { requestKey, result ->
                when (requestKey) {
                    OpenEyesConstants.REQUEST_KEY_TITLE_ALPHA -> {
                        val alpha = result.getInt(OpenEyesConstants.EXTRA_KEY_ALPHA)
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
        mPresenter.getTabInfo()
    }

    override fun isAlreadyLoadedData(): Boolean = true

    override fun setTabInfo(tabInfoBean: OpenEyesTabInfoBean) {
        mFlMagicIndicator.bindViewPager2(
            FragmentStatePager2ItemAdapter(
                this,
                FragmentPagerItems.with(mContext)
                    .apply {
                        tabInfoBean.tabInfo.tabList.forEach {
                            add(it.name, OpenEyesRankFragment::class.java, Bundle().apply {
                                putString(OpenEyesConstants.EXTRA_KEY_API_URL, it.apiUrl)
                            })
                        }
                    }
                    .create()
            ),
            mVpPager,
            true
        )
    }
}