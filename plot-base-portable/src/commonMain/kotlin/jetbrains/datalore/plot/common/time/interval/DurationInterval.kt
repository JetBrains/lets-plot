/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import kotlin.math.ceil

internal class DurationInterval(private val myDuration: Duration, count: Int) : TimeInterval(count) {

    override// milliseconds
    // fractional seconds
    // seconds
    // minutes
    // hours
    // days
    // weeks
    val tickFormatPattern: String
        get() {
            val duration = myDuration.duration
            if (duration < Duration.SECOND.duration) {
                return "%S"
            } else if (duration < Duration.MINUTE.duration) {
                return "%S"
            } else if (duration < Duration.HOUR.duration) {
                return "%M"
            } else if (duration < Duration.DAY.duration) {
                return "%H:%M"
            } else if (duration < Duration.WEEK.duration) {
                return "%b %e"
            }
            return "%b %e"
        }

    init {
        if (!myDuration.isPositive) {
            throw RuntimeException("Duration must be positive")
        }
    }

    override fun range(start: Double, end: Double): List<Double> {
        val step = (myDuration.duration * count).toDouble()
        var tick = ceil(start / step) * step
        val result = ArrayList<Double>()
        while (tick <= end) {
            result.add(tick)
            tick += step
        }
        return result
    }
}
