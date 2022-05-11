package dev.virefire.kson

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import dev.virefire.kson.exceptions.JsonElementNotFoundException
import dev.virefire.kson.exceptions.JsonTypeMismatchException

private fun getTypeName(element: JsonElement): String {
    return when (element) {
        is JsonArray -> "list"
        is JsonObject -> "map"
        is JsonNull -> "null"
        is JsonPrimitive -> {
            if (element.isBoolean) "boolean"
            else if (element.isNumber) "number"
            else if (element.isString) "string"
            else "?"
        }
        else -> "?"
    }
}

class ParsedElement(private var json: JsonElement?, private val isSilent: Boolean = false) {
    init {
        if (json == null) {
            json = JsonNull.INSTANCE
        }
    }

    private fun throwOrNull(e: Throwable): ParsedElement {
        if (isSilent) return ParsedElement(JsonNull.INSTANCE, true)
        throw e
    }

    val silent: ParsedElement
        get() = ParsedElement(json, true)

    operator fun get(key: String): ParsedElement {
        if (json !is JsonObject)
            return throwOrNull(JsonTypeMismatchException("Trying to get \"$key\" on ${getTypeName(json!!)} (${json!!}), only supported on map"))
        if (!json!!.asJsonObject.has(key))
            return throwOrNull(JsonElementNotFoundException("Element \"$key\" doesn't exist in map ${json!!}"))
        return ParsedElement(json!!.asJsonObject.get(key), isSilent)
    }

    operator fun get(key: Int): ParsedElement {
        if (json !is JsonArray)
            return throwOrNull(JsonTypeMismatchException("Trying to get $key on ${getTypeName(json!!)} (${json!!}), only supported on list"))
        if (json!!.asJsonArray.size() <= key)
            return throwOrNull(JsonElementNotFoundException("Element $key doesn't exist in list ${json!!}"))
        return ParsedElement(json!!.asJsonArray.get(key), isSilent)
    }

    fun has(key: String): Boolean {
        if (json !is JsonObject) return false
        return json!!.asJsonObject.has(key)
    }

    val isNull: Boolean
        get () = json!! is JsonNull

    val isString: Boolean
        get () = json!! is JsonNull || (json!!.isJsonPrimitive && json!!.asJsonPrimitive.isString)

    val isNumber: Boolean
        get () = json!! is JsonNull || (json!!.isJsonPrimitive && json!!.asJsonPrimitive.isNumber)

    val isBoolean: Boolean
        get () = json!! is JsonNull || (json!!.isJsonPrimitive && json!!.asJsonPrimitive.isBoolean)

    val isList: Boolean
        get () = json!! is JsonNull || json!!.isJsonArray

    val isMap: Boolean
        get () = json!! is JsonNull || json!!.isJsonObject

    val string: String?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isString))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as String")
            return if (json!! is JsonNull) null else json!!.asString
        }

    val number: Number?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isNumber))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Number")
            return if (json!! is JsonNull) null else json!!.asNumber
        }

    val int: Int?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isNumber))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Int")
            return if (json!! is JsonNull) null else json!!.asInt
        }

    val long: Long?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isNumber))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Long")
            return if (json!! is JsonNull) null else json!!.asLong
        }

    val float: Float?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isNumber))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Float")
            return if (json!! is JsonNull) null else json!!.asFloat
        }

    val double: Double?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isNumber))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Double")
            return if (json!! is JsonNull) null else json!!.asDouble
        }

    val boolean: Boolean?
        get () {
            if (json !is JsonNull && (json !is JsonPrimitive || !json!!.asJsonPrimitive.isBoolean))
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Boolean")
            return if (json!! is JsonNull) null else json!!.asBoolean
        }

    val list: List<ParsedElement>?
        get () {
            if (json!! is JsonNull)
                return null
            if (json!! !is JsonArray)
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as List")
            return json!!.asJsonArray.map { ParsedElement(it) }
        }

    val map: Map<String, ParsedElement>?
        get () {
            if (json!! is JsonNull)
                return null
            if (json!! !is JsonObject)
                throw JsonTypeMismatchException("Trying to get ${getTypeName(json!!)} (${json!!}) as Map")
            val map = mutableMapOf<String, ParsedElement>()
            json!!.asJsonObject.entrySet().forEach {
                map[it.key] = ParsedElement(it.value)
            }
            return map
        }

    override fun toString(): String {
        return json.toString()
    }
}