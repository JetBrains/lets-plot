/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

internal class MultiOrdering<K : Comparable<K>>(private val myKeys: List<K?>) {
    private val myIndices: MutableList<Int>

    init {
        myIndices = ArrayList(myKeys.size)
        for (i in myKeys.indices) {
            myIndices.add(i)
        }

        myIndices.sortWith(Comparator { i: Int?, j: Int? ->
            val keyI = myKeys[i!!]
            val keyJ = myKeys[j!!]
            when {
                keyI === keyJ -> 0
                keyI == null -> -1
                keyJ == null -> 1
                else -> keyI.compareTo(keyJ)
            }
        })
    }

    fun <T> sortedCopy(l: List<T?>): List<T?> {
        require(l.size == myIndices.size) { "Expected size " + myIndices.size + " but was size " + l.size }
        val copy = ArrayList<T?>(myIndices.size)
        for (oldIndex in myIndices) {
            val v = l[oldIndex]
            copy.add(v)
        }
        return copy
    }

    fun sortedCopyOfKeys(): List<K?> {
        return sortedCopy(myKeys)
    }
}
