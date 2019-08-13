package com.wkz.kotlinmvvm.viewmodel.fragment

import com.wkz.framework.base.BaseFragment
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesFragmentHomeBinding
import com.wkz.kotlinmvvm.mvp.contract.OpenEyesHomeContract
import com.wkz.kotlinmvvm.mvp.model.bean.HomeBean
import com.wkz.kotlinmvvm.mvp.presenter.OpenEyesHomePresenter

class OpenEyesHomeFragment :
    BaseFragment<OpenEyesHomeContract.View, OpenEyesHomePresenter, OpenEyesFragmentHomeBinding>(),
    OpenEyesHomeContract.View {
    override fun getLayoutId(): Int = R.layout.open_eyes_fragment_home

    override fun initView() {
    }

    override fun lazyLoadData() {
    }

    override fun setHomeData(homeBean: HomeBean) {

    }

    override fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>) {

    }
}
