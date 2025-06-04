/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.YearInterval

internal class TimeScaleTickFormatterFactory(
    private val minInterval: NiceTimeInterval?,
    private val maxInterval: NiceTimeInterval?,
) {
    internal fun formatPattern(step: Double): String {
        // milliseconds
        if (step < 1000) {
            return if (minInterval != null) {
                (minInterval as TimeInterval).tickFormatPattern
            } else {
                TimeInterval.milliseconds(1).tickFormatPattern
            }
        }

        // years
        if (step > YearInterval.MS) {
            return if (maxInterval != null) {
                (maxInterval as TimeInterval).tickFormatPattern
            } else {
                YearInterval.TICK_FORMAT
            }
        }

//        if (minInterval != null) {
//            // check if we have to hold on minimal interval formatter
//            val stepCount = 100
//            val start = 0.0
//            val end = step * stepCount
//            val intervalCount = minInterval.range(start, end, tz = null).size
//            if (stepCount >= intervalCount) {
//                // step is smaller than the min interval-> stay with the min interval
//                return minInterval.tickFormatPattern
//            }
//        }


        val interval = NiceTimeInterval.forMillis(step, minInterval, maxInterval)
        return interval.tickFormatPattern
    }
}
