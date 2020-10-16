/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.math.ipow
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.stat.CorrelationUtil.correlation
import jetbrains.datalore.plot.base.stat.CorrelationUtil.correlationMatrix
import jetbrains.datalore.plot.base.stat.math3.correlationPearson
import kotlin.math.abs
import kotlin.test.*

class CorrelationTest {
    val N = 10000
    val EPS = 0.00001

    @Test
    fun argTest1() {
        val xs = doubleArrayOf(0.0)
        val ys = doubleArrayOf()
        val ex = assertFailsWith<IllegalArgumentException> { correlationPearson(xs, ys) }
        assertEquals("Two series must have the same size.", ex.message)
    }

    @Test
    fun argTest2() {
        val xs = doubleArrayOf()
        val ys = doubleArrayOf(0.0)
        val ex = assertFailsWith<IllegalArgumentException> { correlationPearson(xs, ys) }
        assertEquals("Two series must have the same size.", ex.message)
    }

    @Test
    fun zeroVarTest1() {
        val xs = doubleArrayOf(10.0, 10.0)
        val ys = doubleArrayOf(0.0, 0.0)

        val ex = assertFailsWith<IllegalArgumentException> { correlationPearson(xs, ys) }
        assertEquals("Correlation is not defined for sequences with zero variation.", ex.message)
    }

    @Test
    fun corrOneTest1() {
        val xs = DoubleArray(N) { 0.001 * it.toDouble() }
        val ys = DoubleArray(N) { 15.0 + 0.1 * it.toDouble() }
        val c = correlationPearson(xs, ys)
        assertTrue(abs(1.0 - c) < EPS)
    }

    @Test
    fun corrOneTest2() {
        val xs = DoubleArray(N) { 0.013 * it.toDouble() }
        val ys = DoubleArray(N) { 2.0 - 0.2 * it.toDouble() }
        val c = correlationPearson(xs, ys)
        assertTrue(abs(-1.0 - c) < EPS)
    }

    @Test
    fun corrZeroTest1() {
        val xs = doubleArrayOf(1.0, 1.0, 1.0, 0.0)
        val ys = doubleArrayOf(0.0, 1.0, 2.0, 1.0)
        val c = correlationPearson(xs, ys)
        assertTrue(abs(c) < EPS)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun corrMatrixTest1() {
        val a: ArrayList<Double> = arrayListOf(1.0, 2.0, 3.0)
        val b: ArrayList<Double> = arrayListOf(1.0, 1.0, 2.0)
        val c: ArrayList<Double> = arrayListOf(1.0, -1.0, 1.0)

        val data = DataFrame.Builder()
            .putNumeric(DataFrame.Variable("A"), a)
            .putNumeric(DataFrame.Variable("B"), b)
            .putNumeric(DataFrame.Variable("C"), c)
            .build()

        val labels2series = mapOf("A" to a, "B" to b, "C" to c)

        val cm = correlationMatrix(data, CorrelationStat.Type.FULL, fillDiagonal = true, corrfn = ::correlationPearson)

        assertEquals(cm.rowCount(), a.size.ipow(2).toInt())

        val v1: List<String> = cm[Stats.X] as List<String>
        val v2: List<String> = cm[Stats.Y] as List<String>
        val cr: List<Double> = cm.getNumeric(Stats.CORR) as List<Double>

        for ((v12, corr) in v1.zip(v2).zip(cr)) {
            val s1 = labels2series[v12.first]
            val s2 = labels2series[v12.second]
            assertNotNull(s1)
            assertNotNull(s2)
            assertEquals(corr, correlation(s1, s2, ::correlationPearson))
        }
    }
}