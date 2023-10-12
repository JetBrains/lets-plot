/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.PowerFormat

class PowerBreakFormatter(base: Int, value: Double, step: Double, allowMetricPrefix: Boolean) :
    NumericBreakFormatter(value, step, allowMetricPrefix) {
    private val powerFormatter = PowerFormat(base)

    override fun apply(value: Any): String {
        return if (powerFormatter.isPowerDegree(value as Number)) {
            powerFormatter.apply(value)
        } else {
            super.apply(value)
        }
    }
}