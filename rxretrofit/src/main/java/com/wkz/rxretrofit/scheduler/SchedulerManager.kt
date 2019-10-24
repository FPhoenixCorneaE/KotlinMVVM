package com.wkz.rxretrofit.scheduler

object SchedulerManager {

    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
    fun <T> computationToMain(): ComputationMainScheduler<T> {
        return ComputationMainScheduler()
    }
    fun <T> newThreadToMain(): NewThreadMainScheduler<T> {
        return NewThreadMainScheduler()
    }
    fun <T> singleToMain(): SingleMainScheduler<T> {
        return SingleMainScheduler()
    }
    fun <T> trampolineToMain(): TrampolineMainScheduler<T> {
        return TrampolineMainScheduler()
    }
}
