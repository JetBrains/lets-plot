/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.plot.base.BogusContext
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.coord.CoordinatesMapper
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PathGeomTest {
    private val coord = Coords.create(
        CoordinatesMapper.create(
            DoubleRectangle.LTRB(0, 0, 200, 200),
            DoubleVector(200.0, 200.0),
            identity(),
            false
        )
    )
    private var linesHelper: LinesHelper = LinesHelper(PositionAdjustments.identity(), coord, BogusContext)

    @Test
    fun twoPointsWithDifferentSize() {
        val aes = AestheticsBuilder(2)
            .x(listOf(0.0, 5.0)::get)
            .y(listOf(0.0, 5.0)::get)
            .size(listOf(2.0, 4.0)::get)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        visualPath[0].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(0.0, 0.0),
                    DoubleVector(2.5, 2.5)
                ),
                actual = it.coordinates
            )
            assertEquals(2.0, it.aes.size())
        }

        visualPath[1].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(2.5, 2.5),
                    DoubleVector(5.0, 5.0),
                ),
                actual = it.coordinates
            )
            assertEquals(4.0, it.aes.size())
        }

        assertEquals(2, visualPath.size)
    }

    @Test
    fun sawtooth() {
        val aes = AestheticsBuilder(4)
            .x(listOf(1.0, 2.0, 3.0, 4.0)::get)
            .y(listOf(0.0, 5.0, 0.0, 10.0)::get)
            .size(listOf(10.0, 10.0, 10.0, 8.0)::get)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        visualPath[0].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(1.0, 0.0),
                    DoubleVector(2.0, 5.0),
                    DoubleVector(3.0, 0.0),
                    DoubleVector(3.5, 5.0),
                ),
                actual = it.coordinates
            )

            assertEquals(10.0, it.aes.size())
        }

        visualPath[1].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(3.5, 5.0),
                    DoubleVector(4.0, 10.0),
                ),
                actual = it.coordinates
            )
            assertEquals(8.0, it.aes.size())
        }

        assertEquals(2, visualPath.size)
    }

    @Test
    fun simpleLineSizeMapping() {
        val aes = AestheticsBuilder(3)
            .x(listOf(0.0, 5.0, 10.0)::get)
            .y(listOf(0.0, 5.0, 0.0)::get)
            .size(listOf(2.0, 8.0, 16.0)::get)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        visualPath[0].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(0.0, 0.0),
                    DoubleVector(2.5, 2.5)
                ),
                actual = it.coordinates
            )
            assertEquals(2.0, it.aes.size())
        }

        visualPath[1].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(2.5, 2.5),
                    DoubleVector(5.0, 5.0),
                    DoubleVector(7.5, 2.5)
                ),
                actual = it.coordinates
            )
            assertEquals(8.0, it.aes.size())
        }

        visualPath[2].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(7.5, 2.5),
                    DoubleVector(10.0, 0.0)
                ),
                actual = it.coordinates
            )
            assertEquals(16.0, it.aes.size())
        }
        assertEquals(3, visualPath.size)
    }


    @Test
    fun noPointsWithSameSize() {
        val aes = AestheticsBuilder(0)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        assertTrue(visualPath.isEmpty())
    }

    @Test
    fun onePointsWithSameSize() {
        val aes = AestheticsBuilder(1)
            .x(listOf(0.0)::get)
            .y(listOf(0.0)::get)
            .size(listOf(2.0)::get)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        visualPath[0].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(0.0, 0.0)
                ),
                actual = it.coordinates
            )
            assertEquals(2.0, it.aes.size())
        }
        assertEquals(1, visualPath.size)
    }


    @Test
    fun twoPointsWithSameSize() {
        val aes = AestheticsBuilder(2)
            .x(listOf(0.0, 5.0)::get)
            .y(listOf(0.0, 5.0)::get)
            .size(listOf(2.0, 2.0)::get)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        visualPath[0].let {
            assertEquals(
                expected = clientPoints(
                    DoubleVector(0.0, 0.0),
                    DoubleVector(5.0, 5.0)
                ),
                actual = it.coordinates
            )
            assertEquals(2.0, it.aes.size())
        }
        assertEquals(1, visualPath.size)
    }

    @Test
    fun sameSizeMapping() {
        val aes = AestheticsBuilder(3)
            .x(listOf(1.0, 2.0, 3.0)::get)
            .y(MutableList(3) { 0.0 }::get)
            .size(listOf(1.0, 1.0, 1.0)::get)
            .build()

        val variadicPathData = PathGeom.createVariadicPathData(aes.dataPoints(), linesHelper)
        val visualPath = PathGeom.createVisualPath(variadicPathData)

        assertEquals(1, visualPath.size)
        assertEquals(
            listOf(1.0, 1.0, 1.0),
            visualPath.single().aesthetics.map(DataPointAesthetics::size)
        )
    }


    private fun clientPoints(vararg points: DoubleVector): List<DoubleVector?> {
        return points.map(coord::toClient)
    }
}