/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

// ToDo: internal
/*internal*/ class LegendBoxesLayout(
    private val outerBounds: DoubleRectangle,
    private val innerBounds: DoubleRectangle,
    private val theme: LegendTheme
) {

    fun doLayout(legendsBlockInfo: LegendsBlockInfo): LegendsBlockInfo {
        val legendPosition = theme.position()
        val legendJustification = theme.justification()
        val blockSize = legendsBlockInfo.size()

        val legendOrigin: DoubleVector = if (legendPosition.isFixed) {
            LegendBoxesLayoutUtil.overlayLegendOriginOutsidePlot(
                innerBounds,
                outerBounds,
                blockSize,
                legendPosition,
                legendJustification,
                theme.margin()
            )
        } else {
            LegendBoxesLayoutUtil.overlayLegendOriginInsidePlot(
                innerBounds,
                blockSize,
                legendPosition,
                legendJustification
            )
        }
        return legendsBlockInfo.moveAll(legendOrigin)
    }

    class BoxWithLocation internal constructor(val legendBox: LegendBoxInfo, val location: DoubleVector) {

        internal fun size(): DoubleVector {
            return legendBox.size
        }

        internal fun bounds(): DoubleRectangle {
            return DoubleRectangle(location, legendBox.size)
        }
    }
}
