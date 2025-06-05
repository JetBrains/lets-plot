/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import kotlin.math.ceil

/**
 * Duration interval represents a fixed-length time span (such as "5 minutes" or "2 hours")
 * that can be used for creating regular time-based tick marks on an axis.
 */
internal class DurationInterval(
    timeUnit: Duration,
    count: Int
) : TimeInterval {

    private val duration: Duration = timeUnit.mul(count)
    override val tickFormatPattern: String =
        // Note: previously we compared `timeUnit` with `Duration.SECOND`, `Duration.MINUTE`, etc.
        when {
//            duration < Duration.SECOND -> "%M:%S" //"%S"
            duration < Duration.MINUTE -> "%M:%S" //"%S"
//            duration < Duration.HOUR -> HourInterval.TICK_FORMAT //"%M"
            duration < Duration.DAY -> HourInterval.TICK_FORMAT
            else -> DayInterval.TICK_FORMAT
        }

    init {
        check(duration.isPositive) { "Duration must be positive." }
    }

    override fun range(start: Double, end: Double, tz: TimeZone?): List<Double> {
//        val step = (duration.totalMillis * count).toDouble()
        val step = duration.totalMillis.toDouble()
        var tick = ceil(start / step) * step
        val result = ArrayList<Double>()
        while (tick <= end) {
            result.add(tick)
            tick += step
        }
        return result
    }
}
