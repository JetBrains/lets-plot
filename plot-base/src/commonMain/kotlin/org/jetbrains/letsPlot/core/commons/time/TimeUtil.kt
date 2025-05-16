/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import kotlin.math.roundToLong

object TimeUtil {
    fun asDateTimeUTC(instant: Double): DateTime {
        try {
            return (Instant(instant.roundToLong())).toDateTime(TimeZone.UTC)
        } catch (ignored: RuntimeException) {
            throw IllegalArgumentException("Can't create DateTime from instant $instant")
        }

    }

    fun asInstantUTC(dateTime: DateTime): Long {
        return dateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
    }

    fun yearStart(year: Int): DateTime {
        return DateTime(
            Date(1, Month.JANUARY, year),
            Time.DAY_START
        )
    }
}
