/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.math.pow

class ForsythePolynomialGenerator(private val knots: DoubleArray) {
    private val ps: ArrayList<PolynomialFunction>

    init {
        require(knots.isNotEmpty()) { "The knots list must not be empty" }

        ps = arrayListOf(
            PolynomialFunction(doubleArrayOf(1.0)),
            PolynomialFunction(doubleArrayOf(-knots.average(), 1.0))
        )
    }

    private fun alphaBeta(i: Int): Pair<Double, Double> {
        require(i == ps.size) { "Alpha must be calculated sequentially." }

        val p = ps.last()
        val pp = ps[ps.size - 2]
        var sxp = 0.0
        var sp2 = 0.0
        var spp2 = 0.0

        for (x in knots) {
            val pv2 = p.value(x).pow(2)
            val ppv2 = pp.value(x).pow(2)
            sxp += x * pv2
            sp2 += pv2
            spp2 += ppv2
        }

        return Pair(sxp / sp2, sp2 / spp2)
    }

    fun getPolynomial(n: Int): PolynomialFunction {

        require(n >= 0) { "Degree of Forsythe polynomial must not be negative" }

        require(n < knots.size) { "Degree of Forsythe polynomial must not exceed knots.size - 1" }

        if (n >= ps.size) {
            val sz = ps.size

            for (k in sz..n + 1) {
                val (a, b) = alphaBeta(k)
                val pPrev = ps.last()
                val pPrevPrev = ps[ps.size - 2]
                val p = X * pPrev - a * pPrev - b * pPrevPrev
                ps.add(p)
            }
        }

        return ps[n]
    }

    companion object {
        val X = PolynomialFunction(doubleArrayOf(0.0, 1.0))
    }
}

