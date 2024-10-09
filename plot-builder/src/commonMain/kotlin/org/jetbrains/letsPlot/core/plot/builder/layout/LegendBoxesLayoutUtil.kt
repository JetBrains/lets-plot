/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.guide.LegendArrangement

internal object LegendBoxesLayoutUtil {
    fun arrangeLegendBoxes(
        infos: List<LegendBoxInfo>,
        theme: LegendTheme
    ): LegendsBlockInfo {
        val boxWithLocationList = when (theme.legendBoxArrangement()) {
            LegendArrangement.VERTICAL -> verticalStack(infos, theme.spacing().y)
            LegendArrangement.HORIZONTAL -> horizontalStack(infos, theme.spacing().x)
        }
        return LegendsBlockInfo(boxWithLocationList)
    }

    private fun verticalStack(boxInfos: List<LegendBoxInfo>, spacing: Double): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        var y = 0.0
        for (info in boxInfos) {
            result.add(
                LegendBoxesLayout.BoxWithLocation(
                    info,
                    DoubleVector(0.0, y)
                )
            )
            y += info.size.y + spacing
        }
        return result
    }

    private fun horizontalStack(boxInfos: List<LegendBoxInfo>, spacing: Double): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        var x = 0.0
        for (info in boxInfos) {
            result.add(
                LegendBoxesLayout.BoxWithLocation(
                    info,
                    DoubleVector(x, 0.0)
                )
            )
            x += info.size.x + spacing
        }
        return result
    }

    fun overlayLegendOriginInsidePlot(
        plotBounds: DoubleRectangle,
        legendSize: DoubleVector,
        legendPosition: LegendPosition,
        legendJustification: LegendJustification
    ): DoubleVector {
        val plotSize = plotBounds.dimension

        // [0,0] -> bottom-left, [1,1] -> top, right
        val absolutePosition = DoubleVector(
            plotBounds.left + plotSize.x * legendPosition.x,
            plotBounds.bottom - plotSize.y * legendPosition.y
        )

        // legendJustification: [0,0] -> bottom-left, [1,1] -> top, right
        val originOffset = DoubleVector(
            -legendSize.x * legendJustification.x,
            legendSize.y * legendJustification.y - legendSize.y
        )

        return absolutePosition.add(originOffset)
    }

    fun overlayLegendOriginOutsidePlot(
        innerBounds: DoubleRectangle,
        outerBounds: DoubleRectangle,
        legendSize: DoubleVector,
        legendPosition: LegendPosition,
        legendJustification: LegendJustification
    ): DoubleVector {

        return when (legendPosition) {
            LegendPosition.LEFT, LegendPosition.RIGHT -> {
                val y = innerBounds.top + (innerBounds.height - legendSize.y) * ( 1 - legendJustification.y)
                val x = if (legendPosition == LegendPosition.LEFT) outerBounds.left else outerBounds.right - legendSize.x
                DoubleVector(x, y)
            }
            LegendPosition.TOP, LegendPosition.BOTTOM -> {
                val x = innerBounds.left + (innerBounds.width - legendSize.x) * legendJustification.x
                val y = if (legendPosition == LegendPosition.TOP) outerBounds.top else outerBounds.bottom - legendSize.y
                DoubleVector(x, y)
            }
            else -> throw IllegalArgumentException("Expect fixed legend position, " +
                    "but was inside via numeric vector: ${legendPosition.x}, ${legendPosition.y}")
        }
    }
}
