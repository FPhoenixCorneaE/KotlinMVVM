package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @desc：用于IO密集型任务，如异步阻塞IO操作，这个调度器的线程池会根据需要增长；
 * 对于普通的计算任务，请使用Schedulers.computation()；
 * Schedulers.io()默认是一个CachedThreadScheduler，很像一个有线程缓存的新线程调度器
 * @date：2021/3/3 17:17
 */
class IoMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.io(), AndroidSchedulers.mainThread())
