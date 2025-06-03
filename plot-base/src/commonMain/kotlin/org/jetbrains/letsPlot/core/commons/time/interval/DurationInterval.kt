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
    private val duration: Duration, count: Int
) : TimeInterval(count) {

    override val tickFormatPattern: String
        get() {
            val duration = duration.totalMillis
            if (duration < Duration.SECOND.totalMillis) {
                return "%S"
            } else if (duration < Duration.MINUTE.totalMillis) {
                return "%S"
            } else if (duration < Duration.HOUR.totalMillis) {
                return "%M"
            } else if (duration < Duration.DAY.totalMillis) {
                return "%H:%M"
//            } else if (duration < Duration.WEEK.totalMillis) {
//                return "%b %e"
            }

            // This should not happen.
            return "%b %e"
        }

    init {
        check(duration.isPositive) { "Duration must be positive." }
    }

    override fun range(start: Double, end: Double, tz: TimeZone?): List<Double> {
        val step = (duration.totalMillis * count).toDouble()
        var tick = ceil(start / step) * step
        val result = ArrayList<Double>()
        while (tick <= end) {
            result.add(tick)
            tick += step
        }
        return result
    }
}
