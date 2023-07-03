/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.collections

fun <K, V> Map<K?, V>.filterNotNullKeys(): Map<K, V> {
    return entries
        .asSequence()
        .mapNotNull { (k, v) -> k?.let { k to v } }
        .toMap()
}

fun <T> Collection<T>.splitBy(comp: Comparator<T>): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var chunk = mutableListOf<T>()
    forEach {
        when {
            chunk.isEmpty() -> chunk += it
            comp.compare(chunk.last(), it) == 0 -> chunk += it
            else -> {
                result += chunk
                chunk = mutableListOf(it)
            }
        }
    }
    result += chunk
    return result
}
