/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutProvider

internal class LiveMapTileLayoutProvider : TileLayoutProvider {
    override fun createTopDownTileLayout(): TileLayout {
        return TILE_LAYOUT
    }

    override fun createInsideOutTileLayout(): TileLayout {
        return TILE_LAYOUT
    }

    companion object {
        private val TILE_LAYOUT: TileLayout = LiveMapTileLayout()
    }
}