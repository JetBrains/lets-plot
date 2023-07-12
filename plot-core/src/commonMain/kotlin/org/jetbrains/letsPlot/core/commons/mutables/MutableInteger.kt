/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.mutables

class MutableInteger(private var value: Int) {

    val andIncrement: Int
        get() = getAndAdd(1)

    fun get(): Int {
        return value
    }

    fun getAndAdd(v: Int): Int {
        val prevValue = value
        value = prevValue + v
        return prevValue
    }

    fun increment() {
        getAndAdd(1)
    }
}