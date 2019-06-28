package jetbrains.datalore.base.json

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parseMap

@ImplicitReflectionSerializer
actual object JsonSupport {
    actual fun parseJson(json: String): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        val json = json.replace("'", "\"")

        println("Parsing JSON:")
        println(json)

        val map = Json.parseMap<String, Any>(json)
        return HashMap(map)  // need all values mutable
    }

    actual fun toJson(o: Any): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
