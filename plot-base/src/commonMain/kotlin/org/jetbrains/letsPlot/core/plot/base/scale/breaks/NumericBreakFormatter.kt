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
        val maxExp = expFormat.max ?: DEF_MAX_EXP
        var precision = -step10Power
        if (domain10Power < 0 && step10Power <= minExp) { // values is between 0 and 1; formatted with scientific notation
            val fractionPrecision = domain10Power - step10Power
            precision = fractionPrecision + 1 // one extra digit before the dot in scientific notation
        } else if (domain10Power > 7 && step10Power > 2) { // large range with large step, so the remaining digits are not significant
            precision = domain10Power - step10Power
            if (domain10Power >= maxExp) {
                precision += 1 // one extra digit before the dot in scientific notation
            }
        }

        if (precision < 0) {
            precision = 0.0
            type = "d"
        } else {
            if (domain10Power > 0 && (domain10Power <= 7 || step10Power <= 2)) { // not to large values, so digits before the dot are significant
                precision += ceil(domain10Power)
            }
        }
        // round-up precision unless it's very close to smaller int.
        precision = ceil(precision - 0.001)

        // use comma only for large enough numbers.
        val comma = 4 <= domain10Power
        // use trim to replace 2.00 -> 2
        val trim = type == "g"

        formatter = NumberFormat(NumberFormat.Spec(
            comma = comma,
            precision = precision.toInt(),
            trim = trim,
            type = type,
            expType = expFormat.notationType,
            minExp = minExp,
            maxExp = maxExp,
        ))
    }

    fun apply(value: Any): String = formatter.apply(value as Number)

    companion object {
        const val DEF_MAX_EXP = 6
    }
}
