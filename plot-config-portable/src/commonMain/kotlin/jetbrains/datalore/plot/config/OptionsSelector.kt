/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

fun Map<*, *>.read(vararg query: String): Any? {
    return read(query.dropLast(1), query.last())
}

fun Map<*, *>.read(path: List<String>, item: String): Any? {
    return getMap(path)?.get(item)
}

fun Map<*, *>.write(vararg query: String, value: () -> Any) {
    write(query.dropLast(1), query.last(), value())
}

fun Map<*, *>.write(path: List<String>, item: String, value: Any) {
    provideMap(path)[item] = value
}

fun Map<*, *>.remove(vararg query: String) {
    remove(query.dropLast(1), query.last())
}

fun Map<*, *>.remove(path: List<String>, item: String) {
    getMap(path)?.asMutable()?.remove(item)
}

fun Map<*, *>.has(vararg query: String): Boolean {
    return has(query.dropLast(1), query.last())
}

fun Map<*, *>.has(path: List<String>, item: String): Boolean {
    return getMap(path)?.containsKey(item) ?: false
}

fun Map<*, *>.getMap(vararg query: String): Map<String, Any>? {
    return getMap(query.toList())?.typed()
}

fun Map<*, *>.getMap(path: List<String>): Map<String, Any>? {
    return path.fold<String, Map<*, *>?>(this, { section, next -> section?.read(next)?.let { it as? Map<*, *> } ?: return@fold null } )?.typed()
}

fun Map<*, *>.getList(vararg query: String): List<*>? {
    return getList(query.dropLast(1), query.last())
}

fun Map<*, *>.getList(path: List<String>, item: String): List<*>? {
    return getMap(path)?.get(item) as? List<*>
}

fun Map<*, *>.getMaps(vararg query: String): List<Map<*, *>>? {
    return getList(*query)?.mapNotNull { it as? Map<*, *> }?.toList()
}

fun Map<*, *>.provideMap(vararg query: String): MutableMap<String, Any> {
    return provideMap(query.toList())
}

fun Map<*, *>.provideMap(path: List<String>): MutableMap<String, Any> {
    return path.fold(
        this,
        { acc, next ->
            acc.asMutable().getOrPut(
                key = next,
                defaultValue = { HashMap<String, Any>() }
            ) as Map<*, *>
        }
    ).asMutable()
}

fun Map<*, *>.provideMaps(vararg query: String): MutableList<Map<*, *>> {
    return provideMaps(query.dropLast(1), query.last()).asMutable()
}

fun Map<*, *>.provideMaps(path: List<String>, item: String): MutableList<Map<*, *>> {
    @Suppress("UNCHECKED_CAST")
    return provideMap(path).getOrPut(item, { mutableListOf<Map<*, *>>() }) as MutableList<Map<*, *>>
}

@Suppress("UNCHECKED_CAST")
fun Map<*, *>.asMutable(): MutableMap<String, Any> {
    return this as MutableMap<String, Any>
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<*, *>.typed(): Map<K, V> {
    return this as Map<K, V>
}

@Suppress("UNCHECKED_CAST")
fun <T> List<T>.asMutable(): MutableList<T> {
    return this as MutableList<T>
}

@Suppress("UNCHECKED_CAST")
fun List<*>.asMaps(): List<Map<*, *>> {
    return this as List<Map<*, *>>
}

