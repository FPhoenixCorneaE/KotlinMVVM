package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @desc：用于计算任务，如事件循环或和回调处理，不要用于IO操作(IO操作请使用Schedulers.io())；
 * 默认线程数等于处理器的数量
 * @date：2021/3/3 17:16
 */
class ComputationMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.computation(), AndroidSchedulers.mainThread())
