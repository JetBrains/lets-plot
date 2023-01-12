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
    private val groupAes: Aes<Double>?
) : GeomHelper(pos, coord, ctx) {
    fun getQuantileLineElements(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationBoundStart: (DataPointAesthetics) -> DoubleVector,
        toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector
    ): List<SvgLineElement> {
        val groupComparator: (DataPointAesthetics) -> Comparable<*>? = if (groupAes == null)
            { _ -> null }
        else
            { p -> p[groupAes] }
        val pIt = dataPoints.sortedWith(
            compareBy(
                DataPointAesthetics::group,
                DataPointAesthetics::quantile,
                groupComparator
            )
        ).iterator()
        val quantileLineElements = mutableListOf<SvgLineElement>()
        if (!pIt.hasNext()) return quantileLineElements
        var pPrev = pIt.next()
        while (pIt.hasNext()) {
            val pCurr = pIt.next()
            val quantilesAreSame = pPrev.quantile() == pCurr.quantile() ||
                    (pPrev.quantile()?.isFinite() != true && pCurr.quantile()?.isFinite() != true)
            if (!quantilesAreSame) quantileLineElements.add(getQuantileLineElement(pCurr, toLocationBoundStart, toLocationBoundEnd))
            pPrev = pCurr
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