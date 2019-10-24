package com.wkz.kotlinmvvm.mvvm.viewmodel.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.wkz.framework.base.activity.Dagger2InjectionActivity
import com.wkz.kotlinmvvm.R
import com.wkz.kotlinmvvm.mvvm.contract.OpenEyesHomeContract
import com.wkz.kotlinmvvm.mvvm.presenter.OpenEyesHomePresenter
import com.wkz.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesHomeFragment
import com.wkz.util.FragmentUtil

class OpenEyesHomeActivity :
    Dagger2InjectionActivity<OpenEyesHomeContract.View, OpenEyesHomePresenter>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_home

    override fun initView() {
        FragmentUtil.addFragment(mContext as FragmentActivity, R.id.mFlContainer, OpenEyesHomeFragment.getInstance(), Bundle(), false)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}
