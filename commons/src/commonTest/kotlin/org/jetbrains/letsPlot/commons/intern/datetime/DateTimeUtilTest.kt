/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlin.test.*

class DateTimeUtilTest {
    @Test
    fun simpleLeapYears() {
        assertTrue(DateTimeUtil.isLeap(2004))
        assertFalse(DateTimeUtil.isLeap(2005))
    }

    @Test
    fun centuryLeapYears() {
        assertTrue(DateTimeUtil.isLeap(2000))
        assertFalse(DateTimeUtil.isLeap(2100))
    }

    @Test
    fun leapYearsBetween() {
        assertEquals(1, DateTimeUtil.leapYearsBetween(2000, 2004))
    }

    @Test
    fun incorrectDate() {
        assertFailsWith<IllegalArgumentException> {
            Date(35, Month.SEPTEMBER, 2000)
        }
    }
}
