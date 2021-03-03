package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @desc：为每个任务创建一个新线程
 * @date：2021/3/3 17:17
 */
class NewThreadMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.newThread(), AndroidSchedulers.mainThread())
