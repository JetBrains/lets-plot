/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil.formatterDateUTC
import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.YearInterval

internal class TimeScaleTickFormatterFactory(
    private val minInterval: TimeInterval?
) {

    fun getFormatter(step: Double): (Number) -> String {
        return formatterDateUTC(formatPattern(step))
    }

    private fun formatPattern(step: Double): String {
        if (step < 1000) {        // milliseconds
            return TimeInterval.milliseconds(1).tickFormatPattern
        }

        if (minInterval != null) {
            // check if we have to hold on minimal interval formatter
            val stepCount = 100
            val start = 0.0
            val end = step * stepCount
            val intervalCount = minInterval.range(start, end).size
            if (stepCount >= intervalCount) {
                // step is smaller than min interval -> stay with min interval
                return minInterval.tickFormatPattern
            }
        }

        if (step > YearInterval.MS) {        // years
            return YearInterval.TICK_FORMAT
        }

        val interval = NiceTimeInterval.forMillis(step)
        return interval.tickFormatPattern
    }
}
