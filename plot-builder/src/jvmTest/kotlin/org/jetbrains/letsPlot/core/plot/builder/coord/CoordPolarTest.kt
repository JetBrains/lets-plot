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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContentEquals

@Ignore
class PolarCoordTest {
    private val x = listOf(0, 90, 180, 270, 360)
    private val y = listOf(0, 1, 2, 3, 4)
    private val domain = DoubleRectangle.LTRB(0, 0, 360, 4)
    private val clientSize = DoubleVector(200.0, 200.0)

    private fun applyPolarTransform(start: Double, clockwise: Boolean): List<Vector> {
        val polarCoordProvider = PolarCoordProvider(xLim = null, yLim = null, flipped = true, start, clockwise)
        val adjustedDomain = polarCoordProvider.adjustDomain(domain)

        val polarMapper = polarCoordProvider.createCoordinateMapper(
            adjustedDomain = adjustedDomain,
            clientSize = clientSize
        )

        return x.zip(y)
            .mapNotNull { (x, y) -> polarMapper.toClient(DoubleVector(x.toDouble(), y.toDouble())) }
            .map { (x, y) -> Vector(x.roundToInt(), y.roundToInt()) }
    }

    private fun applyPolarScreenTransform(start: Double, clockwise: Boolean): List<Vector> {
        val polarCoordProvider = PolarCoordProvider(xLim = null, yLim = null, flipped = true, start, clockwise)
        val adjustedDomain = polarCoordProvider.adjustDomain(domain)

        val coordinateSystem = polarCoordProvider.createCoordinateSystem(adjustedDomain, clientSize)

        return x.zip(y)
            .mapNotNull { (x, y) -> coordinateSystem.toClient(DoubleVector(x.toDouble(), y.toDouble())) }
            .map { (x, y) -> Vector(x.roundToInt(), y.roundToInt()) }
    }

    @Test
    fun default() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(122, 100),
                Vector(100, 57),
                Vector(35, 100),
                Vector(100, 187),
            ),
            actual = applyPolarTransform(start = 0.0, clockwise = true)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(122, 100),
                Vector(100, 143),
                Vector(35, 100),
                Vector(100, 13),
            ),
            actual = applyPolarScreenTransform(start = 0.0, clockwise = true)
        )
    }

    @Test
    fun `direction=-1`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(78, 100),
                Vector(100, 57),
                Vector(165, 100),
                Vector(100, 187),
            ),
            actual = applyPolarTransform(start = 0.0, clockwise = false)
        )
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(78, 100),
                Vector(100, 143),
                Vector(165, 100),
                Vector(100, 13),
            ),
            actual = applyPolarScreenTransform(start = 0.0, clockwise = false)
        )
    }

    @Test
    fun `start=HALF_PI`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 78),
                Vector(57, 100),
                Vector(100, 165),
                Vector(187, 100),
            ),
            actual = applyPolarTransform(start = PI / 2, clockwise = true)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 122),
                Vector(57, 100),
                Vector(100, 35),
                Vector(187, 100),
            ),
            actual = applyPolarScreenTransform(start = PI / 2, clockwise = true)
        )
    }

    @Test
    fun `start=-HALF_PI`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 122),
                Vector(143, 100),
                Vector(100, 35),
                Vector(13, 100),
            ),
            actual = applyPolarTransform(start = -PI / 2, clockwise = true)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 78),
                Vector(143, 100),
                Vector(100, 165),
                Vector(13, 100),
            ),
            actual = applyPolarScreenTransform(start = -PI / 2, clockwise = true)
        )
    }


    @Test
    fun `start=HALF_PI direction=-1`() {
        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 78),
                Vector(143, 100),
                Vector(100, 165),
                Vector(13, 100),
            ),
            actual = applyPolarTransform(start = PI / 2, clockwise = false)
        )

        assertContentEquals(
            expected = listOf(
                Vector(100, 100),
                Vector(100, 122),
                Vector(143, 100),
                Vector(100, 35),
                Vector(13, 100),
            ),
            actual = applyPolarScreenTransform(start = PI / 2, clockwise = false)
        )
    }
}