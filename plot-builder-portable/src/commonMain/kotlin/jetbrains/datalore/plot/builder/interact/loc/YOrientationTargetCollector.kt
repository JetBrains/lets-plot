/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil.flipAesKeys

internal class YOrientationTargetCollector(
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
            point,
            radius,
            afterYOrientation(tooltipParams),
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
            rectangle,
            afterYOrientation(tooltipParams),
            tooltipKind
        )
    }

    override fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: GeomTargetCollector.TooltipParams,
        tooltipKind: TipLayoutHint.Kind
    ) {
        targetCollector.addPath(
            points,
            localToGlobalIndex,
            afterYOrientation(tooltipParams),
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
            points,
            index,
            afterYOrientation(tooltipParams),
            tooltipKind
        )
    }

    override fun withFlippedAxis(): GeomTargetCollector {
        check(targetCollector !is FlippedTargetCollector) { "'withFlippedAxis()' is not applicable to FlippedTargetCollector" }
        return FlippedTargetCollector(this)
    }

    override fun withYOrientation(): GeomTargetCollector {
        throw IllegalStateException("'withYOrientation()' is not applicable to YOrientationTargetCollector")
    }

    companion object {
        private fun afterYOrientation(tooltipParams: GeomTargetCollector.TooltipParams): GeomTargetCollector.TooltipParams {
            return GeomTargetCollector.TooltipParams(
                tipLayoutHints = flipAesKeys(tooltipParams.tipLayoutHints),
                stemLength = tooltipParams.stemLength,
                fillColor = tooltipParams.fillColor,
                markerColors = tooltipParams.markerColors,
            )
        }
    }
}