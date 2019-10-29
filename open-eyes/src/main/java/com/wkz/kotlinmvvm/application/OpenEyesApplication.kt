package com.wkz.kotlinmvvm.application

import android.app.Activity
import com.wkz.framework.base.BaseApplication
import com.wkz.kotlinmvvm.dagger2.DaggerOpenEyesAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class OpenEyesApplication : BaseApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        // Dagger2注入依赖
        DaggerOpenEyesAppComponent.builder().create(this).inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector
}