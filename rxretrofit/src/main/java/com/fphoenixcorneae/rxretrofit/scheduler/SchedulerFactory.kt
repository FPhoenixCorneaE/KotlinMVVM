package com.fphoenixcorneae.rxretrofit.scheduler

/**
 * @desc：调度器工厂
 * @date：2021/3/3 17:15
 */
object SchedulerFactory {

    /**
     * 用于计算任务，如事件循环或和回调处理，不要用于IO操作(IO操作请使用Schedulers.io())；
     * 默认线程数等于处理器的数量
     */
    fun <T> computationToMain(): ComputationMainScheduler<T> {
        return ComputationMainScheduler()
    }

    /**
     * 用于IO密集型任务，如异步阻塞IO操作，这个调度器的线程池会根据需要增长；
     * 对于普通的计算任务，请使用Schedulers.computation()；
     * Schedulers.io()默认是一个CachedThreadScheduler，很像一个有线程缓存的新线程调度器
     */
    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }

    /**
     * 为每个任务创建一个新线程
     */
    fun <T> newThreadToMain(): NewThreadMainScheduler<T> {
        return NewThreadMainScheduler()
    }

    /**
     * 该调度器的线程池只能同时执行一个线程
     */
    fun <T> singleToMain(): SingleMainScheduler<T> {
        return SingleMainScheduler()
    }

    /**
     * 当其它排队的任务完成后，在当前线程排队开始执行
     */
    fun <T> trampolineToMain(): TrampolineMainScheduler<T> {
        return TrampolineMainScheduler()
    }
}
