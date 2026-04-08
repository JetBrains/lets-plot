/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.spec.Option.SubPlots

/**
 * Parses scale sharing properties from a deck layout options map.
 */
class DeckScaleShareConfig(
    layoutOpts: OptionsAccessor
) {
    val shareX: Boolean
    val shareY: Boolean
    val hasSharing: Boolean

    init {
        val scaleShare = layoutOpts.getString(SubPlots.Deck.SCALE_SHARE)?.lowercase()
            ?: SubPlots.Deck.ScaleShare.X

        val (x, y) = when (scaleShare) {
            SubPlots.Deck.ScaleShare.X -> true to false
            SubPlots.Deck.ScaleShare.Y -> false to true
            SubPlots.Deck.ScaleShare.ALL -> true to true
            SubPlots.Deck.ScaleShare.NONE -> false to false
            else -> throw IllegalArgumentException(
                "'scale_share'='$scaleShare'. Use: 'x', 'y', 'all', or 'none'."
            )
        }
        shareX = x
        shareY = y
        hasSharing = x || y
    }

    companion object {
        /**
         * Parses deck scale sharing config from a composite figure spec.
         * Returns null if the spec doesn't have a deck layout.
         */
        fun fromCompositeFigureSpec(compositeSpec: Map<String, Any>): DeckScaleShareConfig? {
            @Suppress("UNCHECKED_CAST")
            val layoutOptions = compositeSpec[SubPlots.LAYOUT] as? Map<String, Any> ?: return null
            val layoutName = layoutOptions[SubPlots.Layout.NAME] as? String
            if (layoutName != SubPlots.Layout.SUBPLOTS_DECK) return null

            return DeckScaleShareConfig(OptionsAccessor(layoutOptions))
        }
    }
}
