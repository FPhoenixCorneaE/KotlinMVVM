package com.fphoenixcorneae.ext.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson解析默认Double类型适配器
 */
class DefaultDoubleAdapter : JsonSerializer<Double>,
    JsonDeserializer<Double> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Double {
        try {
            when (json.asString) {
                "", "null" -> {
                    // 定义为Double类型,如果后台返回""或者null,则返回0.00
                    return 0.toDouble()
                }
            }
        } catch (ignore: Exception) {
        }
        return try {
            json.asDouble
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(
        src: Double,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}