/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams

class NullGeomTargetCollector : GeomTargetCollector {
    override fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind,
        useWidthForHintOffset: Boolean
    ) {
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun addPolygon(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        return this
    }

    override fun withYOrientation(): GeomTargetCollector {
        return this
    }
}
