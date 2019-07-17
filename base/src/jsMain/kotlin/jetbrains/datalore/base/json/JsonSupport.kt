package jetbrains.datalore.base.json

import jetbrains.datalore.base.jsObject.JsonToMap
import kotlin.js.JSON.stringify

actual object JsonSupport {
    actual fun parseJson(json: String): MutableMap<String, Any?> {
        val j = JSON.parse<Any>(json)
        return JsonToMap().handleObject(j)
    }

    actual fun toJson(o: Any): String {
        return stringify(o)
    }
}
