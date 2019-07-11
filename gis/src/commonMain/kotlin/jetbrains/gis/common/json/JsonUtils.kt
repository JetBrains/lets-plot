package jetbrains.gis.common.json

typealias JsonObject = HashMap<String, Any?>
typealias JsonArray = ArrayList<*>

object JsonUtils {


//    val NULL = object : Any() {
//        override fun toString(): String {
//            return "null"
//        }
//    }
//
//    fun toAny(v: Any): Any {
//        return (v as? Map<*, *>)?.let { toJsonObject(it) } ?: if (v is List<*>) {
//            toJsonArray(v)
//        } else if (v is String) {
//            JsonString(v)
//        } else (v as? Double)?.let { JsonNumber(it) } ?: if (v is Boolean) {
//            JsonBoolean(v)
//        } else {
//            JsonString(v.toString())
//        }
//    }
//
//    private fun toObject(v: Any): Optional<Any> {
//        if (v is JsonNull) {
//            return Optional.ofNullable(null)
//        } else if (isString(v)) {
//            return Optional.of(getAsString(v)!!)
//        } else if (isNumber(v)) {
//            return Optional.of(getAsDouble(v))
//        } else if (isBoolean(v)) {
//            return Optional.of(getAsBoolean(v))
//        } else if (v is JsonObject) {
//            return Optional.of(toMap(v))
//        } else if (v is JsonArray) {
//            return Optional.of(toArray(v as JsonArray))
//        }
//
//        return Optional.empty()
//    }
//
//    fun toJsonObject(map: Map<*, *>): JsonObject {
//        val obj = JsonObject()
//        for (key in map.keys) {
//            val s = key as String
//            obj.put(s, toAny(map[s]))
//        }
//
//        return obj
//    }
//
//    private fun toJsonArray(v: List<*>): JsonArray {
//        val arr = JsonArray()
//        for (o in v) {
//            arr.add(toAny(o))
//        }
//
//        return arr
//    }
//
fun streamOf(arr: JsonArray): List<*> {
        return arr
    }

    fun objectsStreamOf(arr: JsonArray): List<JsonObject> {
        return streamOf(arr).map { v -> v as JsonObject }
    }

    fun stringStreamOf(arr: JsonArray): Sequence<String?> {
        return arr.asSequence().map { v -> v as String? }
    }

//    fun toMap(obj: JsonObject): Map<String, Any> {
//        val res = HashMap<String, Any>()
//
//        for (key in obj.getKeys()) {
//            toObject(obj[key]).ifPresent({ q -> res[key] = q })
//        }
//
//        return res
//    }
//
//    private fun toArray(arr: JsonArray): List<Any> {
//        val res = ArrayList<Any>()
//
//        var i = 0
//        val n = arr.size()
//        while (i < n) {
//            toObject(arr.get(i)).ifPresent(Consumer<Any> { res.add(it) })
//            i++
//        }
//
//        return res
//    }
//
//    fun isBoolean(e: Any): Boolean {
//        return e is JsonBoolean
//    }
//
//    fun isNumber(e: Any): Boolean {
//        return e is JsonNumber
//    }
//
//    fun isString(e: Any): Boolean {
//        return e is JsonString
//    }
//
//    fun isObject(v: Any): Boolean {
//        return v is JsonObject
//    }
//
//    fun isArray(v: Any): Boolean {
//        return v is JsonArray
//    }
//
//    fun containsString(obj: JsonObject, key: String): Boolean {
//        val v = obj[key]
//        return isString(v) || v is JsonNull
//    }
//
fun getAsString(e: Any?): String? {
    return if (e == null) {
        null
    } else (e as String)
}
//
//    fun getAsDouble(v: Any): Double {
//        return (v as JsonNumber).getDoubleValue()
//    }
//
//    fun getAsBoolean(v: Any): Boolean {
//        return (v as JsonBoolean).getBooleanValue()
//    }
//
//    fun getAsInt(v: Any): Int {
//        return (v as JsonNumber).getIntValue()
//    }
//
//    fun readString(obj: JsonObject, key: String): String {
//        if (!containsString(obj, key)) {
//            throw IllegalStateException("JsonObject does not contain string: $key")
//        }
//
//        return obj.getString(key)
//    }
//
//    fun containsBoolean(obj: JsonObject, key: String): Boolean {
//        val v = obj[key]
//        return isBoolean(v)
//    }
//
//    fun readBoolean(obj: JsonObject, key: String): Boolean {
//        return obj.getBoolean(key)
//    }
//
//    // JsonNull counts as empty array
//    fun containsArray(obj: JsonObject, key: String): Boolean {
//        val arr = obj[key]
//        return arr is JsonNull || arr is JsonArray
//    }
//
//    // JsonNull -> empty array
//    fun getArray(obj: JsonObject, key: String): JsonArray {
//        val arr = obj[key]
//        if (arr is JsonNull) {
//            return JsonArray()
//        } else if (arr is JsonArray) {
//            return arr as JsonArray
//        }
//
//        throw IllegalStateException("JsonObject does not contain array: $key")
//    }
//
//    fun readDouble(array: JsonArray, index: Int): Double {
//        return array.getDouble(index)
//    }
//
//    fun getOptional(obj: JsonObject, key: String): Optional<Any> {
//        return if (!obj.getKeys().contains(key)) {
//            Optional.empty()
//        } else Optional.ofNullable(obj[key])
//    }
//
//    fun getOptionalInt(v: Any): Optional<Int> {
//        if (v is JsonNull) {
//            return Optional.empty()
//        } else if (isNumber(v)) {
//            return Optional.of((v as JsonNumber).getIntValue())
//        }
//
//        throw IllegalStateException("Object is not JsonNumber: " + v.getClass().getName())
//    }
//
//    fun readStringArray(obj: JsonObject, key: String): List<String> {
//        return parseJsonArray(obj.getArray(key), { jsonValue -> (jsonValue as JsonString).getStringValue() })
//    }
//
//    fun <T> parseJsonArray(jsonArray: JsonArray, converter: Function<Any, T>): List<T> {
//        val resultArray = ArrayList<T>()
//        jsonArray.forEach { jsonValue -> resultArray.add(converter.apply(jsonValue)) }
//        return resultArray
//    }
//
//
fun <T : Enum<T>> parseEnum(enumStringValue: String, values: Array<T>): T {
    return values.first { mode -> mode.toString().equals(enumStringValue, ignoreCase = true) }
}

    fun <T : Enum<T>> formatEnum(enumValue: T): String {
        return enumValue.toString().toLowerCase()
    }
}

private fun <K, V> HashMap<K, V>.getNumber(key: K) = if (this[key] == null) 0.0 else this[key] as Number

internal fun <K, V> HashMap<K, V?>.getDouble(key: K) = this.getNumber(key).toDouble()

internal fun <K, V> HashMap<K, V>.getObject(key: K): JsonObject {
    return this[key] as JsonObject
}

internal fun <K, V> HashMap<K, V>.getArray(key: K): JsonArray {
    return this[key] as JsonArray
}
