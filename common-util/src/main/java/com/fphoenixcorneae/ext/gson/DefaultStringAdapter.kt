package com.fphoenixcorneae.ext.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson解析默认String类型适配器
 */
class DefaultStringAdapter : JsonSerializer<String>,
    JsonDeserializer<String> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String {
        try {
            when (json.asString) {
                "null" -> {
                    // 定义为String类型,如果后台返回null,则返回""
                    return ""
                }
            }
        } catch (ignore: Exception) {
        }
        return try {
            json.asString
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(
        src: String,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src)
    }
}