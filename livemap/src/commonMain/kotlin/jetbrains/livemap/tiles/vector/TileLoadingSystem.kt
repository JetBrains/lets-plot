/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.vector

import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.math.round
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.*
import jetbrains.livemap.core.multitasking.*
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.projections.WorldRectangle
import jetbrains.livemap.tiles.CellKey
import jetbrains.livemap.tiles.CellStateUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.tiles.Tile.SnapshotTile
import jetbrains.livemap.tiles.components.*
import jetbrains.livemap.tiles.components.CellLayerKind.LABEL
import jetbrains.livemap.tiles.components.CellLayerKind.WORLD
import jetbrains.livemap.tiles.debug.DebugTileDataFetcher
import jetbrains.livemap.tiles.debug.DebugTileDataParser
import jetbrains.livemap.tiles.debug.DebugTileDataRenderer

class TileLoadingSystem(
    private val myQuantumIterations: Int,
    private val myTileService: TileService,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myMapRect: WorldRectangle
    private lateinit var myCanvasSupplier: () -> Canvas
    private lateinit var myTileDataFetcher: TileDataFetcher
    private lateinit var myTileDataParser: TileDataParser
    private lateinit var myTileDataRenderer: TileDataRenderer

    override fun initImpl(context: LiveMapContext) {
        myMapRect = context.mapProjection.mapRect
        val dimension = round(myMapRect.dimension.x, myMapRect.dimension.y)
        myCanvasSupplier = { context.mapRenderContext.canvasProvider.createCanvas(dimension) }

        myTileDataFetcher = TileDataFetcherImpl(context.mapProjection, myTileService)
        myTileDataParser = TileDataParserImpl(context.mapProjection)
        myTileDataRenderer = TileDataRendererImpl(myTileService::mapConfig)

        run {
            // enable debug stats
            val stats: StatisticsComponent = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS).get()
            myTileDataFetcher = DebugTileDataFetcher(stats, context.systemTime, myTileDataFetcher)
            myTileDataParser = DebugTileDataParser(stats, context.systemTime, myTileDataParser)
            myTileDataRenderer = DebugTileDataRenderer(stats, context.systemTime, myTileDataRenderer)
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        getSingleton<RequestTilesComponent>().requestTiles.forEach { cellKey ->
            val tileResponseComponent = TileResponseComponent()

            createEntity("tile_$cellKey")
                .addComponents {
                    + CellComponent(cellKey)
                    + tileResponseComponent
                }

            myTileDataFetcher.fetch(cellKey).onResult(
                { tileResponseComponent.tileData = it },
                { tileResponseComponent.tileData = emptyList() }
            )
        }

        val downloadedEntities = ArrayList<EcsEntity>()
        for (entity in getEntities<TileResponseComponent>()) {
            val tileData = entity.get<TileResponseComponent>().tileData ?: continue
            downloadedEntities.add(entity)

            val cellKey = entity.get<CellComponent>().cellKey
            val tileEntities = getTileLayerEntities(cellKey)

            entity.setMicroThread(myQuantumIterations, myTileDataParser
                .parse(cellKey, tileData)
                .flatMap { tileFeatures ->
                    val microThreads = ArrayList<MicroTask<Unit>>()
                    tileEntities.forEach { tileLayerEntity ->
                        microThreads.add(
                            myTileDataRenderer
                                .render(myCanvasSupplier(), tileFeatures, cellKey, tileLayerEntity.get<KindComponent>().layerKind)
                                .map { snapshotAsync ->
                                    snapshotAsync.onSuccess { snapshot ->
                                        runLaterBySystem(tileLayerEntity) { theEntity ->
                                            theEntity.get<TileComponent>().tile = SnapshotTile(snapshot)
                                            ParentLayerComponent.tagDirtyParentLayer(theEntity)
                                        }
                                    }
                                    return@map
                                }
                        )
                    }
                    MicroTaskUtil.join(microThreads)
                })
        }

        downloadedEntities.forEach { it.remove<TileResponseComponent>() }
    }

    private fun getTileLayerEntities(cellKey: CellKey): Sequence<EcsEntity> {
        return getEntities(CELL_COMPONENT_LIST)
            .filter {
                it.get<CellComponent>().cellKey == cellKey
                        && it.get<KindComponent>().layerKind in setOf(WORLD, LABEL)
            }
    }

    companion object {
        val CELL_COMPONENT_LIST = listOf(
            CellComponent::class,
            KindComponent::class
        )

        val TILE_COMPONENT_LIST = listOf(
            CellComponent::class,
            KindComponent::class,
            TileComponent::class
        )
    }

    class TileResponseComponent : EcsComponent {

        private val myLock = Lock()
        private var myTileData: List<TileLayer>? = null

        var tileData: List<TileLayer>?
            get() = myLock.execute {
                return myTileData
            }
            set(tileData) = myLock.execute {
                myTileData = tileData
            }
    }
}
