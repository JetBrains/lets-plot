/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

/**
 * Represents a block of legends (one or more legend boxes arranged together)
 * that has been collected from subplots in a composite figure.
 *
 * Contains the arranged legends along with their position and justification metadata.
 *
 * TODO: merge with regular LegendBlockInfo.
 */
class CompositeLegendBlockInfo private constructor(
    val legendsBlockInfo: LegendsBlockInfo,
    val position: LegendPosition,
    val justification: LegendJustification
) {

    companion object {
        fun create(
            legendsInBlock: List<LegendBoxInfo>,
            theme: LegendTheme,
        ): CompositeLegendBlockInfo {
            check(legendsInBlock.isNotEmpty()) { "Legends block list is empty" }
            val position: LegendPosition = legendsInBlock[0].position
            val justification: LegendJustification = legendsInBlock[0].justification
            val legendsBlockInfo = LegendBoxesLayoutUtil.arrangeLegendBoxes(
                legendsInBlock,
                theme
            )
            return CompositeLegendBlockInfo(legendsBlockInfo, position, justification)
        }
    }
}
