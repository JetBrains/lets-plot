/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern

fun <K, V> Map<K?, V>.filterNotNullKeys(): Map<K, V> {
    return entries
        .asSequence()
        .mapNotNull { (k, v) -> k?.let { k to v } }
        .toMap()
}

fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
    return entries
        .asSequence()
        .mapNotNull { (k, v) -> v?.let { k to v } }
        .toMap()
}

fun <T> Collection<T>.splitBy(comp: Comparator<T>): List<List<T>> {
    if (isEmpty()) {
        return emptyList()
    }

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

@Suppress("UNCHECKED_CAST")
fun <T> Collection<T?>.splitByNull(): List<List<T>> {
    return splitBy(compareBy { it == null })
        .filter { null !in it } as List<List<T>>
}

fun <T> Iterable<T>.indicesOf(predicate: (T) -> Boolean) =
    mapIndexedNotNull{ i, elem -> i.takeIf{ predicate(elem) } }

fun <T : Comparable<T>> List<T?>.predecessorIndexOrNull(element: T?): Int? =
    binarySearch(element).let { i ->
        when {
            i > 0 -> i - 1 // element is in the list, return predecessor index
            i == 0 -> 0 // element is first in the list, return its index
            i == -1 -> null // element is less than the first element, no predecessor
            i <= -(size + 1) -> null // element is greater than the last element, no predecessor
            else -> -i - 2 // element is between two elements, return predecessor index
        }
    }