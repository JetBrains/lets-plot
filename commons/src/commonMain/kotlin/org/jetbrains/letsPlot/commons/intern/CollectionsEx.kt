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

// Returns indices (i, j) such as list[i] < element <= list[j];
// or (0, 1) if element is equal to the first element and less than the second element.
// List is expected to be sorted in ascending order, can contain duplicate elements and should have at least two elements.
fun <T : Comparable<T>> List<T>.bracketingIndicesOrNull(element: T): Pair<Int, Int>? {
    if (size < 2) return null
    val j = -binarySearch { if (it < element) -1 else 1 } - 1 // first index with list[j] >= element
    if (j == size) return null // element is greater than all list elements
    if (j > 0) return (j - 1) to j // list[j-1] < element == list[j]
    return if (this[0] == element) {
        val k = -binarySearch { if (it <= element) -1 else 1 } - 1 // first index with list[k] > element
        if (k < size) (k - 1) to k else null // list[k-1] < element < list[k], or (0, 1) if k == 1
    } else null
}