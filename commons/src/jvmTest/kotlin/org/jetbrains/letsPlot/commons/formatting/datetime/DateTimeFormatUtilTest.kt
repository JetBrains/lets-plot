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

        //    String s = FormatUtil.formatDateUTC(baseDate, "yyyy.MM.dd G 'at' HH:mm:ss vvvv");
        //    System.out.println(s);
        //    String s = FormatUtil.formatDateUTC(baseDate, "yyyyy.MMMMM.dd GGG hh:mm aaa");
        //    System.out.println(s);

        val m = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "%m"
        )
        assertEquals("08", m)
        val b = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "%b"
        )
        assertEquals("Aug", b)
        val B = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "%B"
        )
        assertEquals("August", B)
    }

    companion object {
//        @Suppress("DEPRECATION")
//        private val baseDate = Date(
//            Date.UTC(
//                2013 - 1900, // 2013
//                7, // Aug
//                7,
//                7,
//                7,
//                7
//            )
//        )
//        private val baseInstant = baseDate.time
    }
}
