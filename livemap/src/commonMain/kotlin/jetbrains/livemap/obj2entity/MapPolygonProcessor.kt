/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.obj2entity

import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.Entities
import jetbrains.livemap.entities.geometry.Renderers.PolygonRenderer
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.geometry.toWorldBoundary
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.regions.RegionComponent
import jetbrains.livemap.entities.regions.RegionRenderer
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.mapobjects.MapObject
import jetbrains.livemap.mapobjects.MapPolygon
import jetbrains.livemap.projections.Coordinates.Companion.ZERO_CLIENT_POINT
import jetbrains.livemap.projections.MapProjection

internal class MapPolygonProcessor(
    componentManager: EcsComponentManager,
    layerManager: LayerManager,
    private val myMapProjection: MapProjection
) {
    private val myFactory: Entities.MapEntityFactory
    private val myLayerEntitiesComponent = LayerEntitiesComponent()

    init {
        componentManager
            .createEntity("map_layer_polygon")
            .addComponent(layerManager.createRenderLayerComponent("geom_polygon"))
            .addComponent(myLayerEntitiesComponent)
            .run { myFactory = Entities.MapEntityFactory(this) }
    }

    fun process(mapObjects: List<MapObject>) {
        for (mapObject in mapObjects) {
            val mapPolygon = mapObject as MapPolygon

            when {
                mapPolygon.geometry != null -> createStaticEntity(mapPolygon)
                mapPolygon.regionId != null -> createDynamicEntity(mapPolygon)
                else -> {
                    // do not create entities for empty geometries
                }
            }
        }
    }

    private fun createStaticEntity(mapPolygon: MapPolygon) {
        val geometry = mapPolygon.geometry!!.toWorldBoundary(myMapProjection)
        val bbox = GeometryUtil.bbox(geometry.asMultipolygon()) ?: error("")

        val geometryEntity = myFactory
            .createMapEntity(bbox.origin, PolygonRenderer(), "map_ent_spolygon")
            .addComponent(WorldGeometryComponent().apply { this.geometry = geometry } )
            .addComponent(WorldDimensionComponent(bbox.dimension))
            .addComponent(ScaleComponent())
            .addComponent(
                StyleComponent().apply {
                    setFillColor(mapPolygon.fillColor)
                    setStrokeColor(mapPolygon.strokeColor)
                    setStrokeWidth(mapPolygon.strokeWidth)
                }
            )

        myLayerEntitiesComponent.add(geometryEntity.id)
    }

    private fun createDynamicEntity(mapPolygon: MapPolygon) {
        val regionEntity = myFactory
            .createDynamicMapEntity("map_ent_dpolygon_" + mapPolygon.regionId, RegionRenderer())
            .addComponent(RegionComponent().apply { id = mapPolygon.regionId })
            .addComponent(
                StyleComponent().apply {
                    setFillColor(mapPolygon.fillColor)
                    setStrokeColor(mapPolygon.strokeColor)
                    setStrokeWidth(mapPolygon.strokeWidth)
                }
            )

        regionEntity.get<ScreenLoopComponent>().origins = listOf(ZERO_CLIENT_POINT)
        myLayerEntitiesComponent.add(regionEntity.id)
    }
}