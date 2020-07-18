package com.wkz.wanandroid.api

import androidx.lifecycle.LiveData
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.wanandroid.mvvm.model.*
import retrofit2.http.*

/**
 * @desc: WanAndroid Api接口
 * @date: 2019-10-28 16:00
 * @注意：@POST请求方式没有参数时不能添加@FormUrlEncoded,有参数时要添加@FormUrlEncoded.
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
    fun getTopArticleList(): LiveData<BaseResponse<ArrayList<WanAndroidArticleBean>>>

    /**
     * 首页文章列表
     */
    @GET("article/list/{page}/json")
    fun getArticleList(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 首页问答列表
     */
    @GET("article/list/{page}/json")
    fun getQaList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 收藏文章
     */
    @POST("/lg/collect/{id}/json")
    fun collectArticle(
        @Path("id") id: Int
    ): LiveData<BaseResponse<Any>>

    /**
     * 取消收藏文章
     */
    @POST("/lg/uncollect_originId/{id}/json")
    fun cancelCollectArticle(
        @Path("id") id: Int
    ): LiveData<BaseResponse<Any>>

    /**
     * 获取热门搜索数据
     */
    @GET("hotkey/json")
    fun getHotSearchData(): LiveData<BaseResponse<ArrayList<WanAndroidSearchBean>>>

    /**
     * 根据关键词搜索数据
     */
    @POST("article/query/{page}/json")
    fun getSearchDataByKey(
        @Path("page") pageNo: Int,
        @Query("k") searchKey: String
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 项目分类
     */
    @GET("project/tree/json")
    fun getProjectClassify(): LiveData<BaseResponse<ArrayList<WanAndroidClassifyBean>>>

    /**
     * 根据分类id获取项目数据
     */
    @GET("project/list/{page}/json")
    fun getProjectDataByClassifyId(
        @Path("page") pageNo: Int,
        @Query("cid") cid: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 获取最新项目数据
     */
    @GET("article/listproject/{page}/json")
    fun getProjectNewestData(
        @Path("page") pageNo: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 公众号分类
     */
    @GET("wxarticle/chapters/json")
    fun getVipcnClassify(): LiveData<BaseResponse<ArrayList<WanAndroidClassifyBean>>>

    /**
     * 获取公众号数据
     */
    @GET("wxarticle/list/{id}/{page}/json")
    fun getVipcnDataByClassifyId(
        @Path("page") pageNo: Int,
        @Path("id") id: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 获取广场文章列表数据
     */
    @GET("user_article/list/{page}/json")
    fun getSquareArticleList(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>

    /**
     * 获取广场每日一问列表数据
     */
    @GET("wenda/list/{page}/json")
    fun getSquareAskList(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>


    /**
     * 获取广场体系数据
     */
    @GET("tree/json")
    fun getSquareSystem(): LiveData<BaseResponse<ArrayList<WanAndroidSystemBean>>>

    /**
     * 获取广场体系文章数据
     */
    @GET("article/list/{page}/json")
    fun getSquareSystemArticleBySystemId(
        @Path("page") pageNo: Int,
        @Query("cid") cid: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidArticleBean>>>>


    /**
     * 获取广场导航数据
     */
    @GET("navi/json")
    fun getSquareNavigation(): LiveData<BaseResponse<ArrayList<WanAndroidNavigationBean>>>

    /**
     * 获取当前账户的个人积分
     */
    @GET("/lg/coin/userinfo/json")
    fun getIntegral(): LiveData<BaseResponse<WanAndroidIntegralBean>>

    /**
     * 获取积分排行榜
     */
    @GET("/coin/rank/{page}/json")
    fun getIntegralRanking(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidIntegralBean>>>>

    /**
     * 获取积分记录
     */
    @GET("/lg/coin/list/{page}/json")
    fun getIntegralRecord(
        @Path("page") page: Int
    ): LiveData<BaseResponse<WanAndroidPageResponse<ArrayList<WanAndroidIntegralRecordBean>>>>
}