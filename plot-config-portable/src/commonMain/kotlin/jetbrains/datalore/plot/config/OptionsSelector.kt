/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

fun Map<*, *>.select(vararg query: String): Any? {
    return read(query.dropLast(1), query.last())
}

fun Map<*, *>.write(vararg query: String, value: () -> Any) {
    write(query.dropLast(1), query.last(), value())
}

fun Map<*, *>.remove(vararg query: String) {
    remove(query.dropLast(1), query.last())
}

private fun Map<*, *>.read(path: List<String>, item: String): Any? {
    return getSection(path)?.get(item)
}

private fun Map<*, *>.write(path: List<String>, item: String, value: Any) {
    provideSection(path).asMutable()[item] = value
}

private fun Map<*, *>.remove(path: List<String>, item: String) {
    getSection(path)?.asMutable()?.remove(item)
}

private fun Map<*, *>.provideSection(path: List<String>): Map<*, *> {
    return path.fold(this, { section, next -> section.asMutable().getOrPut(next, { HashMap<String, Any>() }) as Map<*, *> })
}

private fun Map<*, *>.getSection(path: List<String>): Map<*, *>? {
    return path.fold<String, Map<*, *>?>(this, { section, next -> section?.get(next)?.let { it as Map<*, *> } ?: return@fold null } )
}

@Suppress("UNCHECKED_CAST")
private fun Map<*, *>.asMutable(): MutableMap<String, Any> {
    return this as MutableMap<String, Any>
}
