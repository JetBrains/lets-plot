/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

abstract class MeasuredInDays protected constructor(count: Int) : TimeInterval(count) {

    protected abstract fun getFirstDayContaining(dateTime: DateTime): Date

    override fun range(start: Double, end: Double): List<Double> {
        // TODO: get rid of hardcoded UTC
        val tz = TimeZone.UTC

        if (start > end) {
            throw RuntimeException("Duration must be positive")
        }

        val dateTimeStart = DateTime.ofEpochMilliseconds(start, tz)

        val dateStart = getFirstDayContaining(dateTimeStart)
        var dateTimeTick = DateTime(dateStart)
        if (dateTimeTick < dateTimeStart) {
            dateTimeTick = addInterval(dateTimeTick)
        }

        val result = ArrayList<Double>()
        var tick = dateTimeTick.toEpochMilliseconds(tz).toDouble()
        while (tick <= end) {
            result.add(tick)
            dateTimeTick = addInterval(dateTimeTick)
            tick = dateTimeTick.toEpochMilliseconds(tz).toDouble()
        }

        return result
    }

    protected abstract fun addInterval(dateTime: DateTime): DateTime

}
