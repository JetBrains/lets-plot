/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.cells.CellKey
import jetbrains.livemap.cells.CellLayerKind
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.rendering.Renderer


class StatisticsComponent : EcsComponent {

    val stats = HashMap<CellKey, HashMap<String, String>>()

    fun add(cellKey: CellKey, key: String, value: String) {
        stats.getOrPut(cellKey, ::HashMap)[key] = value
    }
}

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
    var nonCacheable: Boolean = false // for error tile
}

class RequestTilesComponent : EcsComponent {
    var requestTiles = HashSet<CellKey>()
}

