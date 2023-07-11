/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json

typealias Arr = List<*>
typealias Obj = Map<*, *>

fun streamOf(arr: Arr): Sequence<*> = arr.asSequence()
fun objectsStreamOf(arr: Arr): Sequence<Obj> = streamOf(arr).map { it as Obj }
fun stringStreamOf(arr: Arr): Sequence<String?> = streamOf(arr).map { it as String? }

fun isBoolean(e: Any) = e is Boolean
fun isNumber(e: Any) = e is Number
fun isString(e: Any) = e is String

fun getAsDouble(v: Any) = (v as Number).toDouble()
fun getAsInt(v: Any?) = (v as Number).toInt()
fun getAsBoolean(v: Any) = v as Boolean

fun containsString(obj: MutableMap<String, Any?>, key: String): Boolean =
    when (val v = obj[key]) {
        null, isString(v) -> true
        else -> false
    }

fun getAsString(e: Any?): String? =
    when (e) {
        null -> null
        else -> e as String
    }

fun <T : Enum<T>> parseEnum(enumStringValue: String, values: Array<T>): T =
    values.first { mode -> mode.toString().equals(enumStringValue, ignoreCase = true) }

inline fun <reified T : Enum<T>> parseEnum(enumStringValue: String): T = parseEnum(enumStringValue, enumValues<T>())
fun <T : Enum<T>> formatEnum(enumValue: T): String = enumValue.toString().lowercase()

fun <T : Enum<T>> FluentObject.put(key: String, v: Collection<T>) = this.put(key, v.map { formatEnum(it) })
fun FluentObject.put(key: String, v: List<String>) = put(key, FluentArray().addStrings(v.map { it }))

fun Map<*, *>.getNumber(key: String) = if (this[key] == null) 0.0 else this[key] as Number
fun Map<*, *>.getDouble(key: String) = this.getNumber(key).toDouble()
fun Map<*, *>.getString(key: String) = this[key] as String
fun Map<*, *>.getObj(key: String) = this[key] as Obj
fun Map<*, *>.getArr(key: String) = this[key] as Arr


//    private fun toObject(v: Any?): Any? {
//        return when (v) {
//            null -> null
//            is String -> v
//            is Number -> v
//            is Boolean -> v
//            is Map<*, *> -> toMap(v)
//            is List<*> -> toArray(v)
//            else -> throw IllegalArgumentException("Unknown type: ${v.toString()}")
//        }
//    }


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
//    fun getArr(obj: JsonObject, key: String): JsonArray {
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
//        return parseJsonArray(obj.getArr(key), { jsonValue -> (jsonValue as JsonString).getStringValue() })
//    }
//
//    fun <T> parseJsonArray(jsonArray: JsonArray, converter: Function<Any, T>): List<T> {
//        val resultArray = ArrayList<T>()
//        jsonArray.forEach { jsonValue -> resultArray.add(converter.apply(jsonValue)) }
//        return resultArray
//    }
//
//
