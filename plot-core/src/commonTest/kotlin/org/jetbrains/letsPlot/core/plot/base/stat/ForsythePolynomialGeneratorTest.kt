/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.interact.HitShape
import org.jetbrains.letsPlot.core.plot.base.stat.math3.ForsythePolynomialGenerator
import org.jetbrains.letsPlot.core.plot.base.stat.math3.PolynomialFunction
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class ForsythePolynomialGeneratorTest {
    val deg = 11
    val knots = DoubleArray(100 * deg + 1) { 0.001 * it.toDouble() }
    val fpg = ForsythePolynomialGenerator(knots)
    val ps = Array(deg + 1) { fpg.getPolynomial(it) }

    @Test
    fun orthogonalityTest() {
        for (i in 0..deg) {
            val pi = ps[i]
            for (j in 0 until i) {
                val pj = ps[j]
                val res = dot(pi, pj)

                assertTrue(
                    abs(res) < 1e-12,
                    "Orthogonality failed: res = ${res}, Pi = ${pi.toString()}, pj = ${pj.toString()}"
                )
            }
        }
    }

    private fun dot(p1: PolynomialFunction, p2: PolynomialFunction) =
        knots.sumOf { p1.value(it) * p2.value(it) }
}