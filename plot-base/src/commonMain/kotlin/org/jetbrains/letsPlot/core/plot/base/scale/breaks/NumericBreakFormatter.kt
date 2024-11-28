/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.max

internal class NumericBreakFormatter(
    value: Double,
    step: Double,
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


        val minExp = expFormat.min ?: DEF_MIN_EXP
        val maxExp = expFormat.max ?: DEF_MAX_EXP

        val domain10Power = log10(abs(value))
        val step10Power = log10(step)

        val precision = when {
            // values is between 0 and 1, formatted with scientific notation
            domain10Power < 0 && step10Power <= minExp -> domain10Power - step10Power + 1 // one extra digit before the dot in scientific notation
            // large range with large step, so the remaining fraction digits are not significant
            domain10Power >= maxExp && step10Power > 2 -> domain10Power - step10Power + 1
            // integer values
            step10Power > 0 -> ceil(domain10Power) // could contain fraction digits because of scientific notation
            // always has fraction digits
            else -> ceil(domain10Power) - step10Power // size of fraction part (-step10Power) + size of integer part
        }
        // type is integer only for steps larger than 1, when there is no breaks using scientific notation
        val type = if (step10Power > max(0, minExp) && domain10Power < maxExp) {
            "d"
        } else {
            "g"
        }
        val comma = type == "g"

        formatter = NumberFormat(NumberFormat.Spec(
            comma = comma,
            precision = ceil(precision - 0.001).toInt(), // round-up precision unless it's very close to smaller int
            trim = true,
            type = type,
            expType = expFormat.notationType,
            minExp = minExp,
            maxExp = maxExp,
        ))
    }

    fun apply(value: Any): String = formatter.apply(value as Number)

    companion object {
        const val DEF_MIN_EXP = -5
        const val DEF_MAX_EXP = 7
    }
}
