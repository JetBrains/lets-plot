/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeTest {

    @Test
    fun parsing() {
        assertParsed(DateTime(Date(23, Month.SEPTEMBER, 1978), Time(23, 2)))
    }

    private fun assertParsed(dateTime: DateTime) {
        assertEquals(dateTime, DateTime.parse(dateTime.toString()))
    }
}
