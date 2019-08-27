package jetbrains.livemap.tiles

import jetbrains.livemap.core.LinkedList
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.EmptyTile.Companion.EMPTY_TILE
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

        for (entry in myExistedTiles) {
            val tileKey = entry.key

            if (!tileKey.startsWith(cellKey)) {
                continue
            }

            if (!downDonors.any { tileKey.startsWith(it.key) }) {
                downDonors.removeAll { it.key.startsWith(tileKey) }
                downDonors.append(entry)
            }
        }

        if (downDonors.isEmpty()) {
            return EMPTY_TILE
        }

        return CompositeTile().apply {
            downDonors.forEach {
                add(it.value, it.key.subKey(cellKey.length))
            }
        }
    }

    private fun findUpDonorTile(cellKey: CellKey): Tile {
        var upDonor: Entry<CellKey, Tile>? = null

        for (entry in myExistedTiles) {
            val tileKey = entry.key

            if (cellKey.startsWith(tileKey) && (upDonor == null || tileKey.length > upDonor.key.length)) {
                upDonor = entry
            }
        }

        if (upDonor == null) {
            return EMPTY_TILE
        }

        return SubTile(upDonor.value, cellKey.subKey(upDonor.key.length))
    }

    private fun CellKey.startsWith(other: CellKey): Boolean = this.key.startsWith(other.key)

    private fun CellKey.subKey(length: Int): String = this.key.substring(length)

    private val CellKey.length: Int
        get() = key.length
}