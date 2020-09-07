package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class IoMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.io(), AndroidSchedulers.mainThread())
