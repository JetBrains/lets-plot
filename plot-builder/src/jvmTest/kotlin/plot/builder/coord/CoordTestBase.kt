package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import kotlin.test.assertEquals

internal open class CoordTestBase {

    var dataBounds: DoubleRectangle? = null

    /**
     * ratio - ratio between height and width of the display  (ratio = h / w)
     */
    fun tryAdjustDomains(ratio: Double, provider: jetbrains.datalore.plot.builder.coord.CoordProvider, expectedX: ClosedRange<Double>, expectedY: ClosedRange<Double>) {

        val dataBounds = this.dataBounds
        val domainX = dataBounds!!.xRange()
        val domainY = dataBounds.yRange()
        val displaySize = jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.unitDisplaySize(ratio)

        val domains = provider.adjustDomains(domainX, domainY, displaySize)

        assertEquals(expectedX, domains.first, "X range")
        assertEquals(expectedY, domains.second, "Y range")
    }

    /**
     * ratio - ratio between height and width of the display  (ratio = h / w)
     */
    fun tryApplyScales(ratio: Double, provider: jetbrains.datalore.plot.builder.coord.CoordProvider, expectedMin: DoubleVector, expectedMax: DoubleVector, accuracy: DoubleVector) {

        val dataBounds = this.dataBounds
        var domainX = dataBounds!!.xRange()
        var domainY = dataBounds.yRange()
        val displaySize = jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.unitDisplaySize(ratio)
        val domains = provider.adjustDomains(domainX, domainY, displaySize)
        domainX = domains.first
        domainY = domains.second

        val scaleX =
            jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.scaleX(provider, domainX, displaySize.x)
        val scaleY =
            jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.scaleY(provider, domainY, displaySize.y)

        // adapts to display size
        val actualMin =
            jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.applyScales(dataBounds.origin, scaleX, scaleY)
        jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.assertEqualPoints(
            "min",
            expectedMin,
            actualMin,
            accuracy
        )
        val actualMax = jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.applyScales(
            dataBounds.origin.add(dataBounds.dimension),
            scaleX,
            scaleY
        )
        jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.assertEqualPoints(
            "max",
            expectedMax,
            actualMax,
            accuracy
        )
    }

    companion object {
        private const val UNIT = 1.0
        private val EMPTY_BREAKS = GuideBreaks(emptyList<Any>(), emptyList(), emptyList())

        fun unitDisplaySize(ratio: Double): DoubleVector {
            val w = if (ratio > 1) jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.UNIT else jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.UNIT / ratio
            val h = if (ratio < 1) jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.UNIT else jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.UNIT * ratio
            //return new DoubleVector(UNIT, UNIT * ratio);
            return DoubleVector(w, h)
        }

        fun expand(range: ClosedRange<Double>, ratio: Double): ClosedRange<Double> {
            val span = range.upperEndpoint() - range.lowerEndpoint()
            val expand = span * (ratio - 1) / 2.0
            return ClosedRange.closed(
                    range.lowerEndpoint() - expand,
                    range.upperEndpoint() + expand
            )
        }

        fun scaleX(provider: jetbrains.datalore.plot.builder.coord.CoordProvider, domain: ClosedRange<Double>, axisLength: Double): Scale<Double> {
            return provider.buildAxisScaleX(
                    Scales.continuousDomainNumericRange("Test scale X"),
                    domain,
                    axisLength,
                jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.EMPTY_BREAKS
            )
        }

        fun scaleY(provider: jetbrains.datalore.plot.builder.coord.CoordProvider, domain: ClosedRange<Double>, axisLength: Double): Scale<Double> {
            return provider.buildAxisScaleY(
                    Scales.continuousDomainNumericRange("Test scale Y"),
                    domain,
                    axisLength,
                jetbrains.datalore.plot.builder.coord.CoordTestBase.Companion.EMPTY_BREAKS
            )
        }

        fun applyScales(p: DoubleVector, scaleX: Scale<Double>, scaleY: Scale<Double>): DoubleVector {
            return DoubleVector(
                    scaleX.mapper(p.x)!!,
                    scaleY.mapper(p.y)!!)
        }

        private fun assertEqualPoints(text: String, expected: DoubleVector, actual: DoubleVector, accuracy: DoubleVector) {
            assertEquals(expected.x, actual.x, accuracy.x, "$text x")
            assertEquals(expected.y, actual.y, accuracy.y, "$text y")
        }
    }
}
