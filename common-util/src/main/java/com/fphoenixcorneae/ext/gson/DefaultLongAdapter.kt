package com.fphoenixcorneae.ext.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson解析默认Long类型适配器
 */
class DefaultLongAdapter : JsonSerializer<Long>,
    JsonDeserializer<Long> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Long {
        try {
            when (json.asString) {
                "", "null" -> {
                    // 定义为Long类型,如果后台返回""或者null,则返回0L
                    return 0.toLong()
                }
            }
        } catch (ignore: Exception) {
        }
        return try {
            json.asLong
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(
        src: Long,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}