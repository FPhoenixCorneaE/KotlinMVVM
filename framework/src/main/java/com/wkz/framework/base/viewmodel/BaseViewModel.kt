package com.wkz.framework.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wkz.rxretrofit.network.IBaseUrl

abstract class BaseViewModel : AutoDisposeViewModel(), IBaseUrl {

    protected val mThrowable: MutableLiveData<Throwable> = MutableLiveData()

    fun getThrowable(): LiveData<Throwable> = mThrowable
}