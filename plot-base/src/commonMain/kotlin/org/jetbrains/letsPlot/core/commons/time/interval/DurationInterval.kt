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
    private val timeUnit: Duration,
    count: Int
) : TimeInterval {

    private val duration: Duration = timeUnit.mul(count)
    override val tickFormatPattern: String =
        when {
//            duration < Duration.SECOND -> "%M:%S" //"%S"
            duration < Duration.MINUTE -> "%M:%S" //"%S"
//            duration < Duration.HOUR -> HourInterval.TICK_FORMAT //"%M"
            duration < Duration.DAY -> "%H:%M"
            else -> DayInterval.TICK_FORMAT
        }

    init {
        check(duration.isPositive) { "Duration must be positive." }
    }

    override fun range(start: Double, end: Double, tz: TimeZone?): List<Double> {
        val step = duration.totalMillis.toDouble()
        // 1-st tick at or just after the start
        val atomicStep = if (timeUnit < Duration.HOUR) {
            step
        } else {
            timeUnit.totalMillis.toDouble() // 1 hour
        }
        var tick = ceil(start / atomicStep) * atomicStep
        val result = ArrayList<Double>()
        while (tick <= end) {
            result.add(tick)
            tick += step
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DurationInterval

        if (timeUnit != other.timeUnit) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeUnit.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }


}
