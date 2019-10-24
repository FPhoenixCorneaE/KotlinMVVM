package com.wkz.wanandroid.mvvm.view.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.wkz.framework.base.fragment.BaseFragment
import com.wkz.wanandroid.R
import kotlinx.android.synthetic.main.wan_android_activity_home_article.*

/**
 * @desc: 首页文章Fragment
 * @date: 2019-10-24 15:51
 */
class WanAndroidHomeArticleFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.wan_android_activity_home_article

    override fun initView() {
        mRvArticle.layoutManager = LinearLayoutManager(mContext)
    }

    override fun lazyLoadData() {

    }

}