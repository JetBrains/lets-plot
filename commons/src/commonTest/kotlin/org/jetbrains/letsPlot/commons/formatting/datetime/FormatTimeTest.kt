/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Time
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

        val f12 = DateTimeFormat("%I:%M %P")
        val f24 = DateTimeFormat("%H:%M")

        // midnight (start of day)
        Time(0, 0).run {
            assertEquals("12:00 am", f12.apply(this))
            assertEquals("00:00", f24.apply(this))
        }

        // noon
        Time(12, 0).run {
            assertEquals("12:00 pm", f12.apply(this))
            assertEquals("12:00", f24.apply(this))
        }

        // midnight (end of day)
        Time(24, 0).run {
            assertEquals("12:00 am", f12.apply(this))
            assertEquals("24:00", f24.apply(this))
        }

        // minute after midnight
        Time(0, 1).run {
            assertEquals("12:01 am", f12.apply(this))
            assertEquals("00:01", f24.apply(this))
        }

        // minute after noon
        Time(12, 1).run {
            assertEquals("12:01 pm", f12.apply(this))
            assertEquals("12:01", f24.apply(this))
        }

        // hour after midnight
        Time(1, 0).run {
            assertEquals("01:00 am", f12.apply(this))
            assertEquals("01:00", f24.apply(this))
        }

        // hour after noon
        Time(13, 0).run {
            assertEquals("01:00 pm", f12.apply(this))
            assertEquals("13:00", f24.apply(this))
        }
    }
}