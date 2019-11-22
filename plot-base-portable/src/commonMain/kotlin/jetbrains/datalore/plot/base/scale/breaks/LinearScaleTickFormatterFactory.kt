/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

/**
 * @param useMetricPrefix see: https://en.wikipedia.org/wiki/Metric_prefix
 */
/*package*/ internal class LinearScaleTickFormatterFactory(
    private val useMetricPrefix: Boolean
) : QuantitativeTickFormatterFactory() {

    override fun getFormatter(range: ClosedRange<Double>, step: Double): (Any) -> String {
        // avoid 0 values because log10(0) = - Infinity
        var referenceValue = max(abs(range.lowerEndpoint()), range.upperEndpoint())
        if (referenceValue == 0.0) {
            referenceValue = 1.0
        }
        val formatter = NumericBreakFormatter(
            referenceValue,
            step,
            useMetricPrefix
        )
        return formatter::apply
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(log10(0.0))
        }
    }
}
