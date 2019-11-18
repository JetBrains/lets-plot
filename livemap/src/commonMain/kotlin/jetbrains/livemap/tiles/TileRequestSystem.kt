/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.spatial.computeRect
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.placement.WorldDimension2ScreenUpdateSystem
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.projections.WorldRectangle
import jetbrains.livemap.tiles.components.*
import jetbrains.livemap.tiles.debug.DebugCellRenderer
import jetbrains.livemap.tiles.vector.TileLoadingSystem

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

            val parentLayerComponent = ParentLayerComponent(layer.id)
            val name = "tile_${layerKind}_$cellKey"
            val tileLayerEntity =
                Entities.mapEntity(
                    componentManager,
                    tileRect.origin,
                    parentLayerComponent,
                    RendererCacheComponent.NULL_RENDERER,
                    name
                )
                    .addComponents {
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