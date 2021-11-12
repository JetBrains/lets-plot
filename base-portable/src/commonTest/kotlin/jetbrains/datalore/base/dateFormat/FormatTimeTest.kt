/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.dateFormat

import jetbrains.datalore.base.datetime.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTimeTest {
    private val time = Time(4, 46, 35)

    @Test
    fun onlyTime() {
        val f = DateTimeFormat("%Y-%m-%dT%H:%M:%S")
        assertEquals("--T04:46:35", f.apply(time))
    }

    @Test
    fun time_12HourPeriods() {
        // Format                  24-hour   12-hour
        // Midnight (start of day)  00:00   12:00 am
        // Noon                     12:00   12:00 pm
        // Midnight (end of day)    24:00   12:00 am

        val dayStart = Time(0, 0)
        val noon = Time(12, 0)
        val dayEnd = Time(24, 0)

        val f12 = DateTimeFormat("%I:%M %P")
        assertEquals("12:00 am", f12.apply(dayStart))
        assertEquals("12:00 pm", f12.apply(noon))
        assertEquals("12:00 am", f12.apply(dayEnd))

        val f24 = DateTimeFormat("%H:%M")
        assertEquals("00:00", f24.apply(dayStart))
        assertEquals("12:00", f24.apply(noon))
        assertEquals("24:00", f24.apply(dayEnd))
    }
}