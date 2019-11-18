/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.components

import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.Utils
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.tiles.CellKey
import jetbrains.livemap.tiles.Tile

class CellStateComponent : EcsComponent {
    var visibleCells: Set<CellKey> = HashSet()
    var requestCells: Set<CellKey> = HashSet()
    var cellsToRemove: Set<CellKey> = HashSet()

    val quadsRefCounter: HashMap<QuadKey, Int> = HashMap()
    var quadsToAdd: Set<QuadKey> = HashSet()
    var quadsToRemove: Set<QuadKey> = HashSet()

    val visibleQuads: Set<QuadKey>
        get() = quadsRefCounter.keys

    fun update(newVisibleCells: Set<CellKey>) {
        val existingCells = visibleCells

        visibleCells = newVisibleCells
        requestCells = Utils.diff(visibleCells, existingCells)
        cellsToRemove = Utils.diff(existingCells, visibleCells)
    }
}

class StatisticsComponent : EcsComponent {

    val stats = HashMap<CellKey, HashMap<String, String>>()

    fun add(cellKey: CellKey, key: String, value: String) {
        stats.getOrPut(cellKey, ::HashMap)[key] = value
    }
}

class CellComponent(val cellKey: CellKey) : EcsComponent

class DebugDataComponent : EcsComponent {
    private val myData = HashMap<String, String>()

    fun get(key: String) = myData[key]

    internal fun addData(data: Map<String, String>) {
        myData.putAll(data)
    }

    companion object {
        fun renderTimeKey(layerKind: CellLayerKind) = "Render time $layerKind"
        fun snapshotTimeKey(layerKind: CellLayerKind) = "Snapshot time $layerKind"

        const val PARSING_TIME = "Parsing time"
        const val LOADING_TIME = "Loading time"
        const val CELL_DATA_SIZE = "Cell data size"
        const val BIGGEST_LAYER = "BL"

        val LINES_ORDER = listOf(
            CELL_DATA_SIZE,
            LOADING_TIME,
            PARSING_TIME,
            BIGGEST_LAYER,
            renderTimeKey(CellLayerKind.WORLD),
            snapshotTimeKey(CellLayerKind.WORLD),
            renderTimeKey(CellLayerKind.LABEL),
            snapshotTimeKey(CellLayerKind.LABEL)
        )
    }
}

class KindComponent(val layerKind: CellLayerKind) : EcsComponent

class CellLayerComponent(val layerKind: CellLayerKind) : EcsComponent

class DebugCellLayerComponent : EcsComponent

class RendererCacheComponent : EcsComponent {

    var renderer: Renderer = NULL_RENDERER

    companion object {
        val NULL_RENDERER = object : Renderer {
            override fun render(entity: EcsEntity, ctx: Context2d) {}
        }
    }
}

class TileComponent : EcsComponent {
    var tile: Tile? = null
}

class RequestTilesComponent : EcsComponent {
    var requestTiles = HashSet<CellKey>()
}

enum class CellLayerKind constructor(private val myValue: String) {
    WORLD("world"),
    LABEL("label"),
    DEBUG("debug"),
    RASTER("raster_tile");

    override fun toString(): String {
        return myValue
    }
}