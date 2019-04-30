package jetbrains.datalore.visualization.plot.gog.core.scale.breaks

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

/*package*/ internal class LinearScaleTickFormatterFactory
/**
 * @param useMetricPrefix see: https://en.wikipedia.org/wiki/Metric_prefix
 */
(private val myUseMetricPrefix: Boolean) : QuantitativeTickFormatterFactory() {

    override fun getFormatter(range: ClosedRange<Double>, step: Double): Function<Any, String> {
        // avoid 0 values because log10(0) = - Infinity
        var referenceValue = max(abs(range.lowerEndpoint()), range.upperEndpoint())
        if (referenceValue == 0.0) {
            referenceValue = 1.0
        }

        return NumericBreakFormatter(referenceValue, step, myUseMetricPrefix)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(log10(0.0))
        }
    }
}
