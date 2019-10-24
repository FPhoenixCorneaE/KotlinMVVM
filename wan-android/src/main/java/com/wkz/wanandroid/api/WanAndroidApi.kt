package com.wkz.wanandroid.api

import androidx.lifecycle.LiveData
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @desc: WanAndroid Api接口
 */
interface WanAndroidApi {

    /**
     * 文章列表
     */
    @GET("article/list/{page}/json")
    fun getArticleList(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageBean>>
}