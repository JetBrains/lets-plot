/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory

fun Map<*, *>.getElement(path: List<Any>): Any? {
    if (path.isEmpty()) {
        return this
    }

    return path.fold<Any, Any>(this) { cont, key ->
        when (key) {
            is String -> {
                require(cont is Map<*, *>) { "Expected Map but found: ${cont::class.simpleName}" }
                cont[key]
            }

            is Int -> {
                require(cont is List<*>) { "Expected List but found: ${cont::class.simpleName}" }
                cont[key]
            }

            else -> error("Unexpected item type: $key")
        } ?: return null
    }
}

fun Map<*, *>.read(path: List<Any>): Any? {
    return getElement(path)
}

fun Map<*, *>.write(path: List<String>, item: String, value: Any) {
    provideMap(path)[item] = value
}

fun Map<*, *>.remove(path: List<Any>, item: Any) {
    getMap(path)?.asMutable()?.remove(item)
}

fun Map<*, *>.has(path: List<Any>, item: Any): Boolean {
    return getMap(path)?.containsKey(item) ?: false
}

fun Map<*, *>.getString(path: List<Any>): String? {
    return getElement(path) as? String
}

fun Map<*, *>.getDouble(path: List<Any>): Double? {
    return getNumber(path)?.toDouble()
}

fun Map<*, *>.getInt(path: List<Any>): Int? {
    return getNumber(path)?.toInt()
}

fun Map<*, *>.getNumber(path: List<Any>): Number? {
    return getElement(path) as? Number
}

fun Map<*, *>.getBool(path: List<Any>, item: Any): Boolean? {
    return when (val v = getMap(path)?.get(item)) {
        is String -> when (v.lowercase()) {
            "1", "true" -> true
            "0", "false" -> false
            else -> throw IllegalArgumentException("Unexpected boolean value: '$v'")
        }

        is Number -> v.toInt() != 0
        is Boolean -> v
        else -> null
    }
}

inline fun <reified EnumT : Enum<EnumT>> Map<*, *>.getEnum(path: List<Any>): EnumT? {
    val name = getString(path) ?: return null
    val enumInfo = EnumInfoFactory.createEnumInfo<EnumT>()
    val value = enumInfo.safeValueOf(name)
    require(value != null) {
        "Unknown value \'$name\'. Expected: " + enumInfo.originalNames.joinToString(
            prefix = " [",
            separator = "|",
            postfix = "]"
        ) { "'${it.lowercase()}'" }
    }
    return value
}

fun Map<*, *>.getMap(path: List<Any>): Map<String, Any>? {
    return (getElement(path) as? Map<*, *>)?.typed()
}

fun Map<*, *>.getList(path: List<Any>): List<*>? {
    return getElement(path) as? List<*>
}

fun Map<*, *>.provideMap(path: List<String>): MutableMap<String, Any> {
    return path.fold(this) { cont, key ->
        cont.asMutable().getOrPut(key) { HashMap<String, Any>() } as Map<*, *>
    }.asMutable()
}

fun Map<*, *>.provideMaps(path: List<String>, item: String): MutableList<Map<*, *>> {
    @Suppress("UNCHECKED_CAST")
    return provideMap(path).getOrPut(item) {
        mutableListOf<Map<*, *>>()
    } as MutableList<Map<*, *>>
}

@Suppress("UNCHECKED_CAST")
fun Map<*, *>.asMutable(): MutableMap<String, Any> {
    return this as MutableMap<String, Any>
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<*, *>.typed(strict: Boolean = false): Map<K, V> {
    if (strict) {
        onEach { (k, v) -> k as K; v as V }
    }
    return this as Map<K, V>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> List<*>.typed(strict: Boolean = false): List<T> {
    if (strict) {
        onEach { it as T }
    }
    return this as List<T>
}

fun <T> List<T>.asMutable(): MutableList<T> {
    return this as MutableList<T>
}

@Suppress("UNCHECKED_CAST")
fun List<*>.asMaps(): List<Map<*, *>> {
    return this as List<Map<*, *>>
}

@Suppress("UNCHECKED_CAST")
fun Map<*, *>.asMapOfMaps(): Map<*, Map<*, *>> {
    return this as Map<*, Map<*, *>>
}

// varargs version

fun Map<*, *>.read(vararg query: Any): Any? {
    return read(query.toList())
}

fun Map<*, *>.write(vararg query: String, value: () -> Any) {
    write(query.dropLast(1), query.last(), value())
}

fun Map<*, *>.remove(vararg query: Any) {
    remove(query.dropLast(1), query.last())
}

fun Map<*, *>.has(vararg query: Any): Boolean {
    return has(query.dropLast(1), query.last())
}

fun Map<*, *>.getString(vararg query: Any): String? {
    return getString(query.toList())
}

fun Map<*, *>.getDouble(vararg query: Any): Double? {
    return getDouble(query.toList())
}

fun Map<*, *>.getInt(vararg query: Any): Int? {
    return getInt(query.toList())
}

fun Map<*, *>.getNumber(vararg query: Any): Number? {
    return getNumber(query.toList())
}

fun Map<*, *>.getBool(vararg query: Any): Boolean? {
    return getBool(query.dropLast(1), query.last())
}

inline fun <reified EnumT : Enum<EnumT>> Map<*, *>.getEnum(vararg query: Any): EnumT? {
    return getEnum<EnumT>(query.toList())
}

fun Map<*, *>.getMap(vararg query: Any): Map<String, Any>? {
    return getMap(query.toList())?.typed()
}

fun Map<*, *>.getList(vararg query: Any): List<*>? {
    return getList(query.toList())
}

fun Map<*, *>.getMaps(vararg query: Any): List<Map<*, *>>? {
    return getList(*query)?.mapNotNull { it as? Map<*, *> }?.toList()
}

fun Map<*, *>.provideMap(vararg query: String): MutableMap<String, Any> {
    return provideMap(query.toList())
}

fun Map<*, *>.provideMaps(vararg query: String): MutableList<Map<*, *>> {
    return provideMaps(query.dropLast(1), query.last()).asMutable()
}

fun emptyObj(): Map<String, Any?> = emptyMap()
fun emptyData(): Map<String, List<Any?>> = emptyMap()