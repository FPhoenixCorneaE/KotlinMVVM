package com.wkz.wanandroid.api

import androidx.lifecycle.LiveData
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @desc: WanAndroid Api接口
 * @date: 2019-10-28 16:00
 */
interface WanAndroidApi {

    /**
     * 首页Banner
     */
    @GET("banner/json")
    fun getBannerList(): LiveData<BaseResponse<ArrayList<WanAndroidBannerBean>>>

    /**
     * 首页文章列表
     */
    @GET("article/list/{page}/json")
    fun getArticleList(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageBean>>

    /**
     * 首页问答列表
     */
    @GET("article/list/{page}/json")
    fun getQaList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): LiveData<BaseResponse<WanAndroidPageBean>>
}