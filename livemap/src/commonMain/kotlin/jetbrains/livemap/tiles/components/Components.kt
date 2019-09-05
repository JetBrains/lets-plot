package jetbrains.livemap.tiles.components

import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.Utils
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.CellKey
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

    private val myStats = HashMap<CellKey, HashMap<String, String>>()

    fun add(cellKey: CellKey, key: String, value: String) {
        myStats.getOrPut(cellKey, ::HashMap)[key] = value
    }

    operator fun contains(cellKey: CellKey): Boolean {
        return myStats.containsKey(cellKey)
    }

    operator fun get(cellKey: CellKey): Map<String, String>? {
        return myStats[cellKey]
    }

    internal fun remove(cellKey: CellKey) {
        myStats.remove(cellKey)
    }

    companion object {
        fun get(entity: EcsEntity): StatisticsComponent {
            return entity.getComponent()
        }
    }
}

class CellComponent(val cellKey: CellKey) : EcsComponent {
    companion object {
        fun getCellKey(entity: EcsEntity): CellKey {
            return entity.getComponent<CellComponent>().cellKey
        }
    }
}

class DebugDataComponent : EcsComponent {

    private val myData = HashMap<String, String>()

    fun getLine(key: String): String {
        return key + myData[key]
    }

    companion object {
        private val lock = Lock()
        const val PARSING_TIME = "Parsing time: "
        val WORLD_RENDER_TIME = "Render time ${CellLayerKind.WORLD}: "
        val LABEL_RENDER_TIME = "Render time ${CellLayerKind.LABEL}: "
        val WORLD_SNAPSHOT_TIME = "Snapshot time ${CellLayerKind.WORLD}: "
        val LABEL_SNAPSHOT_TIME = "Snapshot time ${CellLayerKind.LABEL}: "
        const val LOADING_TIME = "Loading time: "
        const val CELL_DATA_SIZE = "Cell data size: "
        const val BIGGEST_LAYER = "BL: "

        internal fun addData(entity: EcsEntity, data: Map<String, String>) {
            lock.execute {
                val cellData = entity.getComponent<DebugDataComponent>().myData

                data.entries.forEach { cellData[it.key] = it.value }
            }
        }
    }
}

class KindComponent(val layerKind: CellLayerKind) : EcsComponent {
    companion object {
        fun getLayerKind(entity: EcsEntity): CellLayerKind {
            return entity.getComponent<KindComponent>().layerKind
        }
    }
}

class CellLayerComponent(val kind: CellLayerKind) : EcsComponent {
    companion object {

        operator fun get(entity: EcsEntity): CellLayerComponent {
            return entity.getComponent()
        }

        fun getKind(entity: EcsEntity): CellLayerKind {
            return get(entity).kind
        }
    }
}

class DebugCellLayerComponent : EcsComponent

class RendererCacheComponent : EcsComponent {

    var renderer: Renderer = NULL_RENDERER

    companion object {

        val NULL_RENDERER = object : Renderer {
            override fun render(entity: EcsEntity, ctx: Context2d) {}
        }

        internal operator fun get(entity: EcsEntity): RendererCacheComponent {
            return entity.getComponent()
        }

        fun getRenderer(entity: EcsEntity): Renderer {
            return get(entity).renderer
        }
    }
}

class TileResponseComponent : EcsComponent {

    private val lock = Lock()
    private var myTileData: List<TileLayer>? = null

    var tileData: List<TileLayer>?
        get() = lock.execute {
            return myTileData
        }
        set(tileData) = lock.execute {
            myTileData = tileData
        }

    companion object {
        fun get(entity: EcsEntity): TileResponseComponent {
            return entity.getComponent()
        }

        fun getTileData(entity: EcsEntity): List<TileLayer>? {
            return get(entity).tileData
        }
    }
}

class TileComponent : EcsComponent {

    private var myTile: Tile? = null

    internal fun getTile(): Tile? {
        return myTile
    }

    fun setTile(tile: Tile?): TileComponent {
        myTile = tile
        return this
    }

    companion object {
        fun get(entity: EcsEntity): TileComponent {
            return entity.getComponent()
        }

        fun getTile(entity: EcsEntity): Tile? {
            return get(entity).getTile()
        }

        fun setTile(entity: EcsEntity, tile: Tile) {
            get(entity).setTile(tile)
        }
    }
}

enum class CellLayerKind constructor(private val myValue: String) {
    WORLD("world"),
    LABEL("label"),
    DEBUG("debug");

    override fun toString(): String {
        return myValue
    }
}

internal enum class CellStatus {
    EMPTY,
    LOADING,
    LOADED,
    RENDERING,
    READY
}
