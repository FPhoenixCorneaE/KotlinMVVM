package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @desc：该调度器的线程池只能同时执行一个线程
 * @date：2021/3/3 17:17
 */
class SingleMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.single(), AndroidSchedulers.mainThread())
