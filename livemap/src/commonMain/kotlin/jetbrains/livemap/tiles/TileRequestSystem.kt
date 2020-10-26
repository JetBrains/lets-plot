/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.spatial.computeRect
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.api.mapEntity
import jetbrains.livemap.cells.*
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.placement.*
import jetbrains.livemap.placement.ScreenLoopComponent.Rounding.FLOOR
import jetbrains.livemap.rendering.LayerEntitiesComponent
import jetbrains.livemap.rendering.Renderer
import jetbrains.livemap.rendering.RendererComponent
import jetbrains.livemap.projection.WorldRectangle
import jetbrains.livemap.tiles.RendererCacheComponent.Companion.NULL_RENDERER
import jetbrains.livemap.tiles.vector.TileLoadingSystem
import jetbrains.livemap.tiles.vector.debug.DebugCellRenderer

class TileRequestSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myMapRect: WorldRectangle
    private lateinit var myDonorTileCalculators: Map<CellLayerKind, DonorTileCalculator>

    override fun initImpl(context: LiveMapContext) {
        myMapRect = context.mapProjection.mapRect

        createEntity("tile_for_request").addComponents {
            + RequestTilesComponent()
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        myDonorTileCalculators = createDonorTileCalculators()

        val requestTiles = HashSet(
            getSingleton<CellStateComponent>().requestCells
        )

        getEntities(CellComponent::class).forEach { cellEntity ->
            requestTiles.remove(
                cellEntity.get<CellComponent>().cellKey
            )
        }

        requestTiles.forEach(::createTileLayerEntities)

        getSingleton<RequestTilesComponent>().requestTiles = requestTiles
    }

    private fun createDonorTileCalculators(): Map<CellLayerKind, DonorTileCalculator> {
        val layerTileMap = HashMap<CellLayerKind, MutableMap<CellKey, Tile>>()

        for (entity in getEntities(TileLoadingSystem.TILE_COMPONENT_LIST)) {
            if (entity.get<TileComponent>().nonCacheable) { continue } // don't use error tiles as donors
            val tile = entity.get<TileComponent>().tile ?: continue

            val layerKind = entity.get<KindComponent>().layerKind

            layerTileMap.getOrPut(layerKind, ::HashMap)[entity.get<CellComponent>().cellKey] = tile
        }

        return layerTileMap.mapValues {(_, tilesMap) -> DonorTileCalculator(tilesMap) }
    }

    private fun createTileLayerEntities(cellKey: CellKey) {
        val zoom = cellKey.length
        val tileRect = cellKey.computeRect(myMapRect)

        for (layer in getEntities(CellLayerComponent::class)) {
            val layerKind = layer.get<CellLayerComponent>().layerKind

            val tileLayerEntity =
                mapEntity(
                    componentManager,
                    ParentLayerComponent(layer.id),
                    "tile_${layerKind}_$cellKey"
                )
                    .addComponents {
                        + WorldOriginComponent(tileRect.origin)
                        + RendererComponent(NULL_RENDERER)
                        + ScreenLoopComponent().apply { rounding = FLOOR }
                        + ScreenOriginComponent()
                        + screenDimension {
                            dimension = WorldDimension2ScreenUpdateSystem.world2Screen(tileRect.dimension, zoom)
                        }
                        + CellComponent(cellKey)
                        + KindComponent(layerKind)
                        + renderCache {
                            renderer = getRenderer(layer)
                        }
                        + when {
                            layer.contains<DebugCellLayerComponent>() -> DebugDataComponent()
                            else -> TileComponent().apply { tile = calculateDonorTile(layerKind, cellKey) }
                        }
                    }

            layer.get<LayerEntitiesComponent>().add(tileLayerEntity.id)
        }
    }

    private fun getRenderer(layer: EcsEntity): Renderer = when {
        layer.contains(DebugCellLayerComponent::class) -> DebugCellRenderer()
        else -> TileRenderer()
    }

    private fun calculateDonorTile(layerKind: CellLayerKind, cellKey: CellKey): Tile? {
        return myDonorTileCalculators[layerKind]?.createDonorTile(cellKey)
    }

    private fun screenDimension(block: ScreenDimensionComponent.() -> Unit): ScreenDimensionComponent {
        return ScreenDimensionComponent().apply(block)
    }

    private fun renderCache(block: RendererCacheComponent.() -> Unit): RendererCacheComponent {
        return RendererCacheComponent().apply(block)
    }
}