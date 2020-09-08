package com.fphoenixcorneae.util

import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import com.fphoenixcorneae.ext.loggerE
import com.fphoenixcorneae.ext.loggerI
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 线程工具类
 */
object ThreadUtil {
    private val TYPE_PRIORITY_POOLS: MutableMap<Int, MutableMap<Int, ExecutorService?>> = HashMap()
    private val TASK_TASKINFO_MAP: MutableMap<Task<*>, TaskInfo> =
        ConcurrentHashMap()
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val TIMER = Timer()
    private const val TYPE_SINGLE: Byte = -1
    private const val TYPE_CACHED: Byte = -2
    private const val TYPE_IO: Byte = -4
    private const val TYPE_CPU: Byte = -8
    private var sDeliver: Executor? = null
    /**
     * Return whether the thread is the main thread.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    /**
     * Return a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param size The size of thread in the pool.
     * @return a fixed thread pool
     */
    fun getFixedPool(@IntRange(from = 1) size: Int): ExecutorService? {
        return getPoolByTypeAndPriority(size)
    }

    /**
     * Return a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param size     The size of thread in the pool.
     * @param priority The priority of thread in the poll.
     * @return a fixed thread pool
     */
    fun getFixedPool(
        @IntRange(from = 1) size: Int,
        @IntRange(from = 1, to = 10) priority: Int
    ): ExecutorService? {
        return getPoolByTypeAndPriority(size, priority)
    }

    /**
     * Return a thread pool that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed.
     *
     * @return a single thread pool
     */
    val singlePool: ExecutorService?
        get() = getPoolByTypeAndPriority(TYPE_SINGLE.toInt())

    /**
     * Return a thread pool that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed.
     *
     * @param priority The priority of thread in the poll.
     * @return a single thread pool
     */
    fun getSinglePool(
        @IntRange(
            from = 1,
            to = 10
        ) priority: Int
    ): ExecutorService? {
        return getPoolByTypeAndPriority(
            TYPE_SINGLE.toInt(),
            priority
        )
    }

    /**
     * Return a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.
     *
     * @return a cached thread pool
     */
    val cachedPool: ExecutorService?
        get() = getPoolByTypeAndPriority(TYPE_CACHED.toInt())

    /**
     * Return a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.
     *
     * @param priority The priority of thread in the poll.
     * @return a cached thread pool
     */
    fun getCachedPool(
        @IntRange(
            from = 1,
            to = 10
        ) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService? {
        return getPoolByTypeAndPriority(
            TYPE_CACHED.toInt(),
            priority
        )
    }

    /**
     * Return a thread pool that creates (2 * CPU_COUNT + 1) threads
     * operating off a queue which size is 128.
     *
     * @return a IO thread pool
     */
    val ioPool: ExecutorService?
        get() = getPoolByTypeAndPriority(TYPE_IO.toInt())

    /**
     * Return a thread pool that creates (2 * CPU_COUNT + 1) threads
     * operating off a queue which size is 128.
     *
     * @param priority The priority of thread in the poll.
     * @return a IO thread pool
     */
    fun getIoPool(
        @IntRange(
            from = 1,
            to = 10
        ) priority: Int
    ): ExecutorService? {
        return getPoolByTypeAndPriority(
            TYPE_IO.toInt(),
            priority
        )
    }

    /**
     * Return a thread pool that creates (CPU_COUNT + 1) threads
     * operating off a queue which size is 128 and the maximum
     * number of threads equals (2 * CPU_COUNT + 1).
     *
     * @return a cpu thread pool for
     */
    val cpuPool: ExecutorService?
        get() = getPoolByTypeAndPriority(TYPE_CPU.toInt())

    /**
     * Return a thread pool that creates (CPU_COUNT + 1) threads
     * operating off a queue which size is 128 and the maximum
     * number of threads equals (2 * CPU_COUNT + 1).
     *
     * @param priority The priority of thread in the poll.
     * @return a cpu thread pool for
     */
    fun getCpuPool(
        @IntRange(
            from = 1,
            to = 10
        ) priority: Int
    ): ExecutorService? {
        return getPoolByTypeAndPriority(
            TYPE_CPU.toInt(),
            priority
        )
    }

    /**
     * Executes the given task in a fixed thread pool.
     *
     * @param size The size of thread in the fixed thread pool.
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeByFixed(@IntRange(from = 1) size: Int, task: Task<T>) {
        execute(
            getPoolByTypeAndPriority(size),
            task
        )
    }

    /**
     * Executes the given task in a fixed thread pool.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByFixed(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(
            getPoolByTypeAndPriority(
                size,
                priority
            ), task
        )
    }

    /**
     * Executes the given task in a fixed thread pool after the given delay.
     *
     * @param size  The size of thread in the fixed thread pool.
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeByFixedWithDelay(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                size
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a fixed thread pool after the given delay.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByFixedWithDelay(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                size,
                priority
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size   The size of thread in the fixed thread pool.
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                size
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                size,
                priority
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size         The size of thread in the fixed thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                size
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size         The size of thread in the fixed thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                size,
                priority
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a single thread pool.
     *
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeBySingle(task: Task<T>) {
        execute(
            getPoolByTypeAndPriority(TYPE_SINGLE.toInt()),
            task
        )
    }

    /**
     * Executes the given task in a single thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeBySingle(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt(),
                priority
            ), task
        )
    }

    /**
     * Executes the given task in a single thread pool after the given delay.
     *
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeBySingleWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt()
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a single thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeBySingleWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt(),
                priority
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt()
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt(),
                priority
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt()
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_SINGLE.toInt(),
                priority
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a cached thread pool.
     *
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeByCached(task: Task<T>) {
        execute(
            getPoolByTypeAndPriority(TYPE_CACHED.toInt()),
            task
        )
    }

    /**
     * Executes the given task in a cached thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByCached(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt(),
                priority
            ), task
        )
    }

    /**
     * Executes the given task in a cached thread pool after the given delay.
     *
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeByCachedWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt()
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a cached thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByCachedWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt(),
                priority
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt()
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt(),
                priority
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt()
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CACHED.toInt(),
                priority
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in an IO thread pool.
     *
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeByIo(task: Task<T>) {
        execute(
            getPoolByTypeAndPriority(TYPE_IO.toInt()),
            task
        )
    }

    /**
     * Executes the given task in an IO thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByIo(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt(),
                priority
            ), task
        )
    }

    /**
     * Executes the given task in an IO thread pool after the given delay.
     *
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeByIoWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt()
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in an IO thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByIoWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt(),
                priority
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt()
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt(),
                priority
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt()
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_IO.toInt(),
                priority
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool.
     *
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeByCpu(task: Task<T>) {
        execute(
            getPoolByTypeAndPriority(TYPE_CPU.toInt()),
            task
        )
    }

    /**
     * Executes the given task in a cpu thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByCpu(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        execute(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt(),
                priority
            ), task
        )
    }

    /**
     * Executes the given task in a cpu thread pool after the given delay.
     *
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeByCpuWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt()
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByCpuWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeWithDelay(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt(),
                priority
            ), task, delay, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt()
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param <T>      The type of the task's result.
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt(),
                priority
            ), task, 0, period, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt()
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(
            getPoolByTypeAndPriority(
                TYPE_CPU.toInt(),
                priority
            ), task, initialDelay, period, unit
        )
    }

    /**
     * Executes the given task in a custom thread pool.
     *
     * @param pool The custom thread pool.
     * @param task The task to execute.
     * @param <T>  The type of the task's result.
    </T> */
    fun <T> executeByCustom(
        pool: ExecutorService?,
        task: Task<T>
    ) {
        execute(pool, task)
    }

    /**
     * Executes the given task in a custom thread pool after the given delay.
     *
     * @param pool  The custom thread pool.
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param <T>   The type of the task's result.
    </T> */
    fun <T> executeByCustomWithDelay(
        pool: ExecutorService?,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(pool, task, delay, unit)
    }

    /**
     * Executes the given task in a custom thread pool at fix rate.
     *
     * @param pool   The custom thread pool.
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param <T>    The type of the task's result.
    </T> */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService?,
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, 0, period, unit)
    }

    /**
     * Executes the given task in a custom thread pool at fix rate.
     *
     * @param pool         The custom thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param <T>          The type of the task's result.
    </T> */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService?,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, initialDelay, period, unit)
    }

    /**
     * Cancel the given task.
     *
     * @param task The task to cancel.
     */
    fun cancel(task: Task<*>?) {
        if (task == null) {
            return
        }
        task.cancel()
    }

    /**
     * Cancel the given tasks.
     *
     * @param tasks The tasks to cancel.
     */
    fun cancel(vararg tasks: Task<*>?) {
        if (tasks == null || tasks.size == 0) {
            return
        }
        for (task in tasks) {
            if (task == null) {
                continue
            }
            task.cancel()
        }
    }

    /**
     * Cancel the given tasks.
     *
     * @param tasks The tasks to cancel.
     */
    fun cancel(tasks: List<Task<*>?>?) {
        if (tasks == null || tasks.size == 0) {
            return
        }
        for (task in tasks) {
            if (task == null) {
                continue
            }
            task.cancel()
        }
    }

    /**
     * Cancel the tasks in pool.
     *
     * @param executorService The pool.
     */
    fun cancel(executorService: ExecutorService) {
        if (executorService is PriorityThreadPoolExecutor) {
            for ((key, value) in TASK_TASKINFO_MAP) {
                if (value.mService === executorService) {
                    cancel(key)
                }
            }
        } else {
            loggerI("The executorService is not ThreadUtil's pool.")
        }
    }

    /**
     * Set the deliver.
     *
     * @param deliver The deliver.
     */
    fun setDeliver(deliver: Executor?) {
        sDeliver = deliver
    }

    private fun <T> execute(
        pool: ExecutorService?,
        task: Task<T>
    ) {
        execute(pool, task, 0, 0, null)
    }

    private fun <T> executeWithDelay(
        pool: ExecutorService?,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        execute(pool, task, delay, 0, unit)
    }

    private fun <T> executeAtFixedRate(
        pool: ExecutorService?,
        task: Task<T>,
        delay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        execute(pool, task, delay, period, unit)
    }

    private fun <T> execute(
        pool: ExecutorService?, task: Task<T>,
        delay: Long, period: Long, unit: TimeUnit?
    ) {
        var taskInfo: TaskInfo
        synchronized(TASK_TASKINFO_MAP) {
            if (TASK_TASKINFO_MAP[task] != null) {
                loggerI("Task can only be executed once.")
                return
            }
            taskInfo = TaskInfo(pool)
            TASK_TASKINFO_MAP.put(task, taskInfo)
        }
        if (period == 0L) {
            if (delay == 0L) {
                pool!!.execute(task)
            } else {
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        pool!!.execute(task)
                    }
                }
                taskInfo.mTimerTask = timerTask
                TIMER.schedule(timerTask, unit!!.toMillis(delay))
            }
        } else {
            task.setSchedule(true)
            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    pool!!.execute(task)
                }
            }
            taskInfo.mTimerTask = timerTask
            TIMER.scheduleAtFixedRate(
                timerTask,
                unit!!.toMillis(delay),
                unit.toMillis(period)
            )
        }
    }

    private fun getPoolByTypeAndPriority(type: Int): ExecutorService? {
        return getPoolByTypeAndPriority(
            type,
            Thread.NORM_PRIORITY
        )
    }

    private fun getPoolByTypeAndPriority(
        type: Int,
        priority: Int
    ): ExecutorService? {
        synchronized(TYPE_PRIORITY_POOLS) {
            var pool: ExecutorService?
            var priorityPools = TYPE_PRIORITY_POOLS[type]
            if (priorityPools == null) {
                priorityPools = ConcurrentHashMap()
                pool = PriorityThreadPoolExecutor.createPool(type, priority)
                priorityPools[priority] = pool
                TYPE_PRIORITY_POOLS[type] = priorityPools
            } else {
                pool = priorityPools[priority]
                if (pool == null) {
                    pool = PriorityThreadPoolExecutor.createPool(type, priority)
                    priorityPools[priority] = pool
                }
            }
            return pool
        }
    }

    private val globalDeliver: Executor?
        private get() {
            if (sDeliver == null) {
                sDeliver = object : Executor {
                    private val mHandler =
                        Handler(Looper.getMainLooper())

                    override fun execute(command: Runnable) {
                        mHandler.post(command)
                    }
                }
            }
            return sDeliver
        }

    internal class PriorityThreadPoolExecutor(
        corePoolSize: Int, maximumPoolSize: Int,
        keepAliveTime: Long, unit: TimeUnit?,
        workQueue: RunnableLinkedBlockingQueue,
        threadFactory: ThreadFactory?
    ) : ThreadPoolExecutor(
        corePoolSize, maximumPoolSize,
        keepAliveTime, unit,
        workQueue,
        threadFactory
    ) {
        private val mSubmittedCount =
            AtomicInteger()
        private val mWorkQueue: RunnableLinkedBlockingQueue
        private val submittedCount: Int
            private get() = mSubmittedCount.get()

        override fun afterExecute(
            r: Runnable,
            t: Throwable
        ) {
            mSubmittedCount.decrementAndGet()
            super.afterExecute(r, t)
        }

        override fun execute(command: Runnable) {
            if (this.isShutdown) {
                return
            }
            mSubmittedCount.incrementAndGet()
            try {
                super.execute(command)
            } catch (ignore: RejectedExecutionException) {
                loggerI("This will not happen!")
                mWorkQueue.offer(command)
            } catch (t: Throwable) {
                mSubmittedCount.decrementAndGet()
            }
        }

        companion object {
            fun createPool(type: Int, priority: Int): ExecutorService {
                return when (type.toByte()) {
                    TYPE_SINGLE -> PriorityThreadPoolExecutor(
                        1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        RunnableLinkedBlockingQueue(),
                        ThreadFactoryImpl("single", priority)
                    )
                    TYPE_CACHED -> PriorityThreadPoolExecutor(
                        0, 128,
                        60L, TimeUnit.SECONDS,
                        RunnableLinkedBlockingQueue(true),
                        ThreadFactoryImpl("cached", priority)
                    )
                    TYPE_IO -> PriorityThreadPoolExecutor(
                        2 * CPU_COUNT + 1,
                        2 * CPU_COUNT + 1,
                        30,
                        TimeUnit.SECONDS,
                        RunnableLinkedBlockingQueue(),
                        ThreadFactoryImpl("io", priority)
                    )
                    TYPE_CPU -> PriorityThreadPoolExecutor(
                        CPU_COUNT + 1,
                        2 * CPU_COUNT + 1,
                        30,
                        TimeUnit.SECONDS,
                        RunnableLinkedBlockingQueue(true),
                        ThreadFactoryImpl("cpu", priority)
                    )
                    else -> PriorityThreadPoolExecutor(
                        type, type,
                        0L, TimeUnit.MILLISECONDS,
                        RunnableLinkedBlockingQueue(),
                        ThreadFactoryImpl("fixed($type)", priority)
                    )
                }
            }
        }

        init {
            workQueue.mPool = this
            mWorkQueue = workQueue
        }
    }

    class RunnableLinkedBlockingQueue : LinkedBlockingQueue<Runnable> {
        @Volatile
        internal var mPool: PriorityThreadPoolExecutor? = null
        private var mCapacity = Int.MAX_VALUE

        internal constructor() : super() {}
        internal constructor(isAddSubThreadFirstThenAddQueue: Boolean) : super() {
            if (isAddSubThreadFirstThenAddQueue) {
                mCapacity = 0
            }
        }

        internal constructor(capacity: Int) : super() {
            mCapacity = capacity
        }

        override fun offer(runnable: Runnable): Boolean {
            return when {
                mCapacity <= size && mPool != null && mPool!!.poolSize < mPool!!.maximumPoolSize -> {
                    // create a non-core thread
                    false
                }
                else -> super.offer(runnable)
            }
        }
    }

    private class ThreadFactoryImpl @JvmOverloads internal constructor(
        prefix: String,
        priority: Int,
        isDaemon: Boolean = false
    ) : AtomicLong(), ThreadFactory {
        private val namePrefix: String
        private val priority: Int
        private val isDaemon: Boolean
        override fun newThread(r: Runnable): Thread {
            val t: Thread = object : Thread(r, namePrefix + andIncrement) {
                override fun run() {
                    try {
                        super.run()
                    } catch (t: Throwable) {
                        loggerE("Request threw uncaught throwable:$t")
                    }
                }
            }
            t.isDaemon = isDaemon
            t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e -> println(e) }
            t.priority = priority
            return t
        }

        companion object {
            private val POOL_NUMBER =
                AtomicInteger(1)
            private const val serialVersionUID = -9209200509960368598L
        }

        init {
            namePrefix =
                prefix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-"
            this.priority = priority
            this.isDaemon = isDaemon
        }

        override fun toByte(): Byte {
            return get().toByte()
        }

        override fun toChar(): Char {
            return get().toChar()
        }

        override fun toShort(): Short {
            return get().toShort()
        }
    }

    abstract class SimpleTask<T> : Task<T>() {
        override fun onCancel() {
            loggerI("onCancel: " + Thread.currentThread())
        }

        override fun onFail(t: Throwable?) {
            loggerI("onFail: " + Thread.currentThread())
        }
    }

    abstract class Task<T> : Runnable {
        private val state =
            AtomicInteger(NEW)
        @Volatile
        private var isSchedule = false
        @Volatile
        private var runner: Thread? = null
        private var mTimer: Timer? = null
        private var deliver: Executor? = null
        @Throws(Throwable::class)
        abstract fun doInBackground(): T

        abstract fun onSuccess(result: T)
        abstract fun onCancel()
        abstract fun onFail(t: Throwable?)
        override fun run() {
            if (isSchedule) {
                if (runner == null) {
                    if (!state.compareAndSet(
                            NEW,
                            RUNNING
                        )
                    ) {
                        return
                    }
                    runner = Thread.currentThread()
                } else {
                    if (state.get() != RUNNING) {
                        return
                    }
                }
            } else {
                if (!state.compareAndSet(
                        NEW,
                        RUNNING
                    )
                ) {
                    return
                }
                runner = Thread.currentThread()
            }
            try {
                val result = doInBackground()
                if (isSchedule) {
                    if (state.get() != RUNNING) {
                        return
                    }
                    getDeliver()!!.execute { onSuccess(result) }
                } else {
                    if (!state.compareAndSet(
                            RUNNING,
                            COMPLETING
                        )
                    ) {
                        return
                    }
                    getDeliver()!!.execute {
                        onSuccess(result)
                        onDone()
                    }
                }
            } catch (ignore: InterruptedException) {
                state.compareAndSet(
                    CANCELLED,
                    INTERRUPTED
                )
            } catch (throwable: Throwable) {
                if (!state.compareAndSet(
                        RUNNING,
                        EXCEPTIONAL
                    )
                ) {
                    return
                }
                getDeliver()!!.execute {
                    onFail(throwable)
                    onDone()
                }
            }
        }

        @JvmOverloads
        fun cancel(mayInterruptIfRunning: Boolean = true) {
            synchronized(state) {
                if (state.get() > RUNNING) {
                    return
                }
                state.set(CANCELLED)
            }
            if (mayInterruptIfRunning) {
                if (runner != null) {
                    runner!!.interrupt()
                }
            }
            getDeliver()!!.execute {
                onCancel()
                onDone()
            }
        }

        private fun timeout() {
            synchronized(state) {
                if (state.get() > RUNNING) {
                    return
                }
                state.set(TIMEOUT)
            }
            if (runner != null) {
                runner!!.interrupt()
            }
            onDone()
        }

        val isCanceled: Boolean
            get() = state.get() >= CANCELLED

        val isDone: Boolean
            get() = state.get() > RUNNING

        fun setDeliver(deliver: Executor?): Task<T> {
            this.deliver = deliver
            return this
        }

        fun setTimeout(timeoutMillis: Long, listener: OnTimeoutListener?) {
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    if (!isDone && listener != null) {
                        timeout()
                        listener.onTimeout()
                    }
                }
            }, timeoutMillis)
        }

        fun setSchedule(isSchedule: Boolean) {
            this.isSchedule = isSchedule
        }

        private fun getDeliver(): Executor? {
            return if (deliver == null) {
                globalDeliver
            } else deliver
        }

        @CallSuper
        protected fun onDone() {
            TASK_TASKINFO_MAP.remove(this)
            if (mTimer != null) {
                mTimer!!.cancel()
                mTimer = null
            }
        }

        interface OnTimeoutListener {
            fun onTimeout()
        }

        companion object {
            private const val NEW = 0
            private const val RUNNING = 1
            private const val EXCEPTIONAL = 2
            private const val COMPLETING = 3
            private const val CANCELLED = 4
            private const val INTERRUPTED = 5
            private const val TIMEOUT = 6
        }
    }

    private class TaskInfo(val mService: ExecutorService?) {
        var mTimerTask: TimerTask? = null

    }
}