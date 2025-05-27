/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("PrivatePropertyName")
class DateTimeBeforeUnixEpochTest {
    private val instant1969dec31_h00m00s00 = Instant(-Duration.DAY.duration) // -86400000
    private val datetime1969dec31_h00m00s00 = DateTime(Date(31, Month.DECEMBER, 1969), Time(0, 0))

    private val instant1969dec31_h23m59s59ms999 = Instant(-1)
    private val datetime1969dec31_h23m59s59ms999 = DateTime(Date(31, Month.DECEMBER, 1969), Time(23, 59, 59, 999))

    private val instant1969dec23_h23m59s59ms1 =
        Instant(-Duration.DAY.mul(8).add(Duration.MS.mul(999)).duration) // -691200999
    private val datetime1969dec23_h23m59s59ms1 = DateTime(Date(23, Month.DECEMBER, 1969), Time(23, 59, 59, 1))

    private val instant1752sep14_h01m02s03ms456 = Instant(-6_857_218_676_544)
    private val datetime1752sep14_h01m02s03ms456 = DateTime(Date(14, Month.SEPTEMBER, 1752), Time(1, 2, 3, 456))

    @Test
    fun toDateTime1969dec31h00m00s00() {
        val actual = toDateTime(instant1969dec31_h00m00s00)
        assertEquals(datetime1969dec31_h00m00s00, actual)
    }

    @Test
    fun toInstant1969dec31h00m00s00() {
        val actual = toInstant(datetime1969dec31_h00m00s00)
        assertEquals(instant1969dec31_h00m00s00, actual)
    }

    @Test
    fun toDateTime1969dec31_h23m59s59ms999() {
        val actual = toDateTime(instant1969dec31_h23m59s59ms999)
        assertEquals(datetime1969dec31_h23m59s59ms999, actual)
    }

    @Test
    fun toInstant1969dec31_h23m59s59ms999() {
        val actual = toInstant(datetime1969dec31_h23m59s59ms999)
        assertEquals(instant1969dec31_h23m59s59ms999, actual)
    }

    @Test
    fun toDateTime1969dec23_h23m59s59ms1() {
        val actual = toDateTime(instant1969dec23_h23m59s59ms1)
        assertEquals(datetime1969dec23_h23m59s59ms1, actual)
    }

    @Test
    fun toInstant1969dec23_h23m59s59ms1() {
        val actual = toInstant(datetime1969dec23_h23m59s59ms1)
        assertEquals(instant1969dec23_h23m59s59ms1, actual)
    }

    @Test
    fun toDateTime1752sep14_h01m02s03ms456() {
        val actual = toDateTime(instant1752sep14_h01m02s03ms456)
        assertEquals(datetime1752sep14_h01m02s03ms456, actual)
    }

    @Test
    fun toInstant1752sep14_h01m02s03ms456() {
        val actual = toInstant(datetime1752sep14_h01m02s03ms456)
        assertEquals(instant1752sep14_h01m02s03ms456, actual)
    }

    private fun toDateTime(epochMillis: Instant): DateTime {
        return epochMillis.toDateTime(TimeZone.UTC)
    }

    private fun toInstant(dateTime: DateTime): Instant {
        return dateTime.toInstant(TimeZone.UTC)
    }
}