package com.wkz.wanandroid.api

import androidx.lifecycle.LiveData
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.wanandroid.mvvm.model.*
import retrofit2.http.*

/**
 * @desc: WanAndroid Api接口
 * @date: 2019-10-28 16:00
 * 注意：@POST请求方式没有参数时不能添加@FormUrlEncoded,有参数时要添加@FormUrlEncoded.
 */
interface WanAndroidApi {

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("username") userName: String,
        @Field("password") password: String
    ): LiveData<BaseResponse<WanAndroidUserInfoBean>>

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): LiveData<BaseResponse<Any>>

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

    /**
     * 收藏文章
     */
    @POST("/lg/collect/{id}/json")
    fun collectArticle(@Path("id") id: Int): LiveData<BaseResponse<Any>>

    /**
     * 取消收藏文章
     */
    @POST("/lg/uncollect_originId/{id}/json")
    fun unCollectArticle(@Path("id") id: Int): LiveData<BaseResponse<Any>>

    /**
     * 获取当前账户的个人积分
     */
    @GET("/lg/coin/userinfo/json")
    fun getIntegral(): LiveData<BaseResponse<WanAndroidIntegralBean>>

    /**
     * 获取积分排行榜
     */
    @GET("/coin/rank/{page}/json")
    fun getIntegralRanking(@Path("page") page: Int): LiveData<BaseResponse<WanAndroidIntegralRankingBean>>

    /**
     * 获取积分记录
     */
    @GET("/lg/coin/list/{page}/json")
    fun getIntegralRecord(@Path("page") page: Int): LiveData<BaseResponse<WanAndroidIntegralRecordBean>>
}