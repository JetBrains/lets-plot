/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import kotlin.test.Test

class TimeUnitTest {
    @Test
    fun simple() {
        val dt = DateTime(
            Date(day = 8, month = Month.MARCH, year = 2010),
            Time(hours = 12, minutes = 34, seconds = 56, milliseconds = 789)
        )

        assertThat(Util.applyTimeUnit(dt, "year"))
            .isEqualTo(
                DateTime(
                    Date(day = 1, month = Month.JANUARY, year = 2010),
                    Time(hours = 0, minutes = 0, seconds = 0)
                )
            )
    }

    @Test
    fun `year and month`() {
        val dt = DateTime(
            Date(day = 8, month = Month.MARCH, year = 2010),
            Time(hours = 12, minutes = 34, seconds = 56, milliseconds = 789)
        )

        assertThat(Util.applyTimeUnit(dt, "yearmonth"))
            .isEqualTo(
                DateTime(
                    Date(day = 1, month = Month.MARCH, year = 2010),
                    Time(hours = 0, minutes = 0, seconds = 0)
                )
            )
    }

}