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

//    init {
//        // ToDo: axis position
//        val leftPadding = if (!vAxisTheme.showTitle() && !vAxisTheme.showLabels()) PADDING else 0.0
//        val bottomPadding = if(!hAxisTheme.showTitle() && !hAxisTheme.showLabels()) PADDING else 0.0
//        setPadding(top = PADDING, right = PADDING, bottomPadding, leftPadding)
//    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
//        val paddingLeftTop = DoubleVector(paddingLeft, paddingTop)
//        val paddingRightBottom = DoubleVector(paddingRight, paddingBottom)

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

//    companion object {
//
//        private fun plotInsets(
//            hAxisOrientation: Orientation,
//            vAxisOrientation: Orientation,
//            hAxisTheme: AxisTheme,
//            vAxisTheme: AxisTheme
//        ): Insets {
//            val emptyPadding = 10.0
//
//            val hPadding = if (vAxisTheme.showTitle() || vAxisTheme.showLabels()) 0.0 else emptyPadding
//            val vPadding = if (hAxisTheme.showTitle() || hAxisTheme.showLabels()) 0.0 else emptyPadding
////        setPadding(top = PADDING, right = PADDING, bottomPadding, leftPadding)
//
//            val (left, right) = when (hAxisOrientation) {
//                Orientation.LEFT -> Pair(hPadding, emptyPadding)
//                else -> Pair(emptyPadding, hPadding)
//            }
//            val (top, bottom) = when (vAxisOrientation) {
//                Orientation.TOP -> Pair(vPadding, emptyPadding)
//                else -> Pair(emptyPadding, vPadding)
//            }
//
//            return Insets(left, top, right, bottom)
//        }
//    }
}
