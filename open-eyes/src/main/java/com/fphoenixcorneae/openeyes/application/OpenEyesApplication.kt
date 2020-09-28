package com.fphoenixcorneae.openeyes.application

import android.app.Activity
import com.fphoenixcorneae.framework.base.BaseApplication
import com.fphoenixcorneae.openeyes.dagger2.DaggerOpenEyesAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class OpenEyesApplication : BaseApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        // Dagger2注入依赖
        DaggerOpenEyesAppComponent.builder().create(this).inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector
}