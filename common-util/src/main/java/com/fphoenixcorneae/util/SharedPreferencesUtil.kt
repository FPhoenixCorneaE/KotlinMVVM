package com.fphoenixcorneae.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.fphoenixcorneae.ext.loggerE
import java.io.*
import kotlin.reflect.KProperty

/**
 * SharedPreferences 偏好设定工具类,轻量级的存储
 * 1、每次添加键值对时,会重新写入整个文件数据,所以不适合大量数据存储;
 * 2、commit()提交到硬盘是同步过程;apply()先提交到内存后异步提交到硬盘,无返回值;主线程使用commit()需考虑ANR问题;
 *    不关心提交结果是否成功的情况下,优先考虑apply()方法;
 * 3、多线程场景下效率较低,get()操作会锁定SharedPreferencesImpl里的对象,互斥其他操作,而当put()、commit()和
 *    apply()操作时会锁住Editor对象;
 * 4、由于每次都会把整个文件加载到内存中,因此,如果SharedPreferences文件过大,或者其中的键值对是大对象的JSON
 *    数据则会占用大量内存,读取较慢是一方面,同时也会引发程序频繁GC,导致界面卡顿。
 *
 * 建议:
 * 1、频繁修改的数据修改后统一提交,而不是修改过后马上提交;
 * 2、在跨进程通讯中不去使用SharedPreferences;
 * 3、获取SharedPreferences对象时会读取SharedPreferences文件,如果文件没有读取完,就执行get()和put()操作,可能
 *    会出现需要等待的情况,因此最好提交获取SharedPreferences对象;
 * 4、每次调用edit()方法都会创建一个新的EditorImpl对象,不要频繁调用edit()方法。
 *
 * @date 2019-12-05 10:14
 */
class SharedPreferencesUtil<T>(private val keyName: String, private val default: T) {

    companion object {

        /**
         * Get SharedPreferences
         */
        private val sharedPreferences: SharedPreferences by lazy {
            ContextUtil.context.getSharedPreferences(AppUtil.appName, Context.MODE_PRIVATE)
        }

        /**
         * Get SharedPreferences by name
         */
        private fun getSharedPreferences(name: String?): SharedPreferences {
            var sharedPreferences = sharedPreferences
            if (!TextUtils.isEmpty(name)) {
                sharedPreferences =
                    ContextUtil.context.getSharedPreferences(name, Context.MODE_PRIVATE)
            }
            return sharedPreferences
        }

        /**
         * Put SharedPreferences, the method may set a string/boolean/int/float/long value in the preferences editor
         */
        @JvmStatic
        @JvmOverloads
        fun put(key: String, value: Any?, name: String? = null): Boolean {
            if (TextUtils.isEmpty(key) || null == value) {
                throw RuntimeException("key or value cannot be null.")
            }
            val editor = getSharedPreferences(name).edit()
            when (value) {
                is String -> {
                    editor.putString(key, value.toString())
                }
                is Boolean -> {
                    editor.putBoolean(key, java.lang.Boolean.parseBoolean(value.toString()))
                }
                is Float -> {
                    editor.putFloat(key, (value as Float?)!!)
                }
                is Int -> {
                    editor.putInt(key, (value as Int?)!!)
                }
                is Long -> {
                    editor.putLong(key, (value as Long?)!!)
                }
            }
            return editor.commit()
        }

        /**
         * Put all SharedPreferences, all the data will be maked a list.
         */
        @JvmStatic
        @JvmOverloads
        fun putAll(key: String, list: List<*>, name: String? = null): Boolean {
            if (TextUtils.isEmpty(key) || list.isEmpty()) {
                throw RuntimeException("key or list cannot be null.")
            }
            val editor = getSharedPreferences(name).edit()
            val size = list.size
            when {
                list[0] is String -> {
                    for (i in 0 until size) {
                        editor.putString(key + i, list[i] as String)
                    }
                }
                list[0] is Long -> {
                    for (i in 0 until size) {
                        editor.putLong(key + i, list[i] as Long)
                    }
                }
                list[0] is Float -> {
                    for (i in 0 until size) {
                        editor.putFloat(key + i, list[i] as Float)
                    }
                }
                list[0] is Int -> {
                    for (i in 0 until size) {
                        editor.putLong(key + i, (list[i] as Int).toLong())
                    }
                }
                list[0] is Boolean -> {
                    for (i in 0 until size) {
                        editor.putBoolean(key + i, list[i] as Boolean)
                    }
                }
            }
            return editor.commit()
        }

        /**
         * Retrieve all values from the preferences.
         *
         * @return Returns a map containing a list of pairs key/value representing the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun getAll(name: String? = null): Map<String, *> {
            return getSharedPreferences(name).all
        }

        /**
         * Retrieve a boolean value from the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun getBoolean(key: String, defValue: Boolean = false, name: String? = null): Boolean {
            return getSharedPreferences(name).getBoolean(key, defValue)
        }

        /**
         * Retrieve a long value from the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun getLong(key: String, defValue: Long = 0L, name: String? = null): Long {
            return getSharedPreferences(name).getLong(key, defValue)
        }

        /**
         * Retrieve a float value from the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun getFloat(key: String, defValue: Float = 0F, name: String? = null): Float {
            return getSharedPreferences(name).getFloat(key, defValue)
        }

        /**
         * Retrieve a int value from the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun getInt(key: String, defValue: Int = 0, name: String? = null): Int {
            return getSharedPreferences(name).getInt(key, defValue)
        }

        /**
         * Retrieve a String value from the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun getString(key: String, defValue: String = "", name: String? = null): String {
            return getSharedPreferences(name).getString(key, defValue).toString()
        }

        /**
         * Mark in the editor that a preference value should be removed.
         */
        @JvmStatic
        @JvmOverloads
        fun remove(key: String, name: String? = null): Boolean {
            val editor = getSharedPreferences(name).edit()
            editor.remove(key)
            return editor.commit()
        }

        /**
         * 查询某个key是否已经存在
         *
         * @param key
         * @return
         */
        @JvmStatic
        fun contains(key: String): Boolean {
            return sharedPreferences.contains(key)
        }

        /**
         * Mark in the editor to remove all values from the preferences.
         */
        @JvmStatic
        @JvmOverloads
        fun clear(name: String? = null): Boolean {
            val editor = getSharedPreferences(name).edit()
            editor.clear()
            return editor.commit()
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSharedPreferences(keyName, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharedPreferences(keyName, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putSharedPreferences(name: String, value: T) = with(sharedPreferences.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSharedPreferences(name: String, default: T): T = with(sharedPreferences) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default)).toString())
        }.toString()
        return res as T
    }


    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        var serStr = ""
        try {
            objectOutputStream.writeObject(obj)
            serStr = byteArrayOutputStream.toString("ISO-8859-1")
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        } catch (e: Exception) {
            loggerE(e.toString())
        } finally {
            CloseUtil.closeIOQuietly(objectOutputStream, byteArrayOutputStream)
        }
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as A
        CloseUtil.closeIOQuietly(objectInputStream, byteArrayInputStream)
        return obj
    }
}
