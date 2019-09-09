package jetbrains.datalore.visualization.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

/*package*/ internal class LinearScaleTickFormatterFactory
/**
 * @param useMetricPrefix see: https://en.wikipedia.org/wiki/Metric_prefix
 */
(private val useMetricPrefix: Boolean) : QuantitativeTickFormatterFactory() {

    override fun getFormatter(range: ClosedRange<Double>, step: Double): (Any) -> String {
        // avoid 0 values because log10(0) = - Infinity
        var referenceValue = max(abs(range.lowerEndpoint()), range.upperEndpoint())
        if (referenceValue == 0.0) {
            referenceValue = 1.0
        }

        return { it -> NumericBreakFormatter(referenceValue, step, useMetricPrefix).apply(it) }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(log10(0.0))
        }
    }
}
