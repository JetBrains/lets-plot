/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.testing

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.abs

fun <T> assertContentEquals(
    expected: List<T>,
    actual: List<T>,
    eqComparer: (T, T) -> Boolean = { t1, t2 ->
        if (t1 != null && t2 != null) t1 == t2
        else if (t1 == null && t2 == null) true
        else false
    }
) {
    require(expected.size == actual.size) {
        "Expected list size: ${expected.size}, actual: ${actual.size}"
    }

    expected.zip(actual).forEachIndexed { index, (e, a) ->
        require(eqComparer(e, a)) {
            "Non equal elements at index $index: \nexpected:$e\n but was:$a"
        }
    }
}

fun doubleVectorEqComparer(precision: Double): (DoubleVector, DoubleVector) -> Boolean {
    return { v1, v2 ->  abs(v1.x - v2.x) <= precision && abs(v1.y - v2.y) <= precision }
}
