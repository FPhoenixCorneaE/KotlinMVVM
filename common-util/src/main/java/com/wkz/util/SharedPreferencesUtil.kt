package com.wkz.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import java.io.*
import kotlin.reflect.KProperty

/**
 * SharedPreferences操作
 * @author wkz
 */
class SharedPreferencesUtil<T>(private val keyName: String, private val default: T) {

    companion object {
        private const val file_name = "kotlin_mvvm_file"

        /**
         * Get SharedPreferences
         */
        private val sharedPreferences: SharedPreferences by lazy {
            ContextUtil.context.getSharedPreferences(file_name, Context.MODE_PRIVATE)
        }


        /**
         * Get SharedPreferences by name
         */
        private fun getSharedPreferences(name: String?): SharedPreferences {
            var sharedPreferences = sharedPreferences
            if (!TextUtils.isEmpty(name)) {
                sharedPreferences = ContextUtil.context.getSharedPreferences(name, Context.MODE_PRIVATE)
            }
            return sharedPreferences
        }

        /**
         * Put SharedPreferences, the method may set a string/boolean/int/float/long value in the preferences editor
         */
        fun put(key: String, value: Any): Boolean {
            return put(null, key, value)
        }

        /**
         * Put SharedPreferences, the method may set a string/boolean/int/float/long value in the preferences editor
         */
        fun put(name: String?, key: String, value: Any?): Boolean {
            if (TextUtils.isEmpty(key) || null == value) {
                throw RuntimeException("key or value cannot be null.")
            }
            val editor = getSharedPreferences(name).edit()
            if (value is String) {
                editor.putString(key, value.toString())
            } else if (value is Boolean) {
                editor.putBoolean(key, java.lang.Boolean.parseBoolean(value.toString()))
            } else if (value is Float) {
                editor.putFloat(key, (value as Float?)!!)
            } else if (value is Int) {
                editor.putInt(key, (value as Int?)!!)
            } else if (value is Long) {
                editor.putLong(key, (value as Long?)!!)
            }
            return editor.commit()
        }

        /**
         * Put all SharedPreferences, all the data will be maked a list.
         */
        fun putAll(key: String, list: List<*>): Boolean {
            return putAll(null, key, list)
        }

        /**
         * Put all SharedPreferences, all the data will be maked a list.
         */
        fun putAll(name: String?, key: String, list: List<*>): Boolean {
            if (TextUtils.isEmpty(key) || list.isEmpty()) {
                throw RuntimeException("key or list cannot be null.")
            }
            val editor = getSharedPreferences(name).edit()
            val size = list.size
            if (list[0] is String) {
                for (i in 0 until size) {
                    editor.putString(key + i, list[i] as String)
                }
            } else if (list[0] is Long) {
                for (i in 0 until size) {
                    editor.putLong(key + i, list[i] as Long)
                }
            } else if (list[0] is Float) {
                for (i in 0 until size) {
                    editor.putFloat(key + i, list[i] as Float)
                }
            } else if (list[0] is Int) {
                for (i in 0 until size) {
                    editor.putLong(key + i, (list[i] as Int).toLong())
                }
            } else if (list[0] is Boolean) {
                for (i in 0 until size) {
                    editor.putBoolean(key + i, list[i] as Boolean)
                }
            }
            return editor.commit()
        }

        /**
         * Retrieve all values from the preferences.
         *
         * @return Returns a map containing a list of pairs key/value representing the preferences.
         */
        val all: Map<String, *>
            get() = getAll(null)

        /**
         * Retrieve all values from the preferences.
         *
         * @return Returns a map containing a list of pairs key/value representing the preferences.
         */
        fun getAll(name: String?): Map<String, *> {
            return getSharedPreferences(name).all
        }

        /**
         * Retrieve a boolean value from the preferences.
         */
        fun getBoolean(key: String): Boolean {
            return getBoolean(null, key)
        }

        /**
         * Retrieve a boolean value from the preferences.
         */
        fun getBoolean(name: String?, key: String): Boolean {
            return getSharedPreferences(name).getBoolean(key, false)
        }

        /**
         * Retrieve a long value from the preferences.
         */
        fun getLong(key: String): Long {
            return getLong(null, key)
        }

        /**
         * Retrieve a long value from the preferences.
         */
        fun getLong(name: String?, key: String): Long {
            return getSharedPreferences(name).getLong(key, 0L)
        }

        /**
         * Retrieve a float value from the preferences.
         */
        fun getFloat(key: String): Float {
            return getFloat(null, key)
        }

        /**
         * Retrieve a float value from the preferences.
         */
        fun getFloat(name: String?, key: String): Float {
            return getSharedPreferences(name).getFloat(key, 0f)
        }

        /**
         * Retrieve a int value from the preferences.
         */
        fun getInt(key: String): Int {
            return getInt(null, key)
        }

        /**
         * Retrieve a int value from the preferences.
         */
        fun getInt(name: String?, key: String): Int {
            return getSharedPreferences(name).getInt(key, 0)
        }

        /**
         * Retrieve a String value from the preferences.
         */
        fun getString(key: String): String {
            return getString(null, key)
        }

        /**
         * Retrieve a String value from the preferences.
         */
        fun getString(name: String?, key: String): String {
            return getSharedPreferences(name).getString(key, null).toString()
        }

        /**
         * Mark in the editor that a preference value should be removed.
         */
        fun remove(key: String): Boolean {
            return remove(null, key)
        }

        /**
         * Mark in the editor that a preference value should be removed.
         */
        fun remove(name: String?, key: String): Boolean {
            val editor = getSharedPreferences(name).edit()
            editor.remove(key)
            return editor.commit()
        }

        /**
         * Mark in the editor to remove all values from the preferences.
         */
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
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
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
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}
