/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.interact.tool.ToolFeedback
import jetbrains.datalore.plot.builder.tooltip.HorizontalAxisTooltipPosition
import jetbrains.datalore.plot.builder.tooltip.VerticalAxisTooltipPosition

interface PlotInteractor : Disposable {
    fun onTileAdded(
        geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        layerYOrientations: List<Boolean>,
        axisOrigin: DoubleVector
    )

    fun setAxisTooltipPositions(
        hAxisTooltipPosition: HorizontalAxisTooltipPosition,
        vAxisTooltipPosition: VerticalAxisTooltipPosition
    )

    fun startToolFeedback(toolFeedback: ToolFeedback): Registration
}
