/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.vis.svg.SvgLineElement

open class QuantilesHelper(
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val quantiles: List<Double>,
    private val groupAes: Aes<Double>? = null
) : GeomHelper(pos, coord, ctx) {
    internal fun getQuantileLineElements(
        dataPoints: Iterable<DataPointAesthetics>,
        axisAes: Aes<Double>,
        toLocationBoundStart: (DataPointAesthetics) -> DoubleVector,
        toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector
    ): List<SvgLineElement> {
        if (quantiles.isEmpty() || dataPoints.none()) {
            return emptyList()
        }
        val quantiles = quantiles.sortedDescending()
        val quantileLineElements = mutableListOf<SvgLineElement>()
        dataPoints.groupBy { p ->
            when (groupAes) {
                null -> p.group()
                else -> Pair(p.group(), p[groupAes])
            }
        }.forEach { (_, groupedDataPoints) ->
            val sortedDataPoints = groupedDataPoints.sortedWith(compareBy(DataPointAesthetics::quantile, { it[axisAes] })).asReversed()
            var currPointsIdx = 0
            for (quantile in quantiles) {
                while (currPointsIdx < sortedDataPoints.size) {
                    val p = sortedDataPoints[currPointsIdx]
                    currPointsIdx++
                    if (quantile == p.quantile()) {
                        quantileLineElements.add(getQuantileLineElement(p, toLocationBoundStart, toLocationBoundEnd))
                        break
                    }
                }
            }
        }
        return quantileLineElements
    }

    private fun getQuantileLineElement(
        dataPoint: DataPointAesthetics,
        toLocationBoundStart: (DataPointAesthetics) -> DoubleVector,
        toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector
    ): SvgLineElement {
        val svgElementHelper = GeomHelper(pos, coord, ctx).createSvgElementHelper()
        val start = toLocationBoundStart(dataPoint)
        val end = toLocationBoundEnd(dataPoint)
        return svgElementHelper.createLine(start, end, dataPoint)!!
    }
}