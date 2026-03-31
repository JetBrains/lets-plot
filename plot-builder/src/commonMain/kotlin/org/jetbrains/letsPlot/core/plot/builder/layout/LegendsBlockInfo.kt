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

/**
 * Represents a block of legends (one or more legend boxes arranged together).
 */
class LegendsBlockInfo private constructor(
    val boxWithLocationList: List<LegendBoxesLayout.BoxWithLocation>,
) {
    val position: LegendPosition = boxWithLocationList.first().legendBox.position
    val justification: LegendJustification = boxWithLocationList.first().legendBox.justification

    fun size(): DoubleVector {
        var bounds: DoubleRectangle? = null
        for (boxWithLocation in boxWithLocationList) {
            bounds = bounds?.union(boxWithLocation.bounds()) ?: boxWithLocation.bounds()
        }
        return bounds?.dimension ?: DoubleVector.ZERO
    }

    fun moveAll(delta: DoubleVector): LegendsBlockInfo {
        val newList = boxWithLocationList.map {
            LegendBoxesLayout.BoxWithLocation(it.legendBox, it.location.add(delta))
        }
        return LegendsBlockInfo(newList)
    }

    companion object {
        fun arrangeLegendBoxes(
            legendsInBlock: List<LegendBoxInfo>,
            theme: LegendTheme,
        ): LegendsBlockInfo {
            check(legendsInBlock.isNotEmpty()) { "Legends in block list is empty" }

            // Remove duplicated legends
            val infos = mutableListOf<LegendBoxInfo>()
            for (legend in legendsInBlock) {
                val isDuplicate = infos.any { it.hasSameContent(legend) }
                if (!isDuplicate) {
                    infos.add(legend)
                }
            }

            val boxWithLocationList = LegendBoxesLayoutUtil.arrangeLegendBoxes(infos, theme)
            return LegendsBlockInfo(boxWithLocationList)
        }
    }
}
