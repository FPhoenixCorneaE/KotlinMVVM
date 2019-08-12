package com.wkz.kotlinmvvm.dagger2

import com.wkz.kotlinmvvm.mvp.model.VideoDetailModel
import com.wkz.kotlinmvvm.viewmodel.activity.VideoDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OpenEyesActivityModule {

    /**
     * 绑定视频详情Activity
     */
    @ContributesAndroidInjector(modules = [VideoDetailModel::class])
    abstract fun bindVideoDetailActivity(): VideoDetailActivity
}