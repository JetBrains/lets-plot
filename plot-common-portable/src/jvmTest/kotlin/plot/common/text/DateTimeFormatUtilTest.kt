/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.text

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeFormatUtilTest {

    @Test
    fun month() {
        //    String s = FormatUtil.formatDateUTC(baseDate, "yyyy.MM.dd G 'at' HH:mm:ss vvvv");
        //    System.out.println(s);
        //    String s = FormatUtil.formatDateUTC(baseDate, "yyyyy.MMMMM.dd GGG hh:mm aaa");
        //    System.out.println(s);

        val m = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "M"
        )
        assertEquals("8", m)
        val mm = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "MM"
        )
        assertEquals("08", mm)
        val mmm = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "MMM"
        )
        assertEquals("Aug", mmm)
        val mmmm = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "MMMM"
        )
        assertEquals("August", mmmm)
        val mmmmm = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "MMMMM"
        )
        assertEquals("August", mmmmm)   // ?
        val mmmmmm = DateTimeFormatUtil.formatDateUTC(
            baseInstant,
            "MMMMMM"
        )
        assertEquals("August", mmmmmm)   // ?
    }

    companion object {
        private val baseDate = Date(Date.UTC(
                2013 - 1900, // 2013
                7, // Aug
                7,
                7,
                7,
                7
        ))
        private val baseInstant = baseDate.time
    }
}
