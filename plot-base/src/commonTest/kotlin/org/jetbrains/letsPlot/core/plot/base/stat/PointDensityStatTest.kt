/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import demoAndTestShared.assertArrayEquals
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.scale.transform.IdentityTransform
import org.jetbrains.letsPlot.core.plot.base.stat.math3.BlockRealMatrix
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.data.DataProcessing
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PointDensityStatTest : BaseStatTest() {

    @Test
    fun basic() {
        val radiusToAdjust: (Double) -> Double = { r -> 9 * r * r }
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, 0.0, 4.0),
                TransformVar.Y to listOf(0.0, 2.0, 0.0)
            ),
            adjust = radiusToAdjust(5.0),
            expectedCounts = listOf(3.0, 2.0, 2.0)
        )
    }

    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(pointdensity())
    }

    @Test
    fun flatOneDimension() {
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, 0.0, 0.0),
                TransformVar.Y to listOf(0.0, 2.0, 6.0)
            ),
            adjust = 36.0,
            expectedCounts = listOf(2.0, 2.0, 1.0)
        )
    }

    @Test
    fun flatBothDimensions() {
        val radiusToAdjust: (Double) -> Double = { r -> 9 * r * r }
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, 0.0, 0.0),
                TransformVar.Y to listOf(0.0, 0.0, 0.0)
            ),
            adjust = radiusToAdjust(1.0),
            expectedCounts = listOf(3.0, 3.0, 3.0)
        )
    }

    @Test
    fun oneElementDataFrame() {
        val radiusToAdjust: (Double) -> Double = { r -> 9 * r * r }
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0),
                TransformVar.Y to listOf(0.0)
            ),
            adjust = radiusToAdjust(1.0),
            expectedCounts = listOf(1.0)
        )
    }

    @Test
    fun withWeight() {
        val radiusToAdjust: (Double) -> Double = { r -> 9 * r * r }
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, 0.0, 4.0),
                TransformVar.Y to listOf(0.0, 2.0, 0.0),
                TransformVar.WEIGHT to listOf(2.0, 1.0, 1.0)
            ),
            adjust = radiusToAdjust(5.0),
            expectedCounts = listOf(4.0, 3.0, 3.0)
        )
    }

    @Test
    fun withNAValues() {
        val radiusToAdjust: (Double) -> Double = { r -> 9 * r * r }
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, null, Double.NaN, 0.0, 0.0, 0.0, 4.0, 4.0, 4.0),
                TransformVar.Y to listOf(0.0, 0.0, 0.0, 2.0, null, Double.NaN, 0.0, 0.0, 0.0),
                TransformVar.WEIGHT to listOf(2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, null, Double.NaN)
            ),
            adjust = radiusToAdjust(5.0),
            expectedCounts = listOf(4.0, 3.0, 3.0, 3.0, 3.0)
        )
    }

    @Test
    fun withIndices() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0),
            TransformVar.Y to listOf(0.0, 1.0, null, 3.0, 4.0, 5.0, 6.0, null, 8.0, 9.0),
            TransformVar.SIZE to listOf(10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
        ))
        val bindings = listOf(
            VarBinding(TransformVar.X, Aes.X),
            VarBinding(TransformVar.Y, Aes.Y),
            VarBinding(TransformVar.SIZE, Aes.SIZE)
        )
        val transformByAes: Map<Aes<*>, Transform> = mapOf(
            Aes.X to IdentityTransform(),
            Aes.Y to IdentityTransform(),
            Aes.SIZE to IdentityTransform()
        )
        val statDf = DataProcessing.applyStatTest(
            data = df,
            stat = pointdensity(),
            bindings = bindings,
            transformByAes = transformByAes,
            statCtx = statContext(df)
        )
        assertArrayEquals(arrayOf(10.0, 11.0, 13.0, 14.0, 15.0, 16.0, 18.0, 19.0), statDf.getNumeric(TransformVar.SIZE).toTypedArray())
    }

    @Test
    fun methodKde2d() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 0.0)
        ))
        val stat = pointdensity(method = PointDensityStat.Method.KDE2D)
        val statDf = stat.apply(df, statContext(df))
        val expectedDensityOrder = listOf(2, 1, 0)
        statDf.getNumeric(Stats.DENSITY).slice(expectedDensityOrder).zipWithNext { a, b ->
            assertNotNull(a)
            assertNotNull(b)
            assertTrue(a!! <= b!!, "Expected density ordered as $expectedDensityOrder")
        }
    }

    @Test
    fun adjustedRadius() {
        val radiusToAdjust: (Double) -> Double = { r -> 9 * r * r }
        listOf(
            radiusToAdjust(0.0) to listOf(1.0, 1.0, 1.0),
            radiusToAdjust(4.0 - EPSILON) to listOf(1.0, 1.0, 1.0),
            radiusToAdjust(4.0) to listOf(3.0, 2.0, 2.0),
            radiusToAdjust(sqrt(32.0) - EPSILON) to listOf(3.0, 2.0, 2.0),
            radiusToAdjust(sqrt(32.0)) to listOf(3.0, 3.0, 3.0),
        ).forEach { (adjust, expectedCounts) ->
            check(
                data = mapOf(
                    TransformVar.X to listOf(0.0, 0.0, 4.0),
                    TransformVar.Y to listOf(0.0, 2.0, 0.0)
                ),
                adjust = adjust,
                expectedCounts = expectedCounts
            )
        }
    }

    @Test
    fun regressionForComparator() {
        val badValue = 41 * sqrt(2.0)
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(-badValue, -badValue, 3 * badValue),
            TransformVar.Y to listOf(-badValue, badValue, -badValue)
        ))
        val stat = pointdensity(method = PointDensityStat.Method.KDE2D)
        val statDf = stat.apply(df, statContext(df))
        val expectedDensityOrder = listOf(2, 1, 0)
        statDf.getNumeric(Stats.DENSITY).slice(expectedDensityOrder).zipWithNext { a, b ->
            assertNotNull(a)
            assertNotNull(b)
            assertTrue(a!! <= b!!, "Expected density ordered as $expectedDensityOrder")
        }
    }

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

    private fun check(
        data: Map<DataFrame.Variable, List<Double?>>,
        adjust: Double,
        expectedCounts: List<Double>
    ) {
        val df = dataFrame(data)
        val stat = pointdensity(adjust = adjust)
        val statDf = stat.apply(df, statContext(df))
        checkStatVarValues(statDf, Stats.COUNT, expectedCounts)
    }

    companion object {
        private const val EPSILON = 1e-12
        private val COMPARATOR: Comparator<Double> = compareBy { it }

        private fun pointdensity(
            method: PointDensityStat.Method = PointDensityStat.Method.NEIGHBOURS,
            adjust: Double = 1.0
        ): PointDensityStat {
            return PointDensityStat(
                bandWidthX = null,
                bandWidthY = null,
                bandWidthMethod = AbstractDensity2dStat.DEF_BW,
                adjust = adjust,
                kernel = AbstractDensity2dStat.DEF_KERNEL,
                nX = AbstractDensity2dStat.DEF_N,
                nY = AbstractDensity2dStat.DEF_N,
                method = method
            )
        }

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