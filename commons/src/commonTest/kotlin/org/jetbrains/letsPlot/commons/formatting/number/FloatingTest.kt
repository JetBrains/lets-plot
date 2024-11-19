/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class FloatingTest {
    @Test
    fun fromNumber() {
        assertEquals(Floating(0, "0", 0, ""), Floating.fromNumber(0))
        assertEquals(Floating(1, "234", 0, ""), Floating.fromNumber(1.234))
        assertEquals(Floating(1, "234", -1, ""), Floating.fromNumber(0.1234))
        assertEquals(Floating(1, "234", 1, ""), Floating.fromNumber(12.34))
        assertEquals(Floating(1, "234", 11, ""), Floating.fromNumber(1.234E11))
        assertEquals(Floating(1, "234", -11, ""), Floating.fromNumber(1.234E-11))

    }

    @Test
    fun zero() {
        val f = Floating(0, "0", 0)
        assertEquals(Floating(0, "0", 0), f.round(0))
    }

    @Test
    fun round_0_9() {
        val f = Floating(0, "9", 0)
        assertEquals(Floating(1, "0", 0), f.round(0))
    }

    @Test
    fun round_9_9() {
        val f = Floating(9, "9", 0)
        assertEquals(Floating(1, "0", 1), f.round(0))
    }

    @Test
    fun simple() {
        val f = Floating(1, "234", 2)
        assertEquals(Floating(1, "23", 2), f.round(2))
    }
}