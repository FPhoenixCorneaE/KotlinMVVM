package com.fphoenixcorneae.ext.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson解析默认Integer类型适配器
 */
class DefaultIntegerAdapter : JsonSerializer<Int>,
    JsonDeserializer<Int> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Int {
        try {
            when (json.asString) {
                "", "null" -> {
                    // 定义为Integer类型,如果后台返回""或者null,则返回0
                    return 0
                }
            }
        } catch (ignore: Exception) {
        }
        return try {
            json.asInt
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(
        src: Int,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}