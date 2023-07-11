/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.*

internal class TimeZoneMoscow : TimeZone(ID) {
    private val myOldOffset = Duration.HOUR.mul(4)
    private val myNewOffset = Duration.HOUR.mul(3)
    private val myOldTz = TimeZones.offset(null, myOldOffset, UTC)
    private val myNewTz = TimeZones.offset(null, myNewOffset, UTC)

    private val myOffsetChangeTime = DateTime(Date(26, Month.OCTOBER, 2014), Time(2, 0))
    private val myOffsetChangeInstant = myOldTz.toInstant(myOffsetChangeTime)

    override fun toDateTime(instant: Instant): DateTime {
        return if (instant >= myOffsetChangeInstant) {
            myNewTz.toDateTime(instant)
        } else {
            myOldTz.toDateTime(instant)
        }
    }

    override fun toInstant(dateTime: DateTime): Instant {
        return if (dateTime >= myOffsetChangeTime) {
            myNewTz.toInstant(dateTime)
        } else {
            myOldTz.toInstant(dateTime)
        }
    }

    companion object {
        private const val ID = "Europe/Moscow"
    }

}
