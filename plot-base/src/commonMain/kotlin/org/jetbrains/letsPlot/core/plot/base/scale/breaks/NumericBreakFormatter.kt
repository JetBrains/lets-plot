/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import kotlin.math.*

internal class NumericBreakFormatter(
    value: Double,
    step: Double,
    allowMetricPrefix: Boolean,
    expFormat: ExponentFormat
) {
    private var formatter: NumberFormat

    init {
        @Suppress("NAME_SHADOWING")
        val value = if (value == 0.0) {
            // Use very small value instead because log10(0) -> -Infinity.
            Double.MIN_VALUE * 10
        } else {
            abs(value)
        }

        @Suppress("NAME_SHADOWING")
        val step = if (step == 0.0) {
            value / 10
        } else {
            abs(step)
        }


        var type = "g"

        val domain10Power = log10(abs(value))
        val step10Power = log10(step)

        val minExp = expFormat.min ?: NumberFormat.DEF_MIN_EXP
        var precision = -step10Power
        if (domain10Power < 0 && step10Power <= minExp) {
            precision = domain10Power - step10Power + 1
        } else if (domain10Power > 7 && step10Power > 2) {
            precision = domain10Power - step10Power
        }

        if (precision < 0) {
            precision = 0.0
            type = "d"
        } else {
            if (domain10Power > 0) {
                precision += ceil(domain10Power)
            }
        }
        // round-up precision unless it's very close to smaller int.
        precision = ceil(precision - 0.001)

        // Use comma only for large enough numbers.
        val comma = 4 <= domain10Power
        // Use trim to replace 2.00·10^5 -> 2·10^5
        val trim = type == "g"

        formatter = NumberFormat(NumberFormat.Spec(
            comma = comma,
            precision = precision.toInt(),
            trim = trim,
            type = type,
            expType = expFormat.notationType,
            minExp = minExp,
            maxExp = expFormat.max ?: DEF_MAX_EXP,
        ))
    }

    fun apply(value: Any): String = formatter.apply(value as Number)

    companion object {
        const val DEF_MAX_EXP = 6
    }
}
