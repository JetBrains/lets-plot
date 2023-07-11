/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimeTest {
    @Test
    fun parsing() {
        assertParsed(Time(23, 12))
        assertParsed(Time(0, 12))
    }

    @Test
    fun deserialize() {
        assertEquals(Time(1, 25), Time.fromPrettyHMString("01:25"))
        assertEquals(Time(1, 25), Time.fromPrettyHMString("1:25"))
    }

    @Test
    fun invalid3Symbols() {
        assertFailsWith<IllegalArgumentException> {
            Time.fromPrettyHMString("123")
        }
    }

    @Test
    fun noDelimiter() {
        assertFailsWith<IllegalArgumentException> {
            Time.fromPrettyHMString("1234")
        }
    }

    @Test
    fun invalid5Symbols() {
        assertFailsWith<IllegalArgumentException> {
            Time.fromPrettyHMString("24:01")
        }
    }

    private fun assertParsed(t: Time) {
        if (t.milliseconds != 0) {
            throw IllegalArgumentException()
        }
        assertEquals(t, Time.parse(t.toString()))
    }
}
