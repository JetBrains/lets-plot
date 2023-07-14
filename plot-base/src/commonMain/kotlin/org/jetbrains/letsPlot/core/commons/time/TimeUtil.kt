/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone
import kotlin.math.roundToLong

object TimeUtil {
    fun asDateTimeUTC(instant: Double): DateTime {
        try {
            return TimeZone.UTC.toDateTime(Instant(instant.roundToLong()))
        } catch (ignored: RuntimeException) {
            throw IllegalArgumentException("Can't create DateTime from instant $instant")
        }

    }

    fun asInstantUTC(dateTime: DateTime): Long {
        return TimeZone.UTC.toInstant(dateTime).timeSinceEpoch
    }

    fun yearStart(year: Int): DateTime {
        return DateTime(Date.firstDayOf(year))
    }
}
