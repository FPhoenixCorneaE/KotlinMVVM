package com.wkz.kotlinmvvm.test

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface TestApi {

    // @Body表示输入的参数为非表单请求体
    @Headers("Content-Type: application/json; charset=UTF-8", "Accept: */*")
    @POST("app/gateway/30032/b2b/api/selectOrderRebateListByUserId")
    fun queryRebateListByUserId(@Body body: RequestBody): Observable<Any>

    @POST("app/gateway/30032/b2b/api/selectGoodsClass")
    // 表示提交表单数据，@Field注解键名
    @FormUrlEncoded
    fun queryGoodsClass(@Field("id") id: String): Observable<Any>

    @POST("/file/uploadImage")
    // 支持文件上传的表单
    @Multipart
    fun uploadImage(@Field("userId") userId: String, @Field("image") image: String, @Field("folder") folder: String): Observable<Any>
}