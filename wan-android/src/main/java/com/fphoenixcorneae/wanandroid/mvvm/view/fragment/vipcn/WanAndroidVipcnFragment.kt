package com.fphoenixcorneae.wanandroid.mvvm.view.fragment.vipcn

import androidx.lifecycle.Observer
import com.fphoenixcorneae.adapter.app.FragmentPagerItems
import com.fphoenixcorneae.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.fphoenixcorneae.ext.toHtml
import com.fphoenixcorneae.ext.viewModel
import com.fphoenixcorneae.util.BundleBuilder
import com.fphoenixcorneae.util.statusbar.StatusBarUtil
import com.fphoenixcorneae.wanandroid.R
import com.fphoenixcorneae.wanandroid.constant.WanAndroidConstant
import com.fphoenixcorneae.wanandroid.mvvm.model.WanAndroidClassifyBean
import com.fphoenixcorneae.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.fphoenixcorneae.wanandroid.mvvm.viewmodel.WanAndroidVipcnViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_vipcn.*

/**
 * @desc: 公众号Fragment
 * @date: 2020-06-18 17:31
 */
class WanAndroidVipcnFragment : WanAndroidBaseFragment() {

    /* 公众号ViewModel */
    private val mVipcnViewModel by viewModel<WanAndroidVipcnViewModel>()

    /* 公众号分类数据 */
    private val mClassifyData = arrayListOf<WanAndroidClassifyBean>()
    private val mFragmentPagerCreator by lazy {
        FragmentPagerItems.with(mContext)
    }
    private val mViewPagerAdapter by lazy {
        FragmentStatePager2ItemAdapter(
            this,
            mFragmentPagerCreator.create()
        )
    }

    /**
     * 加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_vipcn

    /**
     * 初始化View
     */
    override fun initView() {
        // 模拟状态栏
        StatusBarUtil.setSmartPadding(mContext, mVwStatusBar)
        StatusBarUtil.setSmartMargin(mContext, mFlMagicIndicator)
        initViewPager2AndMagicIndicator(mViewPagerAdapter, mVpVipcn, mFlMagicIndicator)
    }

    override fun initListener() {
        mVipcnViewModel.apply {
            mClassifyDataUIState.mRefreshSuccess.observe(viewLifecycleOwner, Observer {
                when {
                    it -> showContent()
                    else -> showError()
                }
            })
            mVipcnClassify.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mClassifyData.clear()
                    mClassifyData.addAll(it)

                    mFragmentPagerCreator.create().clear()
                    mFragmentPagerCreator.let {
                        mClassifyData.forEach { classifyBean ->
                            it.add(
                                classifyBean.name.toHtml(),
                                WanAndroidVipcnChildFragment::class.java,
                                BundleBuilder.of()
                                    .putInt(
                                        WanAndroidConstant.WAN_ANDROID_CLASSIFY_ID,
                                        classifyBean.id
                                    )
                                    .get()
                            )
                        }
                    }
                    mFlMagicIndicator.navigator?.notifyDataSetChanged()
                    mVpVipcn.adapter?.notifyDataSetChanged()
                    mVpVipcn.offscreenPageLimit = mViewPagerAdapter.itemCount
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        // 获取公众号分类
        mVipcnViewModel.getVipcnClassify()
    }

    override fun isAlreadyLoadedData(): Boolean = mClassifyData.isNotEmpty()

    companion object {
        fun getInstance(): WanAndroidVipcnFragment {
            return WanAndroidVipcnFragment()
        }
    }
}