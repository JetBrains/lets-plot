/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.base.interact.GeomTargetLocator


class PlotTooltipBounds(
    val placementArea: DoubleRectangle,
    val handlingArea: DoubleRectangle
)

interface PlotInteractor : Disposable {
    fun onTileAdded(
        geomBounds: DoubleRectangle,
        tooltipBounds: PlotTooltipBounds,
        targetLocators: List<GeomTargetLocator>
    )

    fun onViewReset(function: () -> Unit)
    fun onViewZoomArea(function: (DoubleRectangle) -> Unit)
    fun onViewZoomIn(function: (DoubleVector) -> Unit)
    fun onViewZoomOut(function: (DoubleVector) -> Unit)
    fun onViewPanning(function: (DoubleVector) -> Unit)
}
