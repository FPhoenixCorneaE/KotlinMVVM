package com.wkz.rxretrofit.scheduler

object SchedulerManager {

    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
}
