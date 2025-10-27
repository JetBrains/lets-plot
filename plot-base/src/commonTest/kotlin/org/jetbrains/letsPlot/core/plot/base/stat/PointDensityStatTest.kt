/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PointDensityStatTest : BaseStatTest() {
    @Test
    fun testCountNeighborsBasic() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(1.0, 1.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsWeighted() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            weights = listOf(2.0, 1.0, 1.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(1.0, 2.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsEmptyLists() {
        countNeighbors(
            xs = listOf(),
            ys = listOf(),
            radius = 1.0
        ).let { counts ->
            assertTrue(counts.isEmpty())
        }
    }

    @Test
    fun testCountNeighborsOneElement() {
        countNeighbors(
            xs = listOf(4.0),
            ys = listOf(2.0),
            radius = 1.0
        ).let { counts ->
            assertEquals(listOf(0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsWithDuplicates() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 2.0, 0.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(2.0, 2.0, 2.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsWithOnlyDuplicates() {
        val size = 10
        countNeighbors(
            xs = List(size) { 4.0 },
            ys = List(size) { 2.0 },
            radius = 1.0
        ).let { counts ->
            assertEquals(List(size) { size - 1.0 }, counts)
        }
    }

    @Test
    fun testCountNeighborsFlatAlongOneDimension() {
        // Flat along X axis
        countNeighbors(
            xs = listOf(0.0, 0.0, 0.0),
            ys = listOf(0.0, 2.0, 6.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(1.0, 1.0, 0.0), counts)
        }
        // Flat along Y axis
        countNeighbors(
            xs = listOf(0.0, 2.0, 6.0),
            ys = listOf(0.0, 0.0, 0.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(1.0, 1.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsNegativeWeight() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            weights = listOf(-1.0, 1.0, 1.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(1.0, -1.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsZeroWeight() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            weights = listOf(0.0, 1.0, 1.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(1.0, 0.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsBigRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 5.0
        ).let { counts ->
            assertEquals(listOf(2.0, 2.0, 2.0), counts)
        }
    }

    @Test
    fun testCountNeighborsSmallRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 1.0
        ).let { counts ->
            assertEquals(listOf(0.0, 0.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsZeroRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 0.0
        ).let { counts ->
            assertEquals(listOf(0.0, 0.0, 0.0), counts)
        }
    }

    @Test
    fun testCountNeighborsDistanceEqualToRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 4.0
        ).let { counts ->
            assertEquals(listOf(2.0, 1.0, 1.0), counts)
        }
    }

    @Test
    fun testCountNeighborsCheckXY() {
        val checks = listOf(
            4.0 - EPSILON to listOf(0.0, 0.0, 0.0),
            4.0 to listOf(2.0, 1.0, 1.0),
            sqrt(32.0) - EPSILON to listOf(2.0, 1.0, 1.0),
            sqrt(32.0) to listOf(2.0, 2.0, 2.0),
        )
        for ((radius, expectedCounts) in checks) {
            countNeighbors(
                xs = listOf(0.0, 0.0, 4.0),
                ys = listOf(0.0, 2.0, 0.0),
                radius = radius,
                xy = 2.0
            ).let { counts ->
                assertEquals(expectedCounts, counts)
            }
        }
    }

    companion object {
        private const val EPSILON = 1e-12

        private fun countNeighbors(
            xs: List<Double>,
            ys: List<Double>,
            radius: Double,
            weights: List<Double>? = null,
            xy: Double = 1.0
        ): List<Double> {
            return PointDensityStat.countNeighbors(xs, ys, weights ?: List(xs.size) { 1.0 }, radius * radius / xy, xy)
        }
    }
}