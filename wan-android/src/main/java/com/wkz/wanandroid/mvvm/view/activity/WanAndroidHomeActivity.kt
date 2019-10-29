package com.wkz.wanandroid.mvvm.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import com.wkz.adapter.internal.MultiTypeAdapter
import com.wkz.extension.viewModel
import com.wkz.framework.base.activity.BaseActivity
import com.wkz.wanandroid.R
import com.wkz.wanandroid.mvvm.view.wrapper.WanAndroidHomeArticleWrapper
import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidHomeArticleViewModel
import kotlinx.android.synthetic.main.wan_android_activity_home.*

/**
 * @desc: 首页Activity
 * @date: 2019-10-28 16:04
 */
class WanAndroidHomeActivity : BaseActivity() {

    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MultiTypeAdapter()
    }

    private val mHomeArticleWrapper by lazy(LazyThreadSafetyMode.NONE) {
        WanAndroidHomeArticleWrapper()
    }

    private val mHomeArticleViewModel by viewModel<WanAndroidHomeArticleViewModel>()

    /**
     *  加载布局
     */
    override fun getLayoutId(): Int = R.layout.wan_android_activity_home

    /**
     * 初始化 View
     */
    override fun initView() {
        initRecyclerView()
        mHomeArticleViewModel.autoRefresh()
    }

    /**
     * 初始化数据
     */
    override fun initData(savedInstanceState: Bundle?) {

    }

    private fun initRecyclerView() {
        mHomeArticleWrapper.apply {
            setOnItemClickListener { viewHolder, position, item ->

            }
        }
        mAdapter.register(mHomeArticleWrapper)
        mRvArticle?.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mAdapter
        }
        mHomeArticleViewModel.mArticleList.observe(this, Observer {
            Logger.e(it.toString())
        })
    }
}