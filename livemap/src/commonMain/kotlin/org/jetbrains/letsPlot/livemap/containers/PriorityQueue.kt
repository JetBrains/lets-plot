/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.containers

class PriorityQueue<T>(private val comparator: Comparator<T>) {
    private val queue: ArrayList<T> = ArrayList()

    fun add(value: T) {
        var index = queue.binarySearch(value, comparator)

        if (index < 0) {
            index = -index - 1
        }

        queue.add(index, value)
    }

    fun peek(): T? = if (queue.isEmpty()) null else queue[0]

    fun clear() = queue.clear()

    fun toArray() = queue
}