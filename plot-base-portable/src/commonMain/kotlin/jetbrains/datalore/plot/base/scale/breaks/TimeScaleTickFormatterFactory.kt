/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.common.text.Formatter
import jetbrains.datalore.plot.common.time.interval.NiceTimeInterval
import jetbrains.datalore.plot.common.time.interval.TimeInterval
import jetbrains.datalore.plot.common.time.interval.YearInterval

internal class TimeScaleTickFormatterFactory(
    private val myMinInterval: TimeInterval?
) : QuantitativeTickFormatterFactory() {

    override fun getFormatter(range: ClosedRange<Double>, step: Double): (Any) -> String {
        return Formatter.time(formatPattern(step))
    }

    private fun formatPattern(step: Double): String {
        if (step < 1000) {        // milliseconds
            return TimeInterval.milliseconds(1).tickFormatPattern
        }

        if (myMinInterval != null) {
            // check if we have to hold on minimal interval formatter
            val stepCount = 100
            val start = 0.0
            val end = step * stepCount
            val intervalCount = myMinInterval.range(start, end).size
            if (stepCount >= intervalCount) {
                // step is smaller than min interval -> stay with min interval
                return myMinInterval.tickFormatPattern
            }
        }

        if (step > YearInterval.MS) {        // years
            return YearInterval.TICK_FORMAT
        }

        val interval = NiceTimeInterval.forMillis(step)
        return interval.tickFormatPattern
    }
}
