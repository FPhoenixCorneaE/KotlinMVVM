package com.wkz.wanandroid.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.wkz.extension.isNull
import com.wkz.rxretrofit.network.BaseResponse
import com.wkz.rxretrofit.network.IBaseUrl
import com.wkz.rxretrofit.network.RetrofitManager
import com.wkz.util.AppUtil
import com.wkz.util.DeviceIdUtil
import com.wkz.util.LanguageUtil
import com.wkz.wanandroid.api.WanAndroidApi
import com.wkz.wanandroid.api.WanAndroidUrlConstant
import com.wkz.wanandroid.mvvm.model.WanAndroidPageBean
import com.wkz.wanandroid.mvvm.model.WanAndroidUiState

open class WanAndroidBaseViewModel : ViewModel(), IBaseUrl {

    protected val sWanAndroidService: WanAndroidApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.addQueryParameter("deviceId", DeviceIdUtil.uniqueID)
            .addHeader("deviceModel", AppUtil.getMobileModel())
            .addHeader("versionCode", AppUtil.versionCode.toString())
            .addHeader("versionName", AppUtil.versionName)
            .addHeader("language", LanguageUtil.currentLocale.toString())
            .addHeader("sign", AppUtil.getSign())
            .getRetrofit(this)
            .create(WanAndroidApi::class.java)
    }

    override fun getBaseUrl(): String = WanAndroidUrlConstant.BASE_URL

    /**
     * 设置数组列表数据Ui状态
     */
    protected fun <T> BaseResponse<ArrayList<T>>.setArrayListDataUiState(uiState: WanAndroidUiState): ArrayList<T>? {
        uiState.mRefreshing.value = false
        uiState.mLoadingMore.value = false
        return when {
            data.isNull() -> {
                uiState.mRefreshSuccess.value = false
                data
            }
            else -> {
                data?.apply {
                    when {
                        isWanAndroidSuccess() -> {
                            uiState.mRefreshSuccess.value = true
                            when {
                                isEmpty() -> {
                                    uiState.mRefreshNoData.value = true
                                }
                            }
                        }
                        else -> {
                            uiState.mRefreshSuccess.value = false
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置分页数据Ui状态
     */
    protected fun <T> BaseResponse<WanAndroidPageBean<T>>.setPageDataUiState(uiState: WanAndroidUiState): WanAndroidPageBean<T>? {
        uiState.mRefreshing.value = false
        uiState.mLoadingMore.value = false
        return when {
            data.isNull() -> {
                uiState.mRefreshSuccess.value = false
                data
            }
            else -> {
                data?.apply {
                    when {
                        isWanAndroidSuccess() -> {
                            when {
                                isRefreshNoData() -> {
                                    uiState.mRefreshNoData.value = true
                                }
                                isRefreshWithData() -> {
                                    uiState.mRefreshSuccess.value = true
                                    when {
                                        isLoadMoreNoData() -> {
                                            uiState.mLoadMoreNoData.value = true
                                        }
                                    }
                                }
                                isLoadMoreNoData() -> {
                                    uiState.mLoadMoreNoData.value = true
                                }
                                else -> {
                                    uiState.mLoadMoreSuccess.value = true
                                }
                            }
                        }
                        isRefresh() -> {
                            uiState.mRefreshSuccess.value = false
                        }
                        isLoadMore() -> {
                            uiState.mLoadMoreSuccess.value = false
                        }
                    }
                }
            }
        }
    }
}