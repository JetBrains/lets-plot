/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider

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