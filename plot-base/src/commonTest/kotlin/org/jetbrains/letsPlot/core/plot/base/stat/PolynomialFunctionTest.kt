/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.PolynomialFunction
import org.jetbrains.letsPlot.core.plot.base.stat.math3.times
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PolynomialFunctionTest {
    @Test
    fun degreeTest0() {
        val p = PolynomialFunction(doubleArrayOf(0.0))
        assertTrue(p.degree() == 0)
    }

    @Test
    fun degreeTest1() {
        val p = PolynomialFunction(doubleArrayOf(1.0))
        assertTrue(p.degree() == 0)
    }

    @Test
    fun degreeTest2() {
        val p = PolynomialFunction(doubleArrayOf(1.0, 0.0))
        assertTrue(p.degree() == 0)
    }

    @Test
    fun degreeTest3() {
        val p = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        assertTrue(p.degree() == 1)
    }

    @Test
    fun cmpTest1() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        assertEquals(p1, p2)
    }

    @Test
    fun cmpTest2() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        assertFalse(p1 < p2)
    }

    @Test
    fun cmpTest3() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        assertFalse(p1 > p2)
    }

    @Test
    fun cmpTest4() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        assertTrue(p1 < p2)
    }

    @Test
    fun cmpTest5() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 2.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        assertTrue(p1 > p2)
    }

    @Test
    fun cmpTest6() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(-1.0, -1.0))
        assertEquals(-p1, p2)
    }

    @Test
    fun cmpTest7() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(-1.0, -1.0))
        assertEquals(p1, -p2)
    }

    @Test
    fun multTest() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(-1.0, -1.0))
        assertEquals(-1.0 * p1, p2)
    }

    @Test
    fun plusTest1() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p3 = PolynomialFunction(doubleArrayOf(2.0, 2.0))
        assertEquals(p1 + p2, p3)
    }

    @Test
    fun plusTest2() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(0.0, 0.0, 1.0))
        val p3 = PolynomialFunction(doubleArrayOf(1.0, 1.0, 1.0))
        assertEquals(p1 + p2, p3)
    }

    @Test
    fun multTest1() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p2 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p3 = PolynomialFunction(doubleArrayOf(1.0, 2.0, 1.0))
        assertEquals(p1 * p2, p3)
    }

    @Test
    fun multTest2() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p3 = PolynomialFunction(doubleArrayOf(1.0, 2.0, 1.0))
        assertEquals(p1 * p1 * p3, p3 * p3)
        assertEquals(p1.degree() + p1.degree() + p3.degree(), p3.degree() + p3.degree())
    }

    @Test
    fun multTest3() {
        val p1 = PolynomialFunction(doubleArrayOf(1.0, 1.0))
        val p3 = PolynomialFunction(doubleArrayOf(1.0, 2.0, 1.0))
        assertEquals(p1.degree() + p1.degree() + p3.degree(), p3.degree() + p3.degree())
    }
}