package com.fphoenixcorneae.ext.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson解析默认Float类型适配器
 */
class DefaultFloatAdapter : JsonSerializer<Float>,
    JsonDeserializer<Float> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Float {
        try {
            when (json.asString) {
                "", "null" -> {
                    // 定义为Float类型,如果后台返回""或者null,则返回0.0F
                    return 0.toFloat()
                }
            }
        } catch (ignore: Exception) {
        }
        return try {
            json.asFloat
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(
        src: Float,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}