package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SingleMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.single(), AndroidSchedulers.mainThread())
