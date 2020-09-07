package com.fphoenixcorneae.util

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * 克隆工具类
 */
class CloneUtil private constructor() {
    companion object {
        /**
         * Deep clone.
         *
         * @param data The data.
         * @param type The type.
         * @param <T>  The value type.
         * @return The object of cloned.
        </T> */
        fun <T> deepClone(data: T, type: Type?): T? {
            return try {
                val gson = Gson()
                gson.fromJson(gson.toJson(data), type)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}