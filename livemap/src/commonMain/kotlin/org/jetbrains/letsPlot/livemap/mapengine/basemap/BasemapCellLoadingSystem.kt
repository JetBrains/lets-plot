/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap

import org.jetbrains.letsPlot.commons.intern.spatial.computeRect
import org.jetbrains.letsPlot.livemap.api.mapEntity
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.RenderableComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapCellRendererComponent.Companion.NULL_RENDERER
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileLoadingSystem
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug.DebugCellRenderer
import org.jetbrains.letsPlot.livemap.mapengine.placement.ScreenDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridStateComponent

class BasemapCellLoadingSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myMapRect: org.jetbrains.letsPlot.livemap.WorldRectangle
    private lateinit var myDonorTileCalculators: Map<BasemapLayerKind, DonorTileCalculator>

    override fun initImpl(context: LiveMapContext) {
        myMapRect = context.mapProjection.mapRect

        createEntity("tile_for_request").addComponents {
            +RequestTilesComponent()
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myDonorTileCalculators = createDonorTileCalculators()

        val requestTiles = HashSet(
            getSingleton<ViewportGridStateComponent>().cellsToLoad
        )

        getEntities(BasemapCellComponent::class).forEach { cellEntity ->
            requestTiles.remove(
                cellEntity.get<BasemapCellComponent>().cellKey
            )
        }

        requestTiles.forEach(::createTileLayerEntities)

        getSingleton<RequestTilesComponent>().requestTiles = requestTiles
    }

    private fun createDonorTileCalculators(): Map<BasemapLayerKind, DonorTileCalculator> {
        val layerTileMap = HashMap<BasemapLayerKind, MutableMap<CellKey, Tile>>()

        for (entity in getEntities(TileLoadingSystem.TILE_COMPONENT_LIST)) {
            if (entity.get<BasemapTileComponent>().nonCacheable) {
                continue
            } // don't use error tiles as donors
            val tile = entity.get<BasemapTileComponent>().tile ?: continue

            val layerKind = entity.get<KindComponent>().layerKind

            layerTileMap.getOrPut(layerKind, ::HashMap)[entity.get<BasemapCellComponent>().cellKey] = tile
        }

        return layerTileMap.mapValues { (_, tilesMap) -> DonorTileCalculator(tilesMap) }
    }

    private fun createTileLayerEntities(cellKey: CellKey) {
        val zoom = cellKey.length
        val tileRect = cellKey.computeRect(myMapRect)

        for (layer in getEntities<BasemapLayerComponent>()) {
            val layerKind = layer.get<BasemapLayerComponent>().layerKind

            val tileLayerEntity =
                mapEntity(
                    componentManager,
                    ParentLayerComponent(layer.id),
                    "tile_${layerKind}_$cellKey"
                )
                    .addComponents {
                        +WorldOriginComponent(tileRect.origin)
                        +RenderableComponent().apply {
                            renderer = NULL_RENDERER
                            needIntegerCoordinates = true
                        }
                        +ScreenDimensionComponent().apply {
                            dimension = Viewport.toClientDimension(tileRect.dimension, zoom)
                        }
                        +BasemapCellComponent(cellKey)
                        +KindComponent(layerKind)
                        +BasemapCellRendererComponent().apply {
                            renderer = when (layer.contains<DebugCellLayerComponent>()) {
                                true -> DebugCellRenderer()
                                false -> BasemapCellRenderer()
                            }
                        }
                        +when (layer.contains<DebugCellLayerComponent>()) {
                            true -> DebugDataComponent()
                            false -> BasemapTileComponent().apply {
                                tile = calculateDonorTile(layerKind, cellKey)
                            }
                        }
                    }

            layer.get<LayerEntitiesComponent>().add(tileLayerEntity.id)
        }
    }

    private fun calculateDonorTile(layerKind: BasemapLayerKind, cellKey: CellKey): Tile? {
        return myDonorTileCalculators[layerKind]?.createDonorTile(cellKey)
    }
}
