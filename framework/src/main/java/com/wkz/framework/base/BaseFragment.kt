package com.wkz.framework.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * desc:BaseFragment基类
 */
abstract class BaseFragment : Fragment() {
    /**
     * 视图是否加载完毕
     */
    private var isViewPrepared = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadedData = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), null)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepared = true
        initView()
        lazyLoadDataIfPrepared()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepared && !hasLoadedData) {
            lazyLoad()
            hasLoadedData = true
        }
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()
}
