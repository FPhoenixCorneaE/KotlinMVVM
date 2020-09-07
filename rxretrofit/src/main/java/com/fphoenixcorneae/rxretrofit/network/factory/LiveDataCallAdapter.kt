package com.fphoenixcorneae.rxretrofit.network.factory

import androidx.lifecycle.LiveData
import com.fphoenixcorneae.rxretrofit.network.BaseResponse
import com.fphoenixcorneae.rxretrofit.network.exception.ErrorStatus
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @desc: LiveDataCallAdapter
 */
class LiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<T>> {
    override fun adapt(call: Call<T>): LiveData<T> {
        return object : LiveData<T>() {
            private val started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                // 确保执行一次
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            val value = BaseResponse<T>(
                                ErrorStatus.UNKNOWN_ERROR,
                                t.message ?: "",
                                null
                            ) as T
                            postValue(value)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            when (response.code()) {
                                200 -> postValue(response.body())
                                else -> {
                                    val value = BaseResponse<T>(
                                        response.code(),
                                        response.message() ?: "",
                                        null
                                    ) as T
                                    postValue(value)
                                }
                            }
                        }
                    })
                }
            }
        }
    }

    override fun responseType() = responseType
}