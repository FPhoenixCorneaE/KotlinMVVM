package com.wkz.kotlinmvvm.viewmodel.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.wkz.framework.base.BaseActivity
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.databinding.OpenEyesActivityHomeBinding
import com.wkz.kotlinmvvm.mvp.contract.OpenEyesHomeContract
import com.wkz.kotlinmvvm.mvp.presenter.OpenEyesHomePresenter
import com.wkz.kotlinmvvm.viewmodel.fragment.OpenEyesHomeFragment
import com.wkz.util.FragmentUtil

class OpenEyesHomeActivity :
    BaseActivity<OpenEyesHomeContract.View, OpenEyesHomePresenter, OpenEyesActivityHomeBinding>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_home

    override fun initView() {
        FragmentUtil.addFragment(mContext as FragmentActivity, R.id.mFlContainer, OpenEyesHomeFragment.getInstance(), Bundle(), false)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}
