/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeBeforeUnixEpochTest {

    @Test
    fun subtractDayOnDate() {
        assertEquals(Date(31, Month.DECEMBER, 1969), Date.EPOCH.subtractDays(1))
    }

    @Test
    fun subtractDayInMillis() {
        val actual = toDateTime(-Duration.DAY.duration)
        assertEquals(DateTime(Date(31, Month.DECEMBER, 1969), Time(0, 0)), actual)
    }

    @Test
    fun subtractSeconds() {
        assertEquals(Date(31, Month.DECEMBER, 1969), toDateTime(-3601).date)
    }

    @Test
    fun deepToThePastWithBorrowing() {
        val actual = toDateTime(-6857218676544)
        assertEquals(DateTime(Date(14, Month.SEPTEMBER, 1752), Time(1, 2, 3, 456)), actual)
    }

    @Test
    fun borrowingTest() {
        val epoch = Duration.DAY.mul(8).add(Duration.MS.mul(999)).duration
        val actual = toDateTime(-epoch)
        assertEquals(DateTime(Date(23, Month.DECEMBER, 1969), Time(23, 59, 59, 1)), actual)
    }

    private fun toDateTime(epochMillis: Long): DateTime {
        return TimeZone.UTC.toDateTime(Instant(epochMillis))
    }
}