/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone.Companion.UTC
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeFormatUtilTest {

    @Test
    fun month() {
        val baseInstant = DateTime(
            Date(7, Month.AUGUST, 2013),
            Time(7, 7, 7)
        ).toInstant(UTC).toEpochMilliseconds()

        val m = format(
            baseInstant,
            "%m"
        )
        assertEquals("08", m)
        val b = format(
            baseInstant,
            "%b"
        )
        assertEquals("Aug", b)
        val B = format(
            baseInstant,
            "%B"
        )
        assertEquals("August", B)
    }

    companion object {
        private val TZ = UTC

        private fun format(epochMillis: Long, pattern: String): String {
            return DateTimeFormatUtil.format(epochMillis, pattern, TZ)
        }
    }
}
