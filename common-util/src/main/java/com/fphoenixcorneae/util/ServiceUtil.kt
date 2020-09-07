package com.fphoenixcorneae.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.fphoenixcorneae.util.ContextUtil.Companion.context
import java.util.*

/**
 * 服务工具类
 */
class ServiceUtil private constructor() {

    companion object {
        /**
         * Return all of the services are running.
         *
         * @return all of the services are running
         */
        val allRunningServices: Set<*>?
            get() {
                val am =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info =
                    am.getRunningServices(0x7FFFFFFF)
                val names: MutableSet<String> =
                    HashSet()
                if (info == null || info.size == 0) {
                    return null
                }
                for (aInfo in info) {
                    names.add(aInfo.service.className)
                }
                return names
            }

        /**
         * Start the service.
         *
         * @param className The name of class.
         */
        fun startService(className: String) {
            try {
                startService(Class.forName(className))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Start the service.
         *
         * @param cls The service class.
         */
        fun startService(cls: Class<*>?) {
            val intent = Intent(context, cls)
            context.startService(intent)
        }

        /**
         * Stop the service.
         *
         * @param className The name of class.
         * @return `true`: success<br></br>`false`: fail
         */
        fun stopService(className: String): Boolean {
            return try {
                stopService(Class.forName(className))
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        /**
         * Stop the service.
         *
         * @param cls The name of class.
         * @return `true`: success<br></br>`false`: fail
         */
        fun stopService(cls: Class<*>?): Boolean {
            val intent = Intent(context, cls)
            return context.stopService(intent)
        }

        /**
         * Bind the service.
         *
         * @param className The name of class.
         * @param conn      The ServiceConnection object.
         * @param flags     Operation options for the binding.
         *
         *  * 0
         *  * [Context.BIND_AUTO_CREATE]
         *  * [Context.BIND_DEBUG_UNBIND]
         *  * [Context.BIND_NOT_FOREGROUND]
         *  * [Context.BIND_ABOVE_CLIENT]
         *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
         *  * [Context.BIND_WAIVE_PRIORITY]
         *
         */
        fun bindService(
            className: String,
            conn: ServiceConnection,
            flags: Int
        ) {
            try {
                bindService(Class.forName(className), conn, flags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Bind the service.
         *
         * @param cls   The service class.
         * @param conn  The ServiceConnection object.
         * @param flags Operation options for the binding.
         *
         *  * 0
         *  * [Context.BIND_AUTO_CREATE]
         *  * [Context.BIND_DEBUG_UNBIND]
         *  * [Context.BIND_NOT_FOREGROUND]
         *  * [Context.BIND_ABOVE_CLIENT]
         *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
         *  * [Context.BIND_WAIVE_PRIORITY]
         *
         */
        fun bindService(
            cls: Class<*>,
            conn: ServiceConnection,
            flags: Int
        ) {
            val intent = Intent(context, cls)
            context.bindService(intent, conn, flags)
        }

        /**
         * Unbind the service.
         *
         * @param conn The ServiceConnection object.
         */
        fun unbindService(conn: ServiceConnection) {
            context.unbindService(conn)
        }

        /**
         * Return whether service is running.
         *
         * @param cls The service class.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isServiceRunning(cls: Class<*>): Boolean {
            return isServiceRunning(cls.name)
        }

        /**
         * Return whether service is running.
         *
         * @param className The name of class.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isServiceRunning(className: String): Boolean {
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info =
                am.getRunningServices(0x7FFFFFFF)
            if (info == null || info.size == 0) {
                return false
            }
            for (aInfo in info) {
                if (className == aInfo.service.className) {
                    return true
                }
            }
            return false
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}