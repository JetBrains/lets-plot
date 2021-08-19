/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap

import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.containers.LinkedList

interface Tile {
    class SnapshotTile(val snapshot: Canvas.Snapshot) : Tile

    class SubTile(val tile: Tile, val subKey: CellKey) : Tile

    class CompositeTile : Tile {
        val tiles = LinkedList<Pair<Tile, CellKey>>()

        fun add(tile: Tile, subKey: CellKey) {
            tiles.append(Pair(tile, subKey))
        }
    }

    class EmptyTile : Tile {
        companion object {
            val EMPTY_TILE = EmptyTile()
        }
    }
}

