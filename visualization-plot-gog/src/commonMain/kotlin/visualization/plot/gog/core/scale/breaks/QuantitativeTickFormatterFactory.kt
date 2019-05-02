package jetbrains.datalore.visualization.plot.gog.core.scale.breaks

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.common.time.interval.TimeInterval
import kotlin.jvm.JvmOverloads

abstract class QuantitativeTickFormatterFactory {

    fun getFormatter(step: Double): Function<in Any, String> {
        return getFormatter(ClosedRange.closed(0.0, 0.0), step)
    }

    abstract fun getFormatter(range: ClosedRange<Double>, step: Double): Function<in Any, String>

    companion object {

        @JvmOverloads
        fun forLinearScale(useMetricPrefix: Boolean = true): QuantitativeTickFormatterFactory {
            return LinearScaleTickFormatterFactory(useMetricPrefix)
        }

        fun forTimeScale(minInterval: TimeInterval?): QuantitativeTickFormatterFactory {
            return TimeScaleTickFormatterFactory(minInterval)
        }
    }
}
