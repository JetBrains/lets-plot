/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap

import org.jetbrains.letsPlot.core.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.viewport.CellKey


/**
 *For any entity. Automatotically destroys the entity if cell is not in a viewport.
 */
class BasemapCellComponent(val cellKey: CellKey) : EcsComponent

// For tile entity
class KindComponent(val layerKind: BasemapLayerKind) : EcsComponent

// For layer entity
class BasemapLayerComponent(val layerKind: BasemapLayerKind) : EcsComponent

// Contains tile data (pixels or subtiles or anything)
class BasemapTileComponent : EcsComponent {
    var tile: Tile? = null
    var nonCacheable: Boolean = false // for error tile
}

// Stores render for tiles. BasemapCellsRemovingSystem uses this component
// to turn on/off tiles rendering depending on zoom in regular RendererComonent
class BasemapCellRendererComponent : EcsComponent {
    var renderer: Renderer = NULL_RENDERER

    companion object {
        val NULL_RENDERER = object : Renderer {
            override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {}
        }
    }
}

class DebugCellLayerComponent : EcsComponent

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
        fun renderTimeKey(layerKind: BasemapLayerKind) = "Render time $layerKind"
        fun snapshotTimeKey(layerKind: BasemapLayerKind) = "Snapshot time $layerKind"

        const val PARSING_TIME = "Parsing time"
        const val LOADING_TIME = "Loading time"
        const val CELL_DATA_SIZE = "Cell data size"
        const val BIGGEST_LAYER = "BL"

        val LINES_ORDER = listOf(
            CELL_DATA_SIZE,
            LOADING_TIME,
            PARSING_TIME,
            BIGGEST_LAYER,
            renderTimeKey(BasemapLayerKind.WORLD),
            snapshotTimeKey(BasemapLayerKind.WORLD),
            renderTimeKey(BasemapLayerKind.LABEL),
            snapshotTimeKey(BasemapLayerKind.LABEL)
        )
    }
}

class RequestTilesComponent : EcsComponent {
    var requestTiles = HashSet<CellKey>()
}

