/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.plotInsets
import jetbrains.datalore.plot.builder.layout.util.Insets
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class SingleTilePlotLayout(
    private val tileLayout: TileLayout,
    hAxisOrientation: Orientation,
    vAxisOrientation: Orientation,
    hAxisTheme: AxisTheme,
    vAxisTheme: AxisTheme,
) : PlotLayout {

    private val insets: Insets = plotInsets(
        hAxisOrientation, vAxisOrientation,
        hAxisTheme, vAxisTheme
    )

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val tilePreferredSize = preferredSize
            .subtract(insets.leftTop)
            .subtract(insets.rightBottom)

        val tileInfo = tileLayout
            .doLayout(tilePreferredSize, coordProvider)
            .withOffset(insets.leftTop)

        val plotSize = tileInfo.bounds.dimension
            .add(insets.leftTop)
            .add(insets.rightBottom)

        return PlotLayoutInfo(listOf(tileInfo), plotSize)
    }
}
