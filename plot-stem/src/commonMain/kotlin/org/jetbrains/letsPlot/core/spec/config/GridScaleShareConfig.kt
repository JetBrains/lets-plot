/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite.ScaleSharePolicy
import org.jetbrains.letsPlot.core.spec.Option.SubPlots
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_ALL
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_COL
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_NONE
import org.jetbrains.letsPlot.core.spec.Option.SubPlots.Grid.Scales.SHARE_ROW

/**
 * Parses scale sharing properties from a grid layout options map.
 */
class GridScaleShareConfig(
    layoutOpts: OptionsAccessor
) {
    val shareX: ScaleSharePolicy = parseSharePolicy(SubPlots.Grid.SHARE_X_SCALE, layoutOpts)
    val shareY: ScaleSharePolicy = parseSharePolicy(SubPlots.Grid.SHARE_Y_SCALE, layoutOpts)
    val ncols: Int = layoutOpts.getIntegerSafe(SubPlots.Grid.NCOLS)

    val hasSharing: Boolean = shareX != ScaleSharePolicy.NONE || shareY != ScaleSharePolicy.NONE

    companion object {
        /**
         * Parses scale sharing config from a composite figure spec.
         * Returns null if the spec doesn't have a grid layout.
         */
        fun fromCompositeFigureSpec(compositeSpec: Map<String, Any>): GridScaleShareConfig? {
            @Suppress("UNCHECKED_CAST")
            val layoutOptions = compositeSpec[SubPlots.LAYOUT] as? Map<String, Any> ?: return null
            val layoutName = layoutOptions[SubPlots.Layout.NAME] as? String
            if (layoutName != SubPlots.Layout.SUBPLOTS_GRID) return null

            return GridScaleShareConfig(OptionsAccessor(layoutOptions))
        }

        private fun parseSharePolicy(option: String, layoutOpts: OptionsAccessor): ScaleSharePolicy {
            return layoutOpts.get(option)?.let {
                when (it.toString().lowercase()) {
                    SHARE_NONE -> ScaleSharePolicy.NONE
                    SHARE_ALL -> ScaleSharePolicy.ALL
                    SHARE_ROW -> ScaleSharePolicy.ROW
                    SHARE_COL -> ScaleSharePolicy.COL
                    else -> throw IllegalArgumentException("Unexpected value: '$option = $it'. Use: 'all', 'row', 'col' or 'none'")
                }
            } ?: ScaleSharePolicy.NONE
        }
    }
}
