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

// Returns indices (i, j) such as list[i] < element <= list[j]; or (0, 1) if element == list.first().
// List is expected to be sorted in ascending order, doesn't contain duplicates and should have at least two elements.
fun <T : Comparable<T>> List<T>.bracketingIndicesOrNull(element: T): Pair<Int, Int>? {
    require(size >= 2) { "List should have at least two elements" }
    return binarySearch(element).let { i ->
        val upperIndex = when {
            i == 0 -> 1 // special case: element == list.first()
            i > 0 -> i // element found in the list
            else -> -i - 1 // element not found; insertion point
        }
        when (upperIndex) {
            0 -> null // element < list.first()
            size -> null // element > list.last()
            else -> Pair(upperIndex - 1, upperIndex)
        }
    }
}