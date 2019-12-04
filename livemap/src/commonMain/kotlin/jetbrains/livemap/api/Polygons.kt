/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.entities.Entities.MapEntityFactory
import jetbrains.livemap.entities.geocoding.MapIdComponent
import jetbrains.livemap.entities.geometry.WorldGeometryComponent
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.placement.ScreenOriginComponent
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.entities.regions.RegionFragmentsComponent
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
    val mapProjection: MapProjection
)

fun LayersBuilder.polygons(block: Polygons.() -> Unit) {

    val layerEntity =  myComponentManager
        .createEntity("map_layer_polygon")
        .addComponents {
            + layerManager.addLayer("geom_polygon", LayerGroup.FEATURES)
            + LayerEntitiesComponent()
        }

    Polygons(
        MapEntityFactory(layerEntity),
        mapProjection
    ).apply(block)
}

fun Polygons.polygon(block: PolygonsBuilder.() -> Unit) {
    PolygonsBuilder(factory, mapProjection)
        .apply(block)
        .build()
}

@LiveMapDsl
class PolygonsBuilder(
    private val myFactory: MapEntityFactory,
    private val myMapProjection: MapProjection
) {
    var index: Int = 0
    var mapId: String? = null

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 0.0
    var fillColor: Color = Color.GREEN

    var multiPolygon: MultiPolygon<LonLat>? = null

    fun build(): EcsEntity? {

        return when {
            multiPolygon != null -> createStaticEntity()
            mapId != null -> createDynamicEntity()
            else -> null
        }
    }

    private fun createStaticEntity(): EcsEntity {
        val geometry = multiPolygon!!
            .run { ProjectionUtil.transformMultiPolygon(this, myMapProjection::project) }

        val bbox = GeometryUtil.bbox(geometry) ?: error("")

        return myFactory
            .createMapEntity("map_ent_s_polygon")
            .addComponents {
                + RendererComponent(PolygonRenderer())
                + WorldOriginComponent(bbox.origin)
                + WorldGeometryComponent().apply { this.geometry = geometry }
                + WorldDimensionComponent(bbox.dimension)
                + ScreenLoopComponent()
                + ScreenOriginComponent()
                + ScaleComponent()
                + StyleComponent().apply {
                    setFillColor(this@PolygonsBuilder.fillColor)
                    setStrokeColor(this@PolygonsBuilder.strokeColor)
                    setStrokeWidth(this@PolygonsBuilder.strokeWidth)
                }
            }
    }

    private fun createDynamicEntity(): EcsEntity {
        return myFactory
            .createMapEntity("map_ent_d_polygon_$mapId")
            .addComponents {
                + MapIdComponent(mapId!!)
                + RegionFragmentsComponent()
                + StyleComponent().apply {
                    setFillColor(this@PolygonsBuilder.fillColor)
                    setStrokeColor(this@PolygonsBuilder.strokeColor)
                    setStrokeWidth(this@PolygonsBuilder.strokeWidth)
                }
                + RendererComponent(RegionRenderer())
                + ScreenLoopComponent().apply { origins = listOf(Coordinates.ZERO_CLIENT_POINT) }
                + ScreenOriginComponent()
            }
    }
}

fun PolygonsBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isGeodesic, isClosed = true)
}