/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap

import org.jetbrains.letsPlot.livemap.containers.LinkedList
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tile.CompositeTile
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tile.EmptyTile.Companion.EMPTY_TILE
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tile.SubTile
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import kotlin.collections.Map.Entry

internal class DonorTileCalculator(private val myExistedTiles: Map<CellKey, Tile>) {

    fun createDonorTile(cellKey: CellKey): Tile {
        val upDonor = findUpDonorTile(cellKey)
        val downDonor = findDownDonorTile(cellKey)

        return when {
            upDonor !== EMPTY_TILE && downDonor !== EMPTY_TILE -> CompositeTile().apply {
                add(upDonor, CellKey(""))
                add(downDonor, CellKey(""))
            }
            upDonor !== EMPTY_TILE -> upDonor
            downDonor !== EMPTY_TILE -> downDonor
            else -> EMPTY_TILE
        }
    }

    private fun findDownDonorTile(cellKey: CellKey): Tile {
        val downDonors = LinkedList<Entry<CellKey, Tile>>()

        myExistedTiles
            .filter { it.key.startsWith(cellKey) }
            .forEach { entry ->
                if (!downDonors.any { entry.key.startsWith(it.key) }) {
                    downDonors.removeAll { it.key.startsWith(entry.key) }
                    downDonors.append(entry)
                }
            }

        return if (downDonors.isNotEmpty()) {
            CompositeTile().apply {
                downDonors.forEach { (tileKey, tile) ->
                    add(tile, tileKey.subKey(cellKey))
                }
            }
        } else {
            EMPTY_TILE
        }
    }

    private fun findUpDonorTile(cellKey: CellKey): Tile {
        return myExistedTiles
            .filter { cellKey.startsWith(it.key) }
            .maxByOrNull { it.key.length }
            ?.let { SubTile(it.value, cellKey.subKey(it.key)) }
            ?: EMPTY_TILE
    }

    private fun CellKey.startsWith(other: CellKey): Boolean = this.key.startsWith(other.key)

    private fun CellKey.subKey(other: CellKey): CellKey =
        CellKey(this.key.substring(other.length))
}