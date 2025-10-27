/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.stat.math3.BlockRealMatrix
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PointDensityStatTest : BaseStatTest() {

    // PointDensityStat::countNeighbors()

    @Test
    fun testCountNeighborsBasic() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(2.0, 2.0, 1.0), counts)
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
            assertEquals(listOf(3.0, 3.0, 1.0), counts)
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
            assertEquals(listOf(1.0), counts)
        }
    }

    @Test
    fun testCountNeighborsWithDuplicates() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 2.0, 0.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(3.0, 3.0, 3.0, 1.0), counts)
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
            assertEquals(List(size) { size.toDouble() }, counts)
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
            assertEquals(listOf(2.0, 2.0, 1.0), counts)
        }
        // Flat along Y axis
        countNeighbors(
            xs = listOf(0.0, 2.0, 6.0),
            ys = listOf(0.0, 0.0, 0.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(2.0, 2.0, 1.0), counts)
        }
    }

    @Test
    fun testCountNeighborsNegativeWeight() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            weights = listOf(-2.0, 1.0, 1.0),
            radius = 3.0
        ).let { counts ->
            assertEquals(listOf(-1.0, -1.0, 1.0), counts)
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
            assertEquals(listOf(1.0, 1.0, 1.0), counts)
        }
    }

    @Test
    fun testCountNeighborsBigRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 5.0
        ).let { counts ->
            assertEquals(listOf(3.0, 3.0, 3.0), counts)
        }
    }

    @Test
    fun testCountNeighborsSmallRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 1.0
        ).let { counts ->
            assertEquals(listOf(1.0, 1.0, 1.0), counts)
        }
    }

    @Test
    fun testCountNeighborsZeroRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 0.0
        ).let { counts ->
            assertEquals(listOf(1.0, 1.0, 1.0), counts)
        }
    }

    @Test
    fun testCountNeighborsDistanceEqualToRadius() {
        countNeighbors(
            xs = listOf(0.0, 0.0, 4.0),
            ys = listOf(0.0, 2.0, 0.0),
            radius = 4.0
        ).let { counts ->
            assertEquals(listOf(3.0, 2.0, 2.0), counts)
        }
    }

    @Test
    fun testCountNeighborsCheckXY() {
        val checks = listOf(
            4.0 - EPSILON to listOf(1.0, 1.0, 1.0),
            4.0 to listOf(3.0, 2.0, 2.0),
            sqrt(32.0) - EPSILON to listOf(3.0, 2.0, 2.0),
            sqrt(32.0) to listOf(3.0, 3.0, 3.0),
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

    // PointDensityStat::approxCount()

    @Test
    fun testApproxCountBasic() {
        getTestGrid().let { (stepsX, stepsY, densityMatrix) ->
            PointDensityStat.approxCount(
                x = 3.0, y = 1.5,
                stepsX = stepsX, stepsY = stepsY,
                densityMatrix = densityMatrix,
                xComparator = COMPARATOR, yComparator = COMPARATOR
            )
        }.let { count ->
            assertEquals(0.9, count)
        }
    }

    @Test
    fun testApproxCountPointInCellCenter() {
        getTestGrid().let { (stepsX, stepsY, densityMatrix) ->
            PointDensityStat.approxCount(
                x = 2.0, y = 1.0,
                stepsX = stepsX, stepsY = stepsY,
                densityMatrix = densityMatrix,
                xComparator = COMPARATOR, yComparator = COMPARATOR
            )
        }.let { count ->
            assertEquals(0.9, count)
        }
    }

    @Test
    fun testApproxCountPointOnCellBorder() {
        mapOf(
            Pair(3.0, 0.0) to 0.6,
            Pair(1.0, 0.0) to 0.5,
            Pair(0.0, 1.5) to 0.8,
            Pair(0.0, 0.5) to 0.5,
            Pair(-3.0, 0.0) to 0.4,
            Pair(-1.0, 0.0) to 0.5,
            Pair(0.0, -1.5) to 0.2,
            Pair(0.0, -0.5) to 0.5,
        ).forEach { (point, expected) ->
            getTestGrid().let { (stepsX, stepsY, densityMatrix) ->
                PointDensityStat.approxCount(
                    x = point.first, y = point.second,
                    stepsX = stepsX, stepsY = stepsY,
                    densityMatrix = densityMatrix,
                    xComparator = COMPARATOR, yComparator = COMPARATOR
                )
            }.let { count ->
                assertEquals(expected, count)
            }
        }
    }

    @Test
    fun testApproxCountPointOnCellBorderCenter() {
        mapOf(
            Pair(2.0, 0.0) to 0.6,
            Pair(0.0, 1.0) to 0.8,
            Pair(-2.0, 0.0) to 0.5,
            Pair(0.0, -1.0) to 0.5,
        ).forEach { (point, expected) ->
            getTestGrid().let { (stepsX, stepsY, densityMatrix) ->
                PointDensityStat.approxCount(
                    x = point.first, y = point.second,
                    stepsX = stepsX, stepsY = stepsY,
                    densityMatrix = densityMatrix,
                    xComparator = COMPARATOR, yComparator = COMPARATOR
                )
            }.let { count ->
                assertEquals(expected, count)
            }
        }
    }

    @Test
    fun testApproxCountPointInCellCorner() {
        mapOf(
            Pair(-4.0, -2.0) to 0.1,
            Pair(0.0, -2.0) to 0.2,
            Pair(4.0, -2.0) to 0.3,
            Pair(-4.0, 0.0) to 0.4,
            Pair(0.0, 0.0) to 0.5,
            Pair(4.0, 0.0) to 0.6,
            Pair(-4.0, 2.0) to 0.7,
            Pair(0.0, 2.0) to 0.8,
            Pair(4.0, 2.0) to 0.9,
        ).forEach { (point, expected) ->
            getTestGrid().let { (stepsX, stepsY, densityMatrix) ->
                PointDensityStat.approxCount(
                    x = point.first, y = point.second,
                    stepsX = stepsX, stepsY = stepsY,
                    densityMatrix = densityMatrix,
                    xComparator = COMPARATOR, yComparator = COMPARATOR
                )
            }.let { count ->
                assertEquals(expected, count)
            }
        }
    }

    @Test
    fun testApproxCountVerySkewedDomains() {
        mapOf(
            Pair(0.0, 0.0) to 0.5,
            Pair(2e16, 0.0) to 0.6,
            Pair(2e16, 1e-16) to 0.9,
            Pair(0.0, 1e-16) to 0.8,
        ).forEach { (point, expected) ->
            getTestGrid(
                xRange = DoubleSpan(-4e16, 4e16),
                yRange = DoubleSpan(-2e-16, 2e-16)
            ).let { (stepsX, stepsY, densityMatrix) ->
                PointDensityStat.approxCount(
                    x = point.first, y = point.second,
                    stepsX = stepsX, stepsY = stepsY,
                    densityMatrix = densityMatrix,
                    xComparator = COMPARATOR, yComparator = COMPARATOR
                )
            }.let { count ->
                assertEquals(expected, count)
            }
        }
    }

    companion object {
        private const val EPSILON = 1e-12
        private val COMPARATOR: Comparator<Double> = compareBy { it }

        private fun countNeighbors(
            xs: List<Double>,
            ys: List<Double>,
            radius: Double,
            weights: List<Double>? = null,
            xy: Double = 1.0
        ): List<Double> {
            return PointDensityStat.countNeighbors(xs, ys, weights ?: List(xs.size) { 1.0 }, radius * radius / xy, xy)
        }

        private fun getTestGrid(
            xRange: DoubleSpan = DoubleSpan(-4.0, 4.0),
            yRange: DoubleSpan = DoubleSpan(-2.0, 2.0)
        ): Triple<List<Double>, List<Double>, BlockRealMatrix> {
            val stepsX = DensityStatUtil.createStepValues(xRange, 3)
            val stepsY = DensityStatUtil.createStepValues(yRange, 3)
            val densityMatrix = BlockRealMatrix(arrayOf(
                doubleArrayOf(0.1, 0.2, 0.3),
                doubleArrayOf(0.4, 0.5, 0.6),
                doubleArrayOf(0.7, 0.8, 0.9)
            ))
            return Triple(stepsX, stepsY, densityMatrix)
        }
    }
}