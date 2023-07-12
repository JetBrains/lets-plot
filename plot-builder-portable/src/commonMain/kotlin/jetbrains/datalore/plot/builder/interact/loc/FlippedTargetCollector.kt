/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.interact.TipLayoutHint

internal class FlippedTargetCollector(
    private val targetCollector: GeomTargetCollector
) : GeomTargetCollector {

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
        index: Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        targetCollector.addPolygon(
            points.map(DoubleVector::flip),
            index,
            tooltipParams,
            tooltipKind
        )
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        throw IllegalStateException("'withFlippedAxis()' is not applicable to FlippedTargetCollector")
    }

    override fun withYOrientation(): GeomTargetCollector {
        check(targetCollector !is YOrientationTargetCollector) { "'withYOrientation()' is not applicable to YOrientationTargetCollector" }
        return YOrientationTargetCollector(this)
    }
}