package com.wkz.wanandroid.mvvm.view.fragment.square

import com.wkz.adapter.app.FragmentPagerItems
import com.wkz.adapter.viewpager2.FragmentStatePager2ItemAdapter
import com.wkz.extension.toHtml
import com.wkz.util.BundleBuilder
import com.wkz.wanandroid.R
import com.wkz.wanandroid.constant.WanAndroidConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidSystemBean
import com.wkz.wanandroid.mvvm.view.fragment.WanAndroidBaseFragment
import kotlinx.android.synthetic.main.wan_android_fragment_square_system_article.*

/**
 * @desc: 广场体系文章Fragment
 * @date: 2020-08-27 14:15
 */
class WanAndroidSquareSystemArticleFragment : WanAndroidBaseFragment() {

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
    override fun getLayoutId(): Int = R.layout.wan_android_fragment_square_system_article

    /**
     * 初始化View
     */
    override fun initView() {
        mTbTitleBar.init()
        initViewPager2AndMagicIndicator(
            mViewPagerAdapter,
            mVpSquareSystemArticle,
            mFlMagicIndicator
        )
    }


    override fun initListener() {

    }

    /**
     * 懒加载数据
     */
    override fun lazyLoadData() {
        arguments?.apply {
            val systemBean =
                getParcelable(WanAndroidConstant.WAN_ANDROID_SQUARE_SYSTEM_NAME) as? WanAndroidSystemBean
            systemBean?.apply {
                mTbTitleBar.centerTextView?.text = name
                mFragmentPagerCreator.apply {
                    create().clear()
                    children.forEach {
                        add(
                            it.name.toHtml(),
                            WanAndroidSquareSystemArticleChildFragment::class.java,
                            BundleBuilder.of()
                                .putInt(WanAndroidConstant.WAN_ANDROID_SQUARE_SYSTEM_ID, it.id)
                                .get()
                        )
                    }
                }
                mFlMagicIndicator.navigator?.notifyDataSetChanged()
                mVpSquareSystemArticle.adapter?.notifyDataSetChanged()
                mVpSquareSystemArticle.offscreenPageLimit = mViewPagerAdapter.itemCount
                val position = getInt(WanAndroidConstant.WAN_ANDROID_POSITION)
                mVpSquareSystemArticle.setCurrentItem(position, false)
            }
        }
    }

    override fun isAlreadyLoadedData(): Boolean = true
}