/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.number.PowerFormat
import kotlin.math.*

open class NumericBreakFormatter(
    value: Double,
    step: Double,
    allowMetricPrefix: Boolean,
    base: Int = 10
) {
    private var numericFormatter: NumberFormat
    private val powerFormatter = PowerFormat(
        base,
        min(POWER_FORMATTING_MAX_THRESHOLD, POWER_FORMATTING_THRESHOLD_COEFFICIENT * max(Double.MIN_VALUE, step.absoluteValue))
    )
    private var type = "f"

    init {
        @Suppress("NAME_SHADOWING")
        var value = if (value == 0.0) {
            // Use very small value instead because log10(0) -> -Infinity.
            Double.MIN_VALUE * 10
        } else {
            abs(value)
        }

        @Suppress("NAME_SHADOWING")
        var step = if (step == 0.0) {
            value / 10
        } else {
            abs(step)
        }


        var delimiter = ""

        val domain10Power = log10(abs(value))
        val step10Power = log10(step)

        var precision = -step10Power
        var scientificNotation = false
        if (domain10Power < 0 && step10Power < -4) {
            scientificNotation = true
            type = "e"
            precision = domain10Power - step10Power
        } else if (domain10Power > 7 && step10Power > 2) {
            scientificNotation = true
            precision = domain10Power - step10Power
        }

        if (precision < 0) {
            precision = 0.0
            type = "d"
        }
        // round-up precision ulless it's very close to smaller int.
        precision = ceil(precision - 0.001)

        if (scientificNotation) {
            type = if (domain10Power > 0 && allowMetricPrefix) {
                // generate 'engineering notation', in which the exponent is a multiple of three
                "s"
            } else {
                "e"
            }
        } else {
            delimiter = ","
        }

        numericFormatter = NumberFormat("$delimiter.${precision.toInt()}$type")
    }

    open fun apply(value: Any): String {
        value as Number
        return if (type == "e" && powerFormatter.isPowerDegreeLike(value.toDouble())) {
            powerFormatter.apply(value)
        } else {
            numericFormatter.apply(value)
        }
    }

    companion object {
        private const val POWER_FORMATTING_MAX_THRESHOLD = 1e-9
        private const val POWER_FORMATTING_THRESHOLD_COEFFICIENT = 1e-3
    }
}
