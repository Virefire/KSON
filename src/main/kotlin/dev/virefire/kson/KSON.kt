package dev.virefire.kson

import com.google.gson.*
import java.io.Reader

object KSON {
    fun stringify(value: Any?): String {
        return toJsonElement(value).toString()
    }

    fun parse(json: String): ParsedElement {
        return ParsedElement(JsonParser.parseString(json))
    }

    fun parse(reader: Reader): ParsedElement {
        return ParsedElement(JsonParser.parseReader(reader))
    }
}
