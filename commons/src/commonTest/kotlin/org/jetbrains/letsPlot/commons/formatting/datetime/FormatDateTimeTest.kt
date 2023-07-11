/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatDateTimeTest {
    private val date = Date(6, Month.AUGUST, 2019)
    private val time = Time(4, 46, 35)
    private val dateTime = DateTime(date, time)

    @Test
    fun datePatterns() {
        assertEquals("Tue", DateTimeFormat("%a").apply(dateTime))
        assertEquals("Tuesday", DateTimeFormat("%A").apply(dateTime))
        assertEquals("Aug", DateTimeFormat("%b").apply(dateTime))
        assertEquals("August", DateTimeFormat("%B").apply(dateTime))
        assertEquals("06", DateTimeFormat("%d").apply(dateTime))
        assertEquals("6", DateTimeFormat("%e").apply(dateTime))
        assertEquals("218", DateTimeFormat("%j").apply(dateTime))
        assertEquals("08", DateTimeFormat("%m").apply(dateTime))
        assertEquals("2", DateTimeFormat("%w").apply(dateTime))
        assertEquals("19", DateTimeFormat("%y").apply(dateTime))
        assertEquals("2019", DateTimeFormat("%Y").apply(dateTime))
    }

    @Test
    fun timePatterns() {
        assertEquals("04", DateTimeFormat("%H").apply(dateTime))
        assertEquals("04", DateTimeFormat("%I").apply(dateTime))
        assertEquals("4", DateTimeFormat("%l").apply(dateTime))
        assertEquals("46", DateTimeFormat("%M").apply(dateTime))
        assertEquals("am", DateTimeFormat("%P").apply(dateTime))
        assertEquals("AM", DateTimeFormat("%p").apply(dateTime))
        assertEquals("35", DateTimeFormat("%S").apply(dateTime))
    }

    @Test
    fun leadingZeros() {
        val date = Date(6, Month.JANUARY, 2019)
        val time = Time(4, 3, 2)
        val dateTime = DateTime(date, time)
        assertEquals("04", DateTimeFormat("%H").apply(dateTime))
        assertEquals("04", DateTimeFormat("%I").apply(dateTime))
        assertEquals("4", DateTimeFormat("%l").apply(dateTime))
        assertEquals("03", DateTimeFormat("%M").apply(dateTime))
        assertEquals("02", DateTimeFormat("%S").apply(dateTime))

        assertEquals("06", DateTimeFormat("%d").apply(dateTime))
        assertEquals("6", DateTimeFormat("%e").apply(dateTime))
        assertEquals("006", DateTimeFormat("%j").apply(dateTime))
        assertEquals("01", DateTimeFormat("%m").apply(dateTime))
    }

    @Test
    fun isoFormat() {
        val f = DateTimeFormat("%Y-%m-%dT%H:%M:%S")
        assertEquals("2019-08-06T04:46:35", f.apply(dateTime))
    }

    @Test
    fun randomFormat() {
        val f = DateTimeFormat("----!%%%YY%md%dT%H:%M:%S%%%")
        assertEquals("----!%%2019Y08d06T04:46:35%%%", f.apply(dateTime))
    }
}