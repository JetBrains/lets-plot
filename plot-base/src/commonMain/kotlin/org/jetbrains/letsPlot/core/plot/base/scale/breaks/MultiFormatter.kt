/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import kotlin.math.abs
import kotlin.math.min

internal class MultiFormatter(
    private val breakValues: List<Double>,
    private val breakFormatters: List<(Any) -> String>
) {
    init {
        check(breakValues.size == breakFormatters.size) {
            "MultiFormatter: breakValues.size=${breakValues.size} but breakFormatters.size=${breakFormatters.size}"
        }
        if (breakValues.size > 1) {
            val ordered = breakValues
                .mapIndexed { i, v -> if (i == 0) 0.0 else v - breakValues[i - 1] }
                .all { it >= 0.0 }
            check(ordered) { "MultiFormatter: values must be sorted in ascending order. Were: $breakValues." }
        }
    }

    fun apply(v: Any): String {
        v as Double
        return when {
            breakValues.isEmpty() -> v.toString()
            else -> {
                val i = abs(breakValues.binarySearch(v))
                val ii = min(i, breakValues.size - 1)
                breakFormatters[ii](v)
            }
        }
    }
}