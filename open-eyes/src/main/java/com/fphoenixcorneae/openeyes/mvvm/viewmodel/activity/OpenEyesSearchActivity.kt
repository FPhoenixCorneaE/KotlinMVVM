package com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity

import android.os.Bundle
import com.fphoenixcorneae.openeyes.R
import com.fphoenixcorneae.openeyes.mvvm.contract.OpenEyesSearchContract
import com.fphoenixcorneae.openeyes.mvvm.presenter.OpenEyesSearchPresenter

/**
 * @desc 搜索 Activity
 */
class OpenEyesSearchActivity :
    OpenEyesBaseDagger2Activity<OpenEyesSearchContract.View, OpenEyesSearchPresenter>() {

    override fun getLayoutId(): Int = R.layout.open_eyes_activity_search

    override fun initView() {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}