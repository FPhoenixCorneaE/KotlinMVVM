package com.wkz.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewThreadMainScheduler<T> private constructor() : BaseScheduler<T>(Schedulers.newThread(), AndroidSchedulers.mainThread())
