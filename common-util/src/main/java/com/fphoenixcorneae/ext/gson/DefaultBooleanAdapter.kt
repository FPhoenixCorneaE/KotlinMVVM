package com.fphoenixcorneae.ext.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson解析默认Boolean类型适配器
 */
class DefaultBooleanAdapter : JsonSerializer<Boolean>,
    JsonDeserializer<Boolean> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Boolean {
        try {
            when (json.asString) {
                "", "null", "0" -> {
                    // 定义为Boolean类型,如果后台返回""或者null或者0,则返回false
                    return false
                }
                "1" -> {
                    // 定义为Boolean类型,如果后台返回1,则返回true
                    return true
                }
            }
        } catch (ignore: Exception) {
        }
        return try {
            json.asBoolean
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(
        src: Boolean,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}