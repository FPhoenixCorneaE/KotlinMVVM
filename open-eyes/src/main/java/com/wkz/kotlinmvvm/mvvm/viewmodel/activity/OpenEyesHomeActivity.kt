package com.wkz.kotlinmvvm.mvvm.viewmodel.activity

import android.os.Bundle
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
        FragmentUtil.with(this)
            .setContainerViewId(R.id.mFlContainer)
            .setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out)
            .addFragment(OpenEyesHomeFragment.getInstance())
            .commit()
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}
