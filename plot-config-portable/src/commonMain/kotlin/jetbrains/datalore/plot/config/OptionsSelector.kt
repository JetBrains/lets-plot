/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

fun Map<*, *>.read(vararg query: String): Any? {
    return read(query.dropLast(1), query.last())
}

fun Map<*, *>.read(path: List<String>, item: String): Any? {
    return section(path)?.get(item)
}

fun Map<*, *>.write(vararg query: String, value: () -> Any) {
    write(query.dropLast(1), query.last(), value())
}

fun Map<*, *>.write(path: List<String>, item: String, value: Any) {
    provideSection(path)[item] = value
}

fun Map<*, *>.remove(vararg query: String) {
    remove(query.dropLast(1), query.last())
}

fun Map<*, *>.remove(path: List<String>, item: String) {
    section(path)?.asMutable()?.remove(item)
}

fun Map<*, *>.has(vararg query: String): Boolean {
    return has(query.dropLast(1), query.last())
}

fun Map<*, *>.has(path: List<String>, item: String): Boolean {
    return section(path)?.containsKey(item) ?: false
}

fun Map<*, *>.section(vararg query: String): Map<String, Any>? {
    return section(query.toList())?.typed()
}

fun Map<*, *>.section(path: List<String>): Map<String, Any>? {
    return path.fold<String, Map<*, *>?>(this, { section, next -> section?.read(next)?.let { it as? Map<*, *> } ?: return@fold null } )?.typed()
}

fun Map<*, *>.list(vararg query: String): List<*>? {
    return list(query.dropLast(1), query.last())
}

fun Map<*, *>.list(path: List<String>, item: String): List<*>? {
    return section(path)?.get(item) as? List<*>
}

fun Map<*, *>.sections(vararg query: String): List<Map<*, *>>? {
    return list(*query)?.mapNotNull { it as? Map<*, *> }?.toList()
}

fun Map<*, *>.provideSection(vararg query: String): MutableMap<String, Any> {
    return provideSection(query.toList())
}

fun Map<*, *>.provideSection(path: List<String>): MutableMap<String, Any> {
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

fun Map<*, *>.provideSections(vararg query: String): MutableList<Map<*, *>> {
    return provideSections(query.dropLast(1), query.last()).asMutable()
}

fun Map<*, *>.provideSections(path: List<String>, item: String): MutableList<Map<*, *>> {
    @Suppress("UNCHECKED_CAST")
    return provideSection(path).getOrPut(item, { mutableListOf<Map<*, *>>() }) as MutableList<Map<*, *>>
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
fun List<*>.asSections(): List<Map<*, *>> {
    return this as List<Map<*, *>>
}

