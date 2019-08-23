package com.wkz.kotlinmvvm.test

import com.wkz.rxretrofit.network.IBaseUrl
import com.wkz.rxretrofit.network.RetrofitManager
import com.wkz.rxretrofit.scheduler.SchedulerManager
import okhttp3.RequestBody

class TestModel : IBaseUrl {

    val sService: TestApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit(this).create(TestApi::class.java)
    }

    override fun getBaseUrl(): String {
        return "http://ec2-52-83-156-42.cn-northwest-1.compute.amazonaws.com.cn:30001/"
    }

    fun queryRebateListByUserId(body: RequestBody) {
        sService.queryRebateListByUserId(body)
            .compose(SchedulerManager.ioToMain())
            .subscribe()
    }

    fun queryGoodsClass(id: String) {
        sService.queryGoodsClass(id)
            .compose(SchedulerManager.ioToMain())
            .subscribe()
    }

    fun uploadImage(userId: String, image: String, folder: String) {
        sService.uploadImage(userId, image, folder)
            .compose(SchedulerManager.ioToMain())
            .subscribe()
    }
}