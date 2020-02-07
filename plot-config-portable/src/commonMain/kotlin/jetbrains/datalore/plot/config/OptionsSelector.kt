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
    provideSection(path).asMutable()[item] = value
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

fun Map<*, *>.section(vararg query: String): Map<*, *>? {
    return section(query.toList())
}

fun Map<*, *>.section(path: List<String>): Map<*, *>? {
    return path.fold<String, Map<*, *>?>(this, { section, next -> section?.read(next)?.let { it as Map<*, *> } ?: return@fold null } )
}

fun Map<*, *>.list(vararg query: String): List<*>? {
    return list(query.dropLast(1), query.last())
}

fun Map<*, *>.list(path: List<String>, item: String): List<*>? {
    return section(path)?.get(item) as? List<*>
}

fun Map<*, *>.provideSection(path: List<String>): Map<*, *> {
    return path.fold(this, { section, next -> section.asMutable().getOrPut(next, { HashMap<String, Any>() }) as Map<*, *> })
}

@Suppress("UNCHECKED_CAST")
fun Map<*, *>.asMutable(): MutableMap<String, Any> {
    return this as MutableMap<String, Any>
}
