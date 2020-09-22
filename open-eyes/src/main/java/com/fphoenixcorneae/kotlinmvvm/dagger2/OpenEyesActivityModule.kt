package com.fphoenixcorneae.kotlinmvvm.dagger2

import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesCategoryModel
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesFollowModel
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesHomeModel
import com.fphoenixcorneae.kotlinmvvm.mvvm.model.OpenEyesVideoDetailModel
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.activity.OpenEyesHomeActivity
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.activity.OpenEyesVideoDetailActivity
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesCategoryFragment
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesFollowFragment
import com.fphoenixcorneae.kotlinmvvm.mvvm.viewmodel.fragment.OpenEyesHomeFragment
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
     * 绑定关注Fragment
     */
    @ContributesAndroidInjector(modules = [OpenEyesFollowModel::class])
    abstract fun bindOpenEyesFollowFragment(): OpenEyesFollowFragment

    /**
     * 绑定分类Fragment
     */
    @ContributesAndroidInjector(modules = [OpenEyesCategoryModel::class])
    abstract fun bindOpenEyesCategoryFragment(): OpenEyesCategoryFragment

    /**
     * 绑定视频详情Activity
     */
    @ContributesAndroidInjector(modules = [OpenEyesVideoDetailModel::class])
    abstract fun bindOpenEyesVideoDetailActivity(): OpenEyesVideoDetailActivity
}