package com.wkz.wanandroid.api

import androidx.lifecycle.LiveData
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @desc: WanAndroid Api接口
 * @date: 2019-10-28 16:00
 */
interface WanAndroidApi {

    /**
     * 首页Banner
     */
    @GET("banner/json")
    fun getBannerList(): LiveData<BaseResponse<List<WanAndroidBannerBean>>>

    /**
     * 首页文章列表
     */
    @GET("article/list/{page}/json")
    fun getArticleList(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageBean>>
}