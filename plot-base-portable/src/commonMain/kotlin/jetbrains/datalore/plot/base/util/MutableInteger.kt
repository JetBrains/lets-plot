/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.util

class MutableInteger(private var myValue: Int) {

    val andIncrement: Int
        get() = getAndAdd(1)

    fun get(): Int {
        return myValue
    }

    fun getAndAdd(v: Int): Int {
        val prevValue = myValue
        myValue = prevValue + v
        return prevValue
    }

    fun increment() {
        getAndAdd(1)
    }
}
