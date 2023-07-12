/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class QuantizeScaleTest {

    @Test
    fun undefinedDomain() {
        assertFailsWith(IllegalStateException::class) {
            val scale = QuantizeScale<String>()
                    .range(listOf("A", "B"))
            scale.quantize(1.0)
        }
    }

    @Test
    fun invalidDomain() {
        assertFailsWith(IllegalArgumentException::class) {
            val scale = QuantizeScale<String>()
                    .domain(10.0, 9.0)
                    .range(listOf("A", "B"))
            scale.quantize(1.0)
        }
    }

    @Test
    fun undefinedRange() {
        assertFailsWith(IllegalStateException::class) {
            val scale = QuantizeScale<String>()
                    .domain(10.0, 100.0)
            scale.quantize(1.0)
        }
    }

    @Test
    fun zeroLengthDomain() {
        val scale = QuantizeScale<String>()
                .range(listOf("A", "B", "C"))
                .domain(1.0, 1.0)
        assertEquals("A", scale.quantize(-1.0))
        assertEquals("A", scale.quantize(0.0))
        assertEquals("A", scale.quantize(1.0))
        assertEquals("C", scale.quantize(1.001))
        assertEquals("C", scale.quantize(2.0))
    }

    @Test
    fun rounding() {
        val scale = QuantizeScale<Int>()
                .range(listOf(0, 1))
                .domain(0.0, 1.0)

        assertEquals(0, (scale.quantize(-1.0)))
        assertEquals(0, (scale.quantize(0.0)))
        assertEquals(0, (scale.quantize(0.1)))
        assertEquals(0, (scale.quantize(0.49)))
        assertEquals(1, (scale.quantize(0.5)))
        assertEquals(1, (scale.quantize(0.51)))
        assertEquals(1, (scale.quantize(0.99)))
        assertEquals(1, (scale.quantize(1.0)))
        assertEquals(1, scale.quantize(10.0))
    }

    @Test
    fun onDomainEndsAndOutside() {
        val scale = QuantizeScale<String>()
                .range(listOf("0", "1", "2", "3"))
                .domain(0.0, 1.0)

        assertEquals("0", scale.getOutputValue(-0.01))
        assertEquals(0, scale.getOutputValueIndex(-0.01))
        assertEquals("0", scale.getOutputValue(0.0))
        assertEquals(0, scale.getOutputValueIndex(0.0))
        assertEquals("3", scale.getOutputValue(1.0))
        assertEquals(3, scale.getOutputValueIndex(1.0))
        assertEquals("3", scale.getOutputValue(1.01))
        assertEquals(3, scale.getOutputValueIndex(1.01))
    }
}
