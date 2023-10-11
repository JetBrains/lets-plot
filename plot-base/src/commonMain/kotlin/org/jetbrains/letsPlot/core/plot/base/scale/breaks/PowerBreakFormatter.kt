/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.PowerFormat
import kotlin.math.*

class PowerBreakFormatter(base: Int, value: Double, step: Double, allowMetricPrefix: Boolean) : BreakFormatter {
    private val powerFormatter = PowerFormat(base)
    private val numericFormatter = NumericBreakFormatter(value, step, allowMetricPrefix)
    private var usePowerFormatter = true

    init {
        require(base > 1) { "Base must be greater than 1." }

        if (value == 0.0) {
            usePowerFormatter = false
        } else {
            log(abs(value), base.toDouble()).let { deg ->
                usePowerFormatter = abs(deg - deg.roundToInt()) < POWER_FORMATTING_THRESHOLD
            }
        }
    }

    override fun apply(value: Any): String {
        return if (usePowerFormatter) {
            powerFormatter.apply(value as Number)
        } else {
            numericFormatter.apply(value)
        }
    }

    companion object {
        private const val POWER_FORMATTING_THRESHOLD = 1e-6
    }
}