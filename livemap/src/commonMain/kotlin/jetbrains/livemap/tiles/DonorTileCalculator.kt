package jetbrains.livemap.tiles

import jetbrains.livemap.core.LinkedList
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.Tile.CompositeTile
import jetbrains.livemap.tiles.Tile.EmptyTile.Companion.EMPTY_TILE
import jetbrains.livemap.tiles.Tile.SubTile
import kotlin.collections.Map.Entry

internal class DonorTileCalculator(private val myExistedTiles: Map<CellKey, Tile>) {

    fun createDonorTile(cellKey: CellKey): Tile {
        val upDonor = findUpDonorTile(cellKey)
        val downDonor = findDownDonorTile(cellKey)

        return if (upDonor !== EMPTY_TILE && downDonor !== EMPTY_TILE) {
            CompositeTile().apply {
                add(upDonor, "")
                add(downDonor, "")
            }
        } else if (upDonor !== EMPTY_TILE) {
            upDonor
        } else if (downDonor !== EMPTY_TILE) {
            downDonor
        } else {
            EMPTY_TILE
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
                    add(tile, tileKey.subKey(cellKey.length))
                }
            }
        } else {
            EMPTY_TILE
        }
    }

    private fun findUpDonorTile(cellKey: CellKey): Tile {
        return myExistedTiles
            .filter { cellKey.startsWith(it.key) }
            .maxBy { it.key.length }
            ?.let { SubTile(it.value, cellKey.subKey(it.key.length)) }
            ?: EMPTY_TILE
    }

    private fun CellKey.startsWith(other: CellKey): Boolean = this.key.startsWith(other.key)

    private fun CellKey.subKey(length: Int): String = this.key.substring(length)
}