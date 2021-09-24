/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint

internal class FlippedTargetCollector(private val targetCollector: GeomTargetCollector) : GeomTargetCollector {

    override fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        targetCollector.addPoint(
            index,
            point.flip(),
            radius,
            tooltipParams,
            tooltipKind
        )
    }

    override fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        targetCollector.addRectangle(
            index,
            rectangle.flip(),
            tooltipParams,
            tooltipKind
        )
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        val pointsWithIndex = points.map(DoubleVector::flip).withIndex().reversed()
        val indices = pointsWithIndex.map {
            localToGlobalIndex(it.index)
        }
        targetCollector.addPath(
            pointsWithIndex.map(IndexedValue<DoubleVector>::value),
            { indices[it] },
            tooltipParams,
            tooltipKind
        )
    }

    override fun addPolygon(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        targetCollector.addPolygon(
            points.map(DoubleVector::flip),
            localToGlobalIndex,
            tooltipParams,
            tooltipKind
        )
    }

    override fun flip(): GeomTargetCollector {
        throw IllegalStateException("'flip()' is not applicable to FlippedTargetCollector")
    }
}