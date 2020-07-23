/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.common.time.interval.TimeInterval
import kotlin.jvm.JvmOverloads

abstract class QuantitativeTickFormatterFactory {

    fun getFormatter(step: Double): (Any) -> String {
        return getFormatter(ClosedRange(0.0, 0.0), step)
    }

    abstract fun getFormatter(range: ClosedRange<Double>, step: Double): (Any) -> String

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
