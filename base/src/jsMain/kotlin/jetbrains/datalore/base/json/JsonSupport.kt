package jetbrains.datalore.base.json

import kotlin.js.JSON.stringify

actual object JsonSupport {
    actual fun parseJson(jsonString: String): MutableMap<String, Any?> {
        val parser = object {
            fun handleObject(v: dynamic): MutableMap<String, Any?> {
                return js("Object").entries(v)
                    .unsafeCast<Array<Array<*>>>()
                    .map { (k, v) -> k as String to handleValue(v) }
                    .toMap(HashMap())
            }

            fun handleArray(v: Array<*>) = v.map { handleValue(it) }

            fun handleValue(v: Any?): Any? {
                return when (v) {
                    is String, Boolean, null -> v
                    is Number -> v.toDouble()
                    is Array<*> -> handleArray(v)
                    else -> handleObject(v)
                }
            }
        }

        return parser.handleObject(JSON.parse(jsonString))
    }

    actual fun formatJson(o: Any): String {
        return stringify(o)
    }
}
