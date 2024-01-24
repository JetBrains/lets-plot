/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import kotlin.test.assertEquals

internal open class CoordTestBase {

    lateinit var dataBounds: DoubleRectangle

    /**
     * ratio - ratio between height and width of the display  (ratio = h / w)
     */
    fun tryApplyScales(
        ratio: Double,
        provider: CoordProvider,
        expectedMin: DoubleVector,
        expectedMax: DoubleVector,
        accuracy: DoubleVector
    ) {

        val displaySize = unitDisplaySize(ratio)
        val domain = this.dataBounds
        val adjustedDomain = provider.adjustDomain(domain)
        val adjustedSize = provider.adjustGeomSize(adjustedDomain.xRange(), adjustedDomain.yRange(), displaySize)
        val coordMapper = provider.createCoordinateMapper(adjustedDomain, adjustedSize)

        // adapts to display size
        val actualMin = coordMapper.toClient(dataBounds.origin)!!
        assertEqualPoints(
            "min",
            expectedMin,
            actualMin,
            accuracy
        )

        val p = dataBounds.origin.add(dataBounds.dimension)
        val actualMax = coordMapper.toClient(p)!!
        assertEqualPoints(
            "max",
            expectedMax,
            actualMax,
            accuracy
        )
    }

    companion object {
        private const val UNIT = 1.0
        private val EMPTY_BREAKS = ScaleBreaks.EMPTY

        fun unitDisplaySize(ratio: Double): DoubleVector {
            val w = if (ratio > 1) UNIT else UNIT / ratio
            val h = if (ratio < 1) UNIT else UNIT * ratio
            //return new DoubleVector(UNIT, UNIT * ratio);
            return DoubleVector(w, h)
        }

        fun expand(range: DoubleSpan, ratio: Double): DoubleSpan {
            val span = range.upperEnd - range.lowerEnd
            val expand = span * (ratio - 1) / 2.0
            return DoubleSpan(
                range.lowerEnd - expand,
                range.upperEnd + expand
            )
        }

        private fun assertEqualPoints(
            text: String,
            expected: DoubleVector,
            actual: DoubleVector,
            accuracy: DoubleVector
        ) {
            assertEquals(expected.x, actual.x, accuracy.x, "$text x")
            assertEquals(expected.y, actual.y, accuracy.y, "$text y")
        }
    }
}
