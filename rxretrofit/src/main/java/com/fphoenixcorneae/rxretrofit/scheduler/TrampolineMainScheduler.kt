package com.fphoenixcorneae.rxretrofit.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @desc：当其它排队的任务完成后，在当前线程排队开始执行
 * @date：2021/3/3 17:18
 */
class TrampolineMainScheduler<T> internal constructor() :
    BaseScheduler<T>(Schedulers.trampoline(), AndroidSchedulers.mainThread())
