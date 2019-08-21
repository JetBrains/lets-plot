package jetbrains.livemap.tiles

import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.livemap.core.LinkedList

interface Tile

class SnapshotTile(val snapshot: Canvas.Snapshot) : Tile

class SubTile(val tile: Tile, val subKey: String) : Tile

class CompositeTile : Tile {
    private val myTiles = LinkedList<Pair<Tile, String>>()

    val tiles: List<Pair<Tile, String>>
        get() = myTiles.toList()

    fun add(tile: Tile, subKey: String) {
        myTiles.append(Pair(tile, subKey))
    }
}

class EmptyTile : Tile {
    companion object {
        val EMPTY_TILE = EmptyTile()
    }
}