/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.WeekDay
import kotlin.test.Test
import kotlin.test.assertEquals

class DateSpecsTest {
    @Test
    fun euDaylight() {
        val startSpec = DateSpecs.last(WeekDay.SUNDAY, Month.MARCH)
        assertEquals("RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3", startSpec.rRule)
        assertEquals(Date.parse("19700329"), startSpec.getDate(Date.EPOCH.year))
    }

    @Test
    fun euStandard() {
        val endSpec = DateSpecs.last(WeekDay.SUNDAY, Month.OCTOBER)
        assertEquals("RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10", endSpec.rRule)
        assertEquals(Date.parse("19701025"), endSpec.getDate(Date.EPOCH.year))
    }

    @Test
    fun usDaylight() {
        val startSpec = DateSpecs.first(WeekDay.SUNDAY, Month.MARCH, 2)
        assertEquals("RRULE:FREQ=YEARLY;BYDAY=2SU;BYMONTH=3", startSpec.rRule)
        assertEquals(Date.parse("19700308"), startSpec.getDate(Date.EPOCH.year))
    }

    @Test
    fun usStandard() {
        val endSpec = DateSpecs.first(WeekDay.SUNDAY, Month.NOVEMBER)
        assertEquals("RRULE:FREQ=YEARLY;BYDAY=1SU;BYMONTH=11", endSpec.rRule)
        assertEquals(Date.parse("19701101"), endSpec.getDate(Date.EPOCH.year))
    }
}
