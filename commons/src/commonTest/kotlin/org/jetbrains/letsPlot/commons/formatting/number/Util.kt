/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

object Util {
    const val DOUBLE_ALMOST_MIN_VALUE = 1.00e-323 // MIN_VALUE in JVM and JS has diff value -4.90e-324 vs 5.00e-324. Use own value to avoid platform-specific tests
}

internal fun format(spec: String, v: Number): String = NumberFormat(spec).apply(v)
internal fun format(v: Number, spec: String): String = NumberFormat(spec).apply(v)
internal fun format(spec: String): NumberFormat = NumberFormat(spec)
