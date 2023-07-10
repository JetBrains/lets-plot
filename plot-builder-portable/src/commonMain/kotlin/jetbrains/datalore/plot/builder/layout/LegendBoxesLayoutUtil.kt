/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.LegendArrangement
import jetbrains.datalore.plot.builder.theme.LegendTheme

internal object LegendBoxesLayoutUtil {
    fun arrangeLegendBoxes(
        infos: List<LegendBoxInfo>,
        @Suppress("UNUSED_PARAMETER") theme: LegendTheme
    ): LegendsBlockInfo {
        // ToDo: legend.box options in theme
        val legendArrangement = LegendArrangement.VERTICAL
        val boxWithLocationList = when (legendArrangement) {
            LegendArrangement.VERTICAL -> verticalStack(infos)
            else -> horizontalStack(infos)
        }
        return LegendsBlockInfo(boxWithLocationList)
    }

    private fun verticalStack(boxInfos: List<LegendBoxInfo>): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        var y = 0.0
        for (info in boxInfos) {
            result.add(
                LegendBoxesLayout.BoxWithLocation(
                    info,
                    DoubleVector(0.0, y)
                )
            )
            y += info.size.y
        }
        return result
    }

    private fun horizontalStack(boxInfos: List<LegendBoxInfo>): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        var x = 0.0
        for (info in boxInfos) {
            result.add(
                LegendBoxesLayout.BoxWithLocation(
                    info,
                    DoubleVector(x, 0.0)
                )
            )
            x += info.size.x
        }
        return result
    }

    fun overlayLegendOrigin(
        plotBounds: DoubleRectangle,
        legendSize: DoubleVector,
        legendPosition: jetbrains.datalore.plot.builder.guide.LegendPosition,
        legendJustification: jetbrains.datalore.plot.builder.guide.LegendJustification
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
}
