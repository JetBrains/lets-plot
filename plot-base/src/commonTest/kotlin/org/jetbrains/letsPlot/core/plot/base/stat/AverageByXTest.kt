/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.stat.regression.averageByX
import kotlin.test.Test
import kotlin.test.assertTrue

class AverageByXTest {
    @Test
    fun Test0() {
        val xs: List<Double?> = arrayListOf()
        val ys: List<Double?> = arrayListOf()

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf())
        assertTrue(p.second contentEquals doubleArrayOf())
    }

    @Test
    fun Test1() {
        val xs: List<Double?> = arrayListOf(1.0)
        val ys: List<Double?> = arrayListOf(1.0)

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf(1.0))
        assertTrue(p.second contentEquals doubleArrayOf(1.0))
    }

    @Test
    fun Test2() {
        val xs: List<Double?> = arrayListOf(2.0, 1.0)
        val ys: List<Double?> = arrayListOf(3.0, 1.0)

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf(1.0, 2.0))
        assertTrue(p.second contentEquals doubleArrayOf(1.0, 3.0))
    }

    @Test
    fun Test3() {
        val xs: List<Double?> = arrayListOf(2.0, 1.0, null)
        val ys: List<Double?> = arrayListOf(3.0, 1.0, 2.0)

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf(1.0, 2.0))
        assertTrue(p.second contentEquals doubleArrayOf(1.0, 3.0))
    }

    @Test
    fun Test4() {
        val xs: List<Double?> = arrayListOf(2.0, 1.0, 0.0)
        val ys: List<Double?> = arrayListOf(3.0, 1.0, null)

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf(1.0, 2.0))
        assertTrue(p.second contentEquals doubleArrayOf(1.0, 3.0))
    }

    @Test
    fun Test5() {
        val xs: List<Double?> = arrayListOf(2.0, 1.0, 0.0, 1.0)
        val ys: List<Double?> = arrayListOf(3.0, 1.0, null, 3.0)

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf(1.0, 2.0))
        assertTrue(p.second contentEquals doubleArrayOf(2.0, 3.0))
    }

    @Test
    fun Test6() {
        val xs: List<Double?> = arrayListOf(null, null, null, 1.0, 3.0, 3.0, 2.0, 2.0)
        val ys: List<Double?> = arrayListOf(100.0, 13.0, 1.0, 10.0, 1.0, 3.0, null, 20.0)

        val p = averageByX(xs, ys)

        assertTrue(p.first contentEquals doubleArrayOf(1.0, 2.0, 3.0))
        assertTrue(p.second contentEquals doubleArrayOf(10.0, 20.0, 2.0))
    }
}