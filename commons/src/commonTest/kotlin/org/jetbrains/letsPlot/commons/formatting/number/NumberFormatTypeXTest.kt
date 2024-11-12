/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeXTest {
    @Test
    fun hexLowercaseString() {
        assertEquals("deadbeef", NumberFormat("x").apply(0xdeadbeef))
    }

    @Test
    fun hexLowercaseStringWithPrefix() {
        assertEquals("0xdeadbeef", NumberFormat("#x").apply(0xdeadbeef))
    }

    @Test
    fun groupsThousands() {
        assertEquals("de,adb,eef", NumberFormat(",x").apply(0xdeadbeef))
    }

    @Test
    fun doesNotGroupPrefix() {
        assertEquals("0xade,adb,eef", NumberFormat("#,x").apply(0xadeadbeef))
    }

    @Test
    fun putsSignBeforePrefix() {
        assertEquals("+0xdeadbeef", NumberFormat("+#x").apply(0xdeadbeef))
        assertEquals("-0xdeadbeef", NumberFormat("+#x").apply(-0xdeadbeef))
        assertEquals(" 0xdeadbeef", NumberFormat(" #x").apply(0xdeadbeef))
        assertEquals("-0xdeadbeef", NumberFormat(" #x").apply(-0xdeadbeef))
    }

    @Test
    fun currency() {
        assertEquals("\$de,adb,eef", NumberFormat("$,x").apply(0xdeadbeef))
    }

    @Test
    fun alwaysHasPrecisionZero() {
        assertEquals("deadbeef", NumberFormat(".2x").apply(0xdeadbeef))
        assertEquals("-4", NumberFormat(".2x").apply(-4.2))
    }

    @Test
    fun roundsNonIntegers() {
        assertEquals("2", NumberFormat("x").apply(2.4))
    }

    @Test
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0", NumberFormat("x").apply(-0))
        assertEquals("0", NumberFormat("x").apply(-1e-12))
    }

    @Test
    fun hexUppercaseString() {
        assertEquals("DEADBEEF", NumberFormat("X").apply(0xdeadbeef))
    }

    @Test
    fun hexUppercaseStringWithPrefix() {
        assertEquals("0xDEADBEEF", NumberFormat("#X").apply(0xdeadbeef))
    }

    @Test
    fun prefix() {
        assertEquals("            deadbeef", NumberFormat("20x").apply(0xdeadbeef))
        assertEquals("          0xdeadbeef", NumberFormat("#20x").apply(0xdeadbeef))
        assertEquals("000000000000deadbeef", NumberFormat("020x").apply(0xdeadbeef))
        assertEquals("0x0000000000deadbeef", NumberFormat("#020x").apply(0xdeadbeef))
    }

    @Test
    fun overflowTes() {
        //assertEquals("INFINITY", NumberFormat("x").apply(1.23e200))
    }
}