/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.common.data.SeriesUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

open class QuantilesHelper(
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val quantiles: List<Double>,
    private val groupAes: Aes<Double>? = null
) : GeomHelper(pos, coord, ctx) {
    internal fun splitByQuantiles(
        dataPoints: Iterable<DataPointAesthetics>,
        axisAes: Aes<Double>
    ): List<List<DataPointAesthetics>> {
        if (dataPoints.none()) {
            return emptyList()
        }

        // Fix semi-transparent quantile edges when colored/filled with a single color
        if (!needToSplit(dataPoints)) {
            return listOf(dataPoints.toList())
        }

        val dataPointBunches = mutableListOf<MutableList<DataPointAesthetics>>()
        iterateThroughSortedDataPoints(dataPoints, axisAes) { sortedDataPoints ->
            var quantilePoints = mutableListOf(sortedDataPoints.first())
            for (i in 1 until sortedDataPoints.size) {
                val prev = sortedDataPoints[i - 1]
                val curr = sortedDataPoints[i]
                if (SeriesUtil.isFinite(prev.quantile()) && SeriesUtil.isFinite(curr.quantile())) {
                    if (prev.quantile() == curr.quantile()) {
                        quantilePoints.add(curr)
                    } else {
                        dataPointBunches.add(quantilePoints)
                        quantilePoints = mutableListOf(curr)
                    }
                } else {
                    quantilePoints.add(curr)
                }
            }
            if (quantilePoints.size > 0) {
                dataPointBunches.add(quantilePoints)
            }
        }

        return dataPointBunches
    }

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
        iterateThroughSortedDataPoints(dataPoints, axisAes) { ascendingSortedDataPoints ->
            val sortedDataPoints = ascendingSortedDataPoints.asReversed()
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

    // true if in any group there is at least two distinct values of color or fill aesthetic
    private fun needToSplit(dataPoints: Iterable<DataPointAesthetics>): Boolean {
        val groupColors = mutableMapOf<Int?, Pair<Color?, Color?>>()
        for (p in dataPoints) {
            val pointColors = Pair(p.fill(), p.color())

            when {
                p.group() !in groupColors -> groupColors[p.group()] = pointColors
                groupColors[p.group()] == pointColors -> continue
                groupColors[p.group()] != pointColors -> return true
            }
        }
        return false
    }

    private fun iterateThroughSortedDataPoints(
        dataPoints: Iterable<DataPointAesthetics>,
        axisAes: Aes<Double>,
        action: (List<DataPointAesthetics>) -> Unit
    ) {
        dataPoints.groupBy { p ->
            when (groupAes) {
                null -> p.group()
                else -> Pair(p.group(), p[groupAes])
            }
        }.forEach { (_, groupedDataPoints) ->
            action(groupedDataPoints.sortedWith(compareBy(DataPointAesthetics::quantile, { it[axisAes] })))
        }
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