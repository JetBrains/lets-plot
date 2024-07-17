/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertContentEquals

class PolarCoordTest {
    private val x = listOf(0, 90, 180, 270, 360)
    private val y = listOf(0, 1, 2, 3, 4)
    private val domain = DoubleRectangle.LTRB(0, 0, 360, 4)
    private val clientSize = DoubleVector(200.0, 200.0)

    private fun applyPolarTransform(start: Double, clockwise: Boolean): List<Vector> {
        val polarCoordProvider = PolarCoordProvider(
            xLim = Pair(null, null),
            yLim = Pair(null, null),
            flipped = true,
            start,
            clockwise,
            transformBkgr = false
        )
        val adjustedDomain = polarCoordProvider.adjustDomain(domain)

        val polarMapper = polarCoordProvider.createCoordinateMapper(
            adjustedDomain = adjustedDomain.flipIf(true), // FixMe: polar hack: should work without a flip
            clientSize = clientSize
        )

        return x.zip(y)
            .mapNotNull { (x, y) -> polarMapper.toClient(DoubleVector(x.toDouble(), y.toDouble())) }
            .map { (x, y) -> Vector(x.roundToInt(), y.roundToInt()) }
    }

    private fun applyPolarScreenTransform(start: Double, clockwise: Boolean): List<Vector> {
        val polarCoordProvider = PolarCoordProvider(
            xLim = Pair(null, null),
            yLim = Pair(null, null),
            flipped = true,
            start,
            clockwise,
            transformBkgr = false
        )
        val adjustedDomain = polarCoordProvider.adjustDomain(domain)

        val coordinateSystem = polarCoordProvider.createCoordinateSystem(
            adjustedDomain.flipIf(true),  // FixMe: polar hack: should work without a flip
            clientSize
        )

        return x.zip(y)
            .mapNotNull { (x, y) -> coordinateSystem.toClient(DoubleVector(x.toDouble(), y.toDouble())) }
            .map { (x, y) -> Vector(x.roundToInt(), y.roundToInt()) }
    }

    @Test
    fun default() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(121, 100),
                Vector(100, 59),
                Vector(38, 100),
                Vector(100, 183),
            ),
            actual = applyPolarTransform(start = 0.0, clockwise = true)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(121, 100),
                Vector(100, 141),
                Vector(38, 100),
                Vector(100, 17),
            ),
            actual = applyPolarScreenTransform(start = 0.0, clockwise = true)
        )
    }

    @Test
    fun `direction=-1`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(79, 100),
                Vector(100, 59),
                Vector(162, 100),
                Vector(100, 183),
            ),
            actual = applyPolarTransform(start = 0.0, clockwise = false)
        )
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(79, 100),
                Vector(100, 141),
                Vector(162, 100),
                Vector(100, 17),
            ),
            actual = applyPolarScreenTransform(start = 0.0, clockwise = false)
        )
    }

    @Test
    fun `start=HALF_PI`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 79),
                Vector(59, 100),
                Vector(100, 162),
                Vector(183, 100),
            ),
            actual = applyPolarTransform(start = PI / 2, clockwise = true)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 121),
                Vector(59, 100),
                Vector(100, 38),
                Vector(183, 100),
            ),
            actual = applyPolarScreenTransform(start = PI / 2, clockwise = true)
        )
    }

    @Test
    fun `start=-HALF_PI`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 121),
                Vector(141, 100),
                Vector(100, 38),
                Vector(17, 100),
            ),
            actual = applyPolarTransform(start = -PI / 2, clockwise = true)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 79),
                Vector(141, 100),
                Vector(100, 162),
                Vector(17, 100),
            ),
            actual = applyPolarScreenTransform(start = -PI / 2, clockwise = true)
        )
    }


    @Test
    fun `start=HALF_PI direction=-1`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 79),
                Vector(141, 100),
                Vector(100, 162),
                Vector(17, 100),
            ),
            actual = applyPolarTransform(start = PI / 2, clockwise = false)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 121),
                Vector(141, 100),
                Vector(100, 38),
                Vector(17, 100),
            ),
            actual = applyPolarScreenTransform(start = PI / 2, clockwise = false)
        )
    }
}