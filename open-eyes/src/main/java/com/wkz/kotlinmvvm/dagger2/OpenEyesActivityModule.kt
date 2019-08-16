package com.wkz.kotlinmvvm.dagger2

import com.wkz.kotlinmvvm.mvvm.model.OpenEyesHomeModel
import com.wkz.kotlinmvvm.mvvm.model.VideoDetailModel
import com.wkz.kotlinmvvm.mvvm.viewmodel.activity.OpenEyesHomeActivity
import com.wkz.kotlinmvvm.mvvm.viewmodel.activity.VideoDetailActivity
import com.wkz.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesHomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OpenEyesActivityModule {

    /**
     * 绑定首页Activity
     */
    @ContributesAndroidInjector(modules = [OpenEyesHomeModel::class])
    abstract fun bindOpenEyesHomeActivity(): OpenEyesHomeActivity

    /**
     * 绑定首页Fragment
     */
    @ContributesAndroidInjector(modules = [OpenEyesHomeModel::class])
    abstract fun bindOpenEyesHomeFragment(): OpenEyesHomeFragment

    /**
     * 绑定视频详情Activity
     */
    @ContributesAndroidInjector(modules = [VideoDetailModel::class])
    abstract fun bindOpenEyesVideoDetailActivity(): VideoDetailActivity
}