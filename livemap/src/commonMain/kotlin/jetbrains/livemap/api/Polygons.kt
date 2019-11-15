/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.regions.RegionComponent
import jetbrains.livemap.entities.regions.RegionRenderer
import jetbrains.livemap.entities.rendering.*
import jetbrains.livemap.entities.rendering.Renderers.PolygonRenderer
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.projections.Coordinates
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil


@LiveMapDsl
class Polygons(
    val factory: MapEntityFactory,
    val layerEntitiesComponent: LayerEntitiesComponent,
    val mapProjection: MapProjection
)

fun LayersBuilder.polygons(block: Polygons.() -> Unit) {
    val layerEntitiesComponent = LayerEntitiesComponent()

    val layerEntity =  myComponentManager
        .createEntity("map_layer_polygon")
        .addComponents {
            + layerManager.addLayer("geom_polygon", LayerGroup.FEATURES)
            + layerEntitiesComponent
        }

    Polygons(
        MapEntityFactory(layerEntity),
        layerEntitiesComponent,
        mapProjection
    ).apply(block)
}

fun Polygons.polygon(block: PolygonsBuilder.() -> Unit) {
    PolygonsBuilder()
        .apply(block)
        .build(factory, mapProjection)
        ?.let { polygonEntity ->
            layerEntitiesComponent.add(polygonEntity.id)
        }
}

@LiveMapDsl
class PolygonsBuilder {
    var index: Int = 0
    var mapId: String = ""
    var regionId: String? = null

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 0.0
    var fillColor: Color = Color.GREEN

    var multiPolygon: MultiPolygon<LonLat>? = null

    fun build(
        factory: MapEntityFactory,
        mapProjection: MapProjection
    ): EcsEntity? {

        return when {
            multiPolygon != null -> createStaticEntity(factory, mapProjection)
            regionId != null -> createDynamicEntity(factory)
            else -> null
        }
    }

    private fun createStaticEntity(factory: MapEntityFactory, mapProjection: MapProjection): EcsEntity {
        val geometry = multiPolygon!!
            .run { ProjectionUtil.transformMultiPolygon(this, mapProjection::project) }

        val bbox = GeometryUtil.bbox(geometry) ?: error("")

        return factory
            .createMapEntity(bbox.origin, PolygonRenderer(), "map_ent_s_polygon")
            .addComponents {
                + WorldGeometryComponent().apply { this.geometry = geometry }
                + WorldDimensionComponent(bbox.dimension)
                + ScaleComponent()
                + StyleComponent().apply {
                    setFillColor(this@PolygonsBuilder.fillColor)
                    setStrokeColor(this@PolygonsBuilder.strokeColor)
                    setStrokeWidth(this@PolygonsBuilder.strokeWidth)
                }
            }
    }

    private fun createDynamicEntity(factory: MapEntityFactory): EcsEntity {
        return factory
            .createDynamicMapEntity("map_ent_d_polygon_" + this@PolygonsBuilder.regionId, RegionRenderer())
            .addComponents {
                + RegionComponent().apply { id = this@PolygonsBuilder.regionId }
                + StyleComponent().apply {
                    setFillColor(this@PolygonsBuilder.fillColor)
                    setStrokeColor(this@PolygonsBuilder.strokeColor)
                    setStrokeWidth(this@PolygonsBuilder.strokeWidth)
                }
            }.apply {
                get<ScreenLoopComponent>().origins = listOf(Coordinates.ZERO_CLIENT_POINT)
            }
    }
}

fun PolygonsBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isGeodesic, isClosed = true)
}