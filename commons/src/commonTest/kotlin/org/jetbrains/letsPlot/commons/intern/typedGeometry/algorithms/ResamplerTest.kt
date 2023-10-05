/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ResamplerTest {
    @Test
    fun specialCase_ZeroPrecision() {
        val res = resample(
            p1 = DoubleVector(0.0, 0.0),
            p2 = DoubleVector(1.0, 0.0),
            precision = 0.0,
            transform = { v -> v }
        )
        assertEquals(1_025, res.size)
    }

    @Test
    fun specialCase_ZeroDistanceAndZeroPrecision() {
        val res = resample(
            p1 = DoubleVector(0.0, 0.0),
            p2 = DoubleVector(0.0, 0.0),
            precision = 0.0,
            transform = { v -> v }
        )
        assertEquals(2, res.size)
    }

    @Test
    fun specialCase_ZeroDistance() {
        val data = listOf(DoubleVector(0.0, 0.0), DoubleVector(0.0, 0.0))
        val res = resample(data, 0.001, transform = { v -> v })
        assertContentEquals(data, res)
    }

    @Test
    fun inputThatAlreadyMatchesPrecisionShouldStayTheSame() {
        val data = listOf(
            DoubleVector(0.0, 0.0),
            DoubleVector(1.0, 0.0),
            DoubleVector(2.0, 0.0)
        )

        val res = resample(data, 1.001, transform = { v -> v })

        assertContentEquals(data, res)
    }

    @Test
    fun simple() {
        val data = listOf(
            DoubleVector(0.0, 0.0),
            DoubleVector(1.0, 0.0),
            DoubleVector(2.0, 0.0)
        )

        val res = resample(data, 0.999, transform = { v -> v })

        assertContentEquals(
            expected = listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(0.5, 0.0),
                DoubleVector(1.0, 0.0),
                DoubleVector(1.5, 0.0),
                DoubleVector(2.0, 0.0)
            ),
            actual = res
        )
    }

    @Test
    fun polarWithPrecisionThatDoesntRequireResampling() {
        val data = listOf(
            polarCoord(0.0, 1.0),
            polarCoord(PI / 2.0, 1.0),
            polarCoord(PI, 1.0)
        )

        val res = resample(points = data, precision = 5.00, transform = ::polarTransform)

        assertContentEquals(
            expected = listOf(
                DoubleVector(1.0, 0.0),
                DoubleVector(6.123233995736766E-17, 1.0),
                DoubleVector(-1.0, 1.2246467991473532E-16)
            ),
            actual = res
        )
    }

    @Test
    fun polarWithPrecisionThatRequireResampling() {
        val data = listOf(
            polarCoord(0.0, 1.0),
            polarCoord(PI / 2.0, 1.0),
            polarCoord(PI, 1.0)
        )

        val res = resample(points = data, precision = 0.5, transform = ::polarTransform)
        assertContentEquals(
            expected = listOf(
                DoubleVector(1.0, 0.0),
                DoubleVector(0.7071067811865476, 0.7071067811865475),
                DoubleVector(6.123233995736766E-17, 1.0),
                DoubleVector(-0.7071067811865475, 0.7071067811865476),
                DoubleVector(-1.0, 1.2246467991473532E-16),
            ),
            actual = res
        )
    }

    @Test
    fun fullCircleInPolar() {
        val data = listOf(
            polarCoord(0.0, 1.0),
            polarCoord(PI * 2.0, 1.0)
        )
        val res = resample(points = data, precision = 0.5, transform = ::polarTransform)
        assertContentEquals(
            expected = listOf(
                DoubleVector(1.0, 0.0),
                DoubleVector(0.7071067811865476, 0.7071067811865475),
                DoubleVector(6.123233995736766E-17, 1.0),
                DoubleVector(-0.7071067811865475, 0.7071067811865476),
                DoubleVector(-1.0, 1.2246467991473532E-16),
                DoubleVector(-0.7071067811865477, -0.7071067811865475),
                DoubleVector(-1.8369701987210297E-16, -1.0),
                DoubleVector(0.7071067811865474, -0.7071067811865477),
                DoubleVector(1.0, -2.4492935982947064E-16),
            ),
            actual = res
        )
    }

    @Test
    fun azimuthalTransform() {
        fun scale(cxcy: Double): Double = sqrt(2.0 / (1.0 + cxcy))
        fun transform(v: DoubleVector): DoubleVector {
            val x = toRadians(v.x)
            val y = toRadians(v.y)
            val cx = cos(x)
            val cy = cos(y)
            val k = scale(cx * cy)

            val px = k * cy * sin(x)
            val py = k * sin(y)

            val clientMul = 169.0

            return DoubleVector(px * clientMul, py * clientMul)
        }

        val data = listOf(
            DoubleVector(-179.999, -80.0),
            DoubleVector(179.999, -80.0),
        )

        val res = resample(data, 10.0, ::transform)

        val expected = listOf(
            DoubleVector(-7.968320712465159E-4, -258.92302177007105),
            DoubleVector(-31.333570854380156, -251.30459698081359),
            DoubleVector(-41.502246294393416, -235.37093489165196),
            DoubleVector(-27.695301205015273, -222.1284645178735),
            DoubleVector(0.0, -217.26221207405027),
            DoubleVector(27.695301205015273, -222.1284645178735),
            DoubleVector(41.502246294393416, -235.37093489165196),
            DoubleVector(31.333570854380156, -251.30459698081359),
            DoubleVector(7.968320712465159E-4, -258.92302177007105)
        )

        expected.zip(res).forEachIndexed() { i, (e, a) ->
            if (
                abs(e.x - a.x) > 1e-6 ||
                abs(e.y - a.y) > 1e-6
            ) {
                println("Error at index $i: $e != $a")
            }
        }
    }

    private fun polarCoord(phi: Double, r: Double) = DoubleVector(phi, r)
    private fun polarTransform(v: DoubleVector) = DoubleVector(v.y * cos(v.x), v.y * sin(v.x))
}
