package com.wkz.wanandroid.mvvm.view.fragment

import androidx.lifecycle.Observer
import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.extension.toHtml
import com.wkz.extension.viewModel
import com.wkz.util.BundleBuilder
import com.wkz.util.statusbar.StatusBarUtil
import com.wkz.wanandroid.R
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidClassifyBean
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidVipcnViewModel
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