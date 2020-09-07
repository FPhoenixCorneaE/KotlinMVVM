package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewThreadMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.newThread(), AndroidSchedulers.mainThread())
