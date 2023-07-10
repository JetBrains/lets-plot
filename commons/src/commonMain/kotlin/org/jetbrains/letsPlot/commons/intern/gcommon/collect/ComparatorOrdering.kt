/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.gcommon.collect

internal class ComparatorOrdering<T>(comparator: Comparator<T>) : Ordering<T>() {
    private val myComparator: Comparator<T> = comparator

    override fun compare(a: T, b: T): Int {
        return myComparator.compare(a, b)
    }
}
