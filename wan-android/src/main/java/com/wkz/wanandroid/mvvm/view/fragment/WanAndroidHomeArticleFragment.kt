//package com.wkz.wanandroid.mvvm.view.fragment
//
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.orhanobut.logger.Logger
//import com.wkz.adapter.internal.MultiTypeAdapter
//import com.wkz.extension.viewModel
//import com.wkz.framework.base.fragment.BaseFragment
//import com.wkz.wanandroid.R
//import com.wkz.wanandroid.mvvm.view.wrapper.WanAndroidHomeArticleWrapper
//import com.wkz.wanandroid.mvvm.viewmodel.WanAndroidHomeArticleViewModel
//import kotlinx.android.synthetic.main.wan_android_activity_home_article.*
//
///**
// * @desc: 首页文章Fragment
// * @date: 2019-10-24 15:51
// */
//class WanAndroidHomeArticleFragment : BaseFragment() {
//
//    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
//        MultiTypeAdapter()
//    }
//
//    private val mHomeArticleWrapper by lazy(LazyThreadSafetyMode.NONE) {
//        WanAndroidHomeArticleWrapper()
//    }
//
//    private val mHomeArticleViewModel by viewModel<WanAndroidHomeArticleViewModel>()
//
//    /**
//     * 加载布局
//     */
//    override fun getLayoutId(): Int = R.layout.wan_android_activity_home_article
//
//    /**
//     * 初始化 View
//     */
//    override fun initView() {
//        initRecyclerView()
//    }
//
//    private fun initRecyclerView() {
//        mHomeArticleWrapper.apply {
//            setOnItemClickListener { viewHolder, position, item ->
//
//            }
//        }
//        mAdapter.register(mHomeArticleWrapper)
//        mRvArticle.apply {
//            layoutManager = LinearLayoutManager(mContext)
//            adapter = mAdapter
//        }
//        mHomeArticleViewModel.mArticleList.observe(this, Observer {
//            Logger.e(it.data.toString())
//        })
//    }
//
//    /**
//     * 懒加载数据
//     */
//    override fun lazyLoadData() {
//
//    }
//}