/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import kotlin.test.assertEquals

internal open class CoordTestBase {

    lateinit var dataBounds: DoubleRectangle

    /**
     * ratio - ratio between height and width of the display  (ratio = h / w)
     */
    fun tryAdjustDomains(
        ratio: Double,
        provider: CoordProvider,
        expectedX: DoubleSpan,
        expectedY: DoubleSpan
    ) {

        val dataBounds = this.dataBounds
        val domainX = dataBounds.xRange()
        val domainY = dataBounds.yRange()
//        val displaySize = unitDisplaySize(ratio)

//        val domains = provider.adjustDomains(domainX, domainY, displaySize)
        val domains = provider.adjustDomains(domainX, domainY)
        // The `adjustDomains` has different meaning now!!!

        assertEquals(expectedX, domains.first, "X range")
        assertEquals(expectedY, domains.second, "Y range")
    }

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

        val dataBounds = this.dataBounds
        var domainX = dataBounds.xRange()
        var domainY = dataBounds.yRange()
        val displaySize = unitDisplaySize(ratio)
        val domains = provider.adjustDomains(domainX, domainY)
        domainX = domains.first
        domainY = domains.second

//        val scaleX = scaleX(provider, domainX, displaySize.x)
//        val scaleY = scaleY(provider, domainY, displaySize.y)

        val scaleXMapper = provider.buildAxisXScaleMapper(domainX, displaySize.x)
        val scaleYMapper = provider.buildAxisYScaleMapper(domainY, displaySize.y)

        // adapts to display size
        val actualMin = DoubleVector(scaleXMapper(dataBounds.origin.x)!!, scaleYMapper(dataBounds.origin.y)!!)
        assertEqualPoints(
            "min",
            expectedMin,
            actualMin,
            accuracy
        )

        val p = dataBounds.origin.add(dataBounds.dimension)
        val actualMax = DoubleVector(scaleXMapper(p.x)!!, scaleYMapper(p.y)!!)
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

//        fun scaleX(provider: CoordProvider, domain: DoubleSpan, axisLength: Double): Scale<Double> {
//            return provider.buildAxisScaleX(
//                Scales.DemoAndTest.continuousDomainNumericRange("Test scale X"),
//                domain,
//                axisLength,
//                EMPTY_BREAKS
//            )
//        }

//        fun scaleY(provider: CoordProvider, domain: DoubleSpan, axisLength: Double): Scale<Double> {
//            return provider.buildAxisScaleY(
//                Scales.DemoAndTest.continuousDomainNumericRange("Test scale Y"),
//                domain,
//                axisLength,
//                EMPTY_BREAKS
//            )
//        }

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
