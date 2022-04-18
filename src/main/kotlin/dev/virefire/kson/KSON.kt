package dev.virefire.kson

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import java.io.Reader
import java.io.StringWriter

object KSON {
    fun stringify(value: Any?): String {
        return stringify(toJsonElement(value)) {
            it.isLenient = true
        }
    }

    fun stringify(value: Any?, indent: String): String {
        return stringify(toJsonElement(value)) {
            it.isLenient = true
            it.setIndent(indent)
        }
    }

    fun parse(json: String): ParsedElement {
        return ParsedElement(JsonParser.parseString(json))
    }

    fun parse(reader: Reader): ParsedElement {
        return ParsedElement(JsonParser.parseReader(reader))
    }

    private fun stringify(el: JsonElement, setup: (JsonWriter) -> Unit): String {
        val stringWriter = StringWriter()
        val jsonWriter = JsonWriter(stringWriter)
        setup(jsonWriter)
        Streams.write(el, jsonWriter)
        return stringWriter.toString()
    }
}
