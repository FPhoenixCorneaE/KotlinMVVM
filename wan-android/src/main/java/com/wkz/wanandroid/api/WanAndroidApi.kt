package com.wkz.wanandroid.api

import androidx.lifecycle.LiveData
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.wanandroid.mvvm.model.WanAndroidBannerBean
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import com.wkz.wanandroid.mvvm.model.WanAndroidUserInfoBean
import retrofit2.http.*

/**
 * @desc: WanAndroid Api接口
 * @date: 2019-10-28 16:00
 */
interface WanAndroidApi {

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username") userName: String, @Field("password") password: String): LiveData<BaseResponse<WanAndroidUserInfoBean>>

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("username") userName: String, @Field("password") password: String, @Field("repassword") rePassword: String): LiveData<BaseResponse<Any>>

    /**
     * 首页Banner
     */
    @GET("banner/json")
    fun getBannerList(): LiveData<BaseResponse<ArrayList<WanAndroidBannerBean>>>

    /**
     * 首页置顶文章集合数据
     */
    @GET("article/top/json")
    fun getTopArticleList(): LiveData<BaseResponse<ArrayList<WanAndroidPageBean.ArticleBean>>>

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