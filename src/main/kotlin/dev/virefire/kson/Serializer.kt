package dev.virefire.kson

import com.google.gson.*
import dev.virefire.kson.annotations.Hidden
import dev.virefire.kson.annotations.HideIfNull
import dev.virefire.kson.annotations.JsonName

fun toJsonElement(obj: Any?): JsonElement {
    if (obj == null)
        return JsonNull.INSTANCE
    return when (obj) {
        is Boolean -> JsonPrimitive(obj)
        is Number -> JsonPrimitive(obj)
        is String -> JsonPrimitive(obj)
        is List<*> -> {
            val array = JsonArray()
            obj.forEach {
                array.add(toJsonElement(it))
            }
            return array
        }
        is Map<*, *> -> {
            val jsonObject = JsonObject()
            obj.forEach {
                jsonObject.add(it.key.toString(), toJsonElement(it.value))
            }
            return jsonObject
        }
        else -> {
            val jsonObject = JsonObject()
            obj::class.java.declaredFields.forEach {
                it.isAccessible = true

                if (it.getAnnotation(Hidden::class.java) != null)
                    return@forEach

                if (it.getAnnotation(HideIfNull::class.java) != null && it.get(obj) == null)
                    return@forEach
                var name = it.name

                val annotation = it.getAnnotation(JsonName::class.java)
                if (annotation != null)
                    name = annotation.value

                jsonObject.add(name, toJsonElement(it.get(obj)))
            }
            return jsonObject
        }
    }
}