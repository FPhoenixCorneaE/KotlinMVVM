package com.fphoenixcorneae.openeyes.test

import com.fphoenixcorneae.rxretrofit.network.IBaseUrl
import com.fphoenixcorneae.rxretrofit.network.RetrofitManager
import com.fphoenixcorneae.rxretrofit.scheduler.SchedulerFactory
import okhttp3.RequestBody

class TestModel : IBaseUrl {

    val sService: TestApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager
            .addHeader("thingName", "2f0879bde06e4dd9a62ac791817d4100")
            .addHeader("language", "zh-CN")
            .addHeader("deviceId", "2f0879bde06e4dd9a62ac791817d4100")
            .addHeader(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36"
            )
            .getRetrofit(this)
            .create(TestApi::class.java)
    }

    override fun getBaseUrl(): String {
//        return "http://ec2-52-83-156-42.cn-northwest-1.compute.amazonaws.com.cn:30001/"
        return "https://ceczc1l268.execute-api.cn-northwest-1.amazonaws.com.cn/"
    }

    fun queryRebateListByUserId(body: RequestBody) {
        sService.queryRebateListByUserId(body)
            .compose(SchedulerFactory.ioToMain())
            .subscribe()
    }

    fun queryGoodsClass(id: String) {
        sService.queryGoodsClass(id)
            .compose(SchedulerFactory.ioToMain())
            .subscribe()
    }

    fun uploadImage(userId: String, image: String, folder: String) {
        sService.uploadImage(userId, image, folder)
            .compose(SchedulerFactory.ioToMain())
            .subscribe()
    }

    fun getBindQrCode(orderId: String, deviceId: String) {
        sService.getBindQrCode(orderId, deviceId)
            .compose(SchedulerFactory.ioToMain())
            .subscribe()
    }
}