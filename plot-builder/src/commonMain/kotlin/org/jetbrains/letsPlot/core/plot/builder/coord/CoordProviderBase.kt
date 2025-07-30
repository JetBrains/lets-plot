/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.intern.spatial.projections.identity
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.coord.CoordinatesMapper
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms

abstract class CoordProviderBase(
    protected val xLim: Pair<Double?, Double?>,
    protected val yLim: Pair<Double?, Double?>,
    protected val xReversed: Boolean,
    protected val yReversed: Boolean,
    override val flipped: Boolean,
    protected val projection: Projection = identity(),
) : CoordProvider {

    override val isLinear: Boolean = !projection.nonlinear
    override val isPolar: Boolean = false

    override fun withXlimOverride(xlimOverride: Pair<Double?, Double?>): CoordProvider {
        if (xlimOverride.first == null && xlimOverride.second == null) return this

        val newXLim = mergeRanges(
            xlimOverride,
            xLim,
            xReversed,
            checkRange = false
        )
        return with(newXLim, yLim, xReversed, yReversed, flipped)
    }

    override fun withYlimOverride(ylimOverride: Pair<Double?, Double?>): CoordProvider {
        if (ylimOverride.first == null && ylimOverride.second == null) return this
        val newYLim = mergeRanges(
            ylimOverride,
            yLim,
            yReversed,
            checkRange = false
        )
        return with(xLim, newYLim, xReversed, yReversed, flipped)
    }

    final override fun adjustDomain(dataDomain: DoubleRectangle): DoubleRectangle {
        @Suppress("UNCHECKED_CAST")
        val xRange = mergeRanges(xLim, dataDomain.xRange().toPair(), xReversed, checkRange = true) {
            "Incompatible x-limits and the data range"
        } as Pair<Double, Double>

        @Suppress("UNCHECKED_CAST")
        val yRange = mergeRanges(yLim, dataDomain.yRange().toPair(), yReversed, checkRange = true) {
            "Incompatible y-limits and the data range"
        } as Pair<Double, Double>

        return adjustXYDomains(
            xDomain = DoubleSpan(xRange.first, xRange.second),
            yDomain = DoubleSpan(yRange.first, yRange.second)
        )
    }

    protected open fun adjustXYDomains(xDomain: DoubleSpan, yDomain: DoubleSpan): DoubleRectangle {
        val dataDomain = DoubleRectangle(xDomain, yDomain)
        val validDomain = projection.validDomain().intersect(dataDomain)

        return if (validDomain != null && validDomain.height > 0.0 && validDomain.width > 0.0) {
            validDomain
        } else {
            throw IllegalArgumentException(
                """Can't create a valid domain.
                |  data bbox: $dataDomain
                |  x-lim: $xLim
                |  y-lim: $yLim
            """.trimMargin()
            )
        }
    }

    override fun createCoordinateMapper(
        adjustedDomain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinatesMapper {
        return CoordinatesMapper.create(adjustedDomain, clientSize, projection, flipped)
    }

    private companion object {
        private fun mergeRanges(
            r0: Pair<Double?, Double?>,
            r1: Pair<Double?, Double?>,
            reversed: Boolean,
            checkRange: Boolean,
            errorMessagePrefix: () -> String = { "Incompatible unspecified ranges" },
        ): Pair<Double?, Double?> {
            fun unReverse(range: Pair<Double?, Double?>, reversed: Boolean): Pair<Double?, Double?> {
                return if (reversed) {
                    val first = range.first?.let { Transforms.REVERSE.applyInverse(it) }
                    val second = range.second?.let { Transforms.REVERSE.applyInverse(it) }
                    if (first != null && second != null) {
                        DoubleSpan(first, second).toPair()
                    } else {
                        Pair(first, second)
                    }
                } else {
                    range
                }
            }

            fun reReverse(range: Pair<Double?, Double?>, unReversed: Boolean): Pair<Double?, Double?> {
                return if (unReversed) {
                    val first = range.first?.let { Transforms.REVERSE.apply(it) }
                    val second = range.second?.let { Transforms.REVERSE.apply(it) }
                    if (first != null && second != null) {
                        DoubleSpan(first, second).toPair()
                    } else {
                        Pair(first, second)
                    }
                } else {
                    range
                }
            }

            val norm0 = unReverse(r0, reversed)
            val norm1 = unReverse(r1, reversed)

            val first = norm0.first ?: norm1.first
            val second = norm0.second ?: norm1.second

            if (first != null && second != null && checkRange) {
                require(first < second) { "${errorMessagePrefix.invoke()} : $r0 and $r1" }
            }

            return reReverse(Pair(first, second), reversed)
        }
    }
}
