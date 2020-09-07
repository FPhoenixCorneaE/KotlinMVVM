package com.fphoenixcorneae.kotlinmvvm.dagger2

import com.fphoenixcorneae.kotlinmvvm.application.OpenEyesApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        OpenEyesActivityModule::class
    ]
)
interface OpenEyesAppComponent : AndroidInjector<OpenEyesApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<OpenEyesApplication>()
}