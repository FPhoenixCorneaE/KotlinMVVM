package com.fphoenixcorneae.rxretrofit.network.factory

import androidx.lifecycle.LiveData
import com.fphoenixcorneae.rxretrofit.network.BaseResponse
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @desc: LiveDataCallAdapterFactory
 */
class LiveDataCallAdapterFactory : Factory() {

    companion object {
        fun create(): LiveDataCallAdapterFactory {
            return LiveDataCallAdapterFactory()
        }
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) return null
        //获取第一个泛型类型
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(observableType)
        require(rawType == BaseResponse::class.java) { "type must be BaseResponse" }
        require(observableType is ParameterizedType) { "resource must be parameterized" }
        return LiveDataCallAdapter<Any>(observableType)
    }
}