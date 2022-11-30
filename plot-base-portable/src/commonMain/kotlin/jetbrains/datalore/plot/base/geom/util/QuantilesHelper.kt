/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.vis.svg.SvgLineElement

open class QuantilesHelper(
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val drawQuantiles: List<Double>,
    private val sampleAes: Aes<Double>,
    private val densityAes: Aes<Double>,
    private val groupAes: Aes<Double>?
) : GeomHelper(pos, coord, ctx) {
    fun createGroupedQuantiles(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationBoundStart: (DataPointAesthetics) -> DoubleVector,
        toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector
    ): MutableList<SvgLineElement> {
        val quantileLines = mutableListOf<SvgLineElement>()
        if (drawQuantiles.isEmpty()) return quantileLines
        for ((_, dataPointsGroup) in dataPoints.groupBy { it.group() }) {
            quantileLines.addAll(createQuantiles(dataPointsGroup, toLocationBoundStart, toLocationBoundEnd, true))
        }
        return quantileLines
    }

    fun createQuantiles(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationBoundStart: (DataPointAesthetics) -> DoubleVector,
        toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector,
        grouped: Boolean = false
    ): MutableList<SvgLineElement> {
        val quantileLines = mutableListOf<SvgLineElement>()
        if (drawQuantiles.isEmpty()) return quantileLines
        val geomHelper = createSvgElementHelper()
        val group = if (grouped) dataPoints.first().group() else null
        for (p in calculateQuantiles(dataPoints, group)) {
            val start = toLocationBoundStart(p)
            val end = toLocationBoundEnd(p)
            val line = geomHelper.createLine(start, end, p)!!
            quantileLines.add(line)
        }
        return quantileLines
    }

    private fun calculateQuantiles(
        dataPoints: Iterable<DataPointAesthetics>,
        group: Int?
    ): Iterable<DataPointAesthetics> {
        val sampleValues = dataPoints.map { it[sampleAes]!! }
        val densityValues = dataPoints.map { it[densityAes]!! }
        val densityValuesSum = densityValues.sum()
        val dens = densityValues.runningReduce { cumSum, elem -> cumSum + elem }.map { it / densityValuesSum }
        val quantSample = drawQuantiles.map { pwLinInterp(dens, sampleValues)(it) }
        val quantDensity = quantSample.map { pwLinInterp(sampleValues, densityValues)(it) }
        val constWidth = dataPoints.first().width()
        val constHeight = dataPoints.first().height()
        val constColor = dataPoints.first().color()
        val constSize = dataPoints.first().size()

        val builder = AestheticsBuilder(quantSample.size)
            .aes(sampleAes, AestheticsBuilder.list(quantSample))
            .aes(densityAes, AestheticsBuilder.list(quantDensity))
            .color(AestheticsBuilder.constant(constColor))
            .size(AestheticsBuilder.constant(constSize))
        if (groupAes != null) {
            val groupValue = dataPoints.first()[groupAes]
            builder.aes(groupAes, AestheticsBuilder.constant(groupValue))
            builder.group(AestheticsBuilder.constant(group ?: DEFAULT_GROUP_VALUE))
        }
        if (densityAes != Aes.HEIGHT)
            builder.height(AestheticsBuilder.constant(constHeight))
        if (densityAes != Aes.WIDTH)
            builder.width(AestheticsBuilder.constant(constWidth))
        return builder.build().dataPoints()
    }

    private fun pwLinInterp(x: List<Double>, y: List<Double>): (Double) -> Double {
        // Returns (bounded) piecewise linear interpolation function
        return fun(t: Double): Double {
            val i = x.indexOfFirst { it >= t }
            if (i == 0) return y.first()
            if (i == -1) return y.last()
            val a = (y[i] - y[i - 1]) / (x[i] - x[i - 1])
            val b = y[i - 1] - a * x[i - 1]
            return a * t + b
        }
    }

    companion object {
        private const val DEFAULT_GROUP_VALUE = 0
    }
}