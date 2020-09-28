package com.fphoenixcorneae.openeyes.dagger2

import com.fphoenixcorneae.openeyes.mvvm.model.*
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity.OpenEyesHomeActivity
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.activity.OpenEyesVideoDetailActivity
import com.fphoenixcorneae.openeyes.mvvm.viewmodel.fragment.*
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
     * 绑定热门Fragment
     */
    @ContributesAndroidInjector(modules = [OpenEyesHotModel::class])
    abstract fun bindOpenEyesHotFragment(): OpenEyesHotFragment

    /**
     * 绑定排行榜Fragment
     */
    @ContributesAndroidInjector(modules = [OpenEyesRankModel::class])
    abstract fun bindOpenEyesRankFragment(): OpenEyesRankFragment

    /**
     * 绑定视频详情Activity
     */
    @ContributesAndroidInjector(modules = [OpenEyesVideoDetailModel::class])
    abstract fun bindOpenEyesVideoDetailActivity(): OpenEyesVideoDetailActivity
}