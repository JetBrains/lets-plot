/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

abstract class TimeZoneAwareInterval protected constructor(
    count: Int,
) : TimeInterval(count) {

    protected abstract fun atOrBefore(dateTime: DateTime): DateTime

    protected abstract fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime

    final override fun range(start: Double, end: Double, tz: TimeZone?): List<Double> {
        if (start > end) {
            throw RuntimeException("Duration must be positive")
        }

        val tz = tz ?: TimeZone.UTC

        val startDateTime = DateTime.ofEpochMilliseconds(start, tz)
        var nextDateTime = atOrBefore(startDateTime).let {
            if (it < startDateTime) {
                addInterval(it, tz)
            } else {
                it
            }
        }

        val result = ArrayList<Double>()
        var nextTick = nextDateTime.toEpochMilliseconds(tz).toDouble()
        while (nextTick <= end) {
            result.add(nextTick)

            nextDateTime = addInterval(nextDateTime, tz)
            nextTick = nextDateTime.toEpochMilliseconds(tz).toDouble()
        }

        return result
    }
}
