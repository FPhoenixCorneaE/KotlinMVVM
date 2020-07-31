package com.wkz.wanandroid.mvvm.view.fragment.project

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
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidProjectViewModel
import kotlinx.android.synthetic.main.wan_android_fragment_project.*

/**
 * @desc: 项目Fragment
 * @date: 2020-06-14 17:31
 */
class WanAndroidProjectFragment : WanAndroidBaseFragment() {

    /* 项目ViewModel */
    private val mProjectViewModel by viewModel<WanAndroidProjectViewModel>()

    /* 项目分类数据 */
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
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_project

    /**
     * 初始化View
     */
    override fun initView() {
        // 模拟状态栏
        StatusBarUtil.setSmartPadding(mContext, mVwStatusBar)
        StatusBarUtil.setSmartMargin(mContext, mFlMagicIndicator)
        initViewPager2AndMagicIndicator(mViewPagerAdapter, mVpProject, mFlMagicIndicator)
    }

    override fun initListener() {
        mProjectViewModel.apply {
            mProjectClassify.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mClassifyData.clear()
                    mClassifyData.add(WanAndroidClassifyBean(name = getString(R.string.wan_android_project_newest)))
                    mClassifyData.addAll(it)

                    mFragmentPagerCreator.create().clear()
                    mFragmentPagerCreator.let {
                        mClassifyData.forEach { classifyBean ->
                            it.add(
                                classifyBean.name.toHtml(),
                                WanAndroidProjectChildFragment::class.java,
                                BundleBuilder.of()
                                    .putInt(
                                        WanAndroidConstant.WAN_ANDROID_CLASSIFY_ID,
                                        classifyBean.id
                                    )
                                    .putBoolean(
                                        WanAndroidConstant.WAN_ANDROID_NEWEST_PROJECT,
                                        classifyBean.id == 0
                                    )
                                    .get()
                            )
                        }
                    }
                    mFlMagicIndicator.navigator?.notifyDataSetChanged()
                    mVpProject.adapter?.notifyDataSetChanged()
                    mVpProject.offscreenPageLimit = mViewPagerAdapter.itemCount
                }
            })
        }
    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        // 获取项目分类
        mProjectViewModel.getProjectClassify()
    }

    override fun isAlreadyLoadedData(): Boolean = mClassifyData.isNotEmpty()

    companion object {
        fun getInstance(): WanAndroidProjectFragment {
            return WanAndroidProjectFragment()
        }
    }
}