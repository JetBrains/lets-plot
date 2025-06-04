/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.intern.concurrent.Lock
import org.jetbrains.letsPlot.commons.intern.concurrent.execute
import org.jetbrains.letsPlot.commons.intern.math.round
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import org.jetbrains.letsPlot.gis.tileprotocol.TileService
import org.jetbrains.letsPlot.livemap.core.BusyStateComponent
import org.jetbrains.letsPlot.livemap.core.ecs.*
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.*
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.basemap.*
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapLayerKind.LABEL
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapLayerKind.WORLD
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tile.SnapshotTile
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug.DebugTileDataFetcher
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug.DebugTileDataParser
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug.DebugTileDataRenderer
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS

class TileLoadingSystem(
    private val myQuantumIterations: Int,
    private val myTileService: TileService,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myMapRect: org.jetbrains.letsPlot.livemap.WorldRectangle
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
                    + BasemapCellComponent(cellKey)
                    + tileResponseComponent
                    + BusyStateComponent()
                }

            myTileDataFetcher.fetch(cellKey).onResult(
                { tileResponseComponent.tileData = it },
                { tileResponseComponent.tileData = emptyList() }
            )
        }

        val downloadedEntities = ArrayList<EcsEntity>()
        onEachEntity<TileResponseComponent>() { entity, responseComponent ->
            val tileData = responseComponent.tileData ?: return@onEachEntity

            downloadedEntities.add(entity)
            entity.remove<BusyStateComponent>()

            val cellKey = entity.get<BasemapCellComponent>().cellKey
            val tileEntities = getTileLayerEntities(cellKey)

            entity.setMicroThread(myQuantumIterations, myTileDataParser
                .parse(cellKey, tileData)
                .flatMap { tileFeatures ->
                    val microThreads = ArrayList<MicroTask<Unit>>()
                    tileEntities.forEach { tileLayerEntity ->
                        tileLayerEntity.add(BusyStateComponent())
                            val canvas = myCanvasSupplier()
                            val tileLoadingTask = myTileDataRenderer
                                .render(canvas, tileFeatures, cellKey, tileLayerEntity.get<KindComponent>().layerKind)
                                .map {
                                    runLaterBySystem(tileLayerEntity) { theEntity ->
                                        val snapshot = canvas.takeSnapshot()
                                        theEntity.get<BasemapTileComponent>().tile = SnapshotTile(snapshot)
                                        theEntity.remove<BusyStateComponent>()
                                        ParentLayerComponent.tagDirtyParentLayer(theEntity)
                                    }
                                    return@map
                                }

                            microThreads.add(tileLoadingTask)
                    }
                    MicroTaskUtil.join(microThreads)
                }
            )
        }

        downloadedEntities.forEach { it.remove<TileResponseComponent>() }
    }

    private fun getTileLayerEntities(cellKey: CellKey): Sequence<EcsEntity> {
        return getEntities3<BasemapCellComponent, KindComponent, BasemapTileComponent>().filter { entity ->
            entity.get<BasemapCellComponent>().cellKey == cellKey &&
                    entity.get<KindComponent>().layerKind in setOf(WORLD, LABEL)
        }
    }

    companion object {

        val TILE_COMPONENT_LIST = listOf(
            BasemapCellComponent::class,
            BasemapTileComponent::class
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
