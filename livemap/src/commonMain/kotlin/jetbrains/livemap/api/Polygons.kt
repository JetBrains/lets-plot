/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.GeometryUtil
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.projections.ProjectionUtil
import jetbrains.livemap.core.rendering.layers.LayerGroup
import jetbrains.livemap.geocoding.*
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.placement.ScreenOriginComponent
import jetbrains.livemap.placement.WorldDimensionComponent
import jetbrains.livemap.placement.WorldOriginComponent
import jetbrains.livemap.projection.Coordinates
import jetbrains.livemap.projection.MapProjection
import jetbrains.livemap.regions.RegionFragmentsComponent
import jetbrains.livemap.regions.RegionRenderer
import jetbrains.livemap.rendering.*
import jetbrains.livemap.rendering.Renderers.PolygonRenderer
import jetbrains.livemap.scaling.ScaleComponent
import jetbrains.livemap.searching.IndexComponent
import jetbrains.livemap.searching.LocatorComponent
import jetbrains.livemap.searching.PolygonLocatorHelper

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
    var layerIndex: Int? = null
    var index: Int? = null
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
                if (layerIndex != null && index != null) {
                    + IndexComponent(layerIndex!!, index!!)
                }
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
                + NeedLocationComponent()
                + NeedCalculateLocationComponent()
                + LocatorComponent(PolygonLocatorHelper())
            }
    }

    private fun createDynamicEntity(): EcsEntity {
        return myFactory
            .createMapEntity("map_ent_d_polygon_$mapId")
            .addComponents {
                if (layerIndex != null && index != null) {
                    + IndexComponent(layerIndex!!, index!!)
                }
                + MapIdComponent(mapId!!)
                + NeedBboxComponent()
                + RegionFragmentsComponent()
                + RendererComponent(RegionRenderer())
                + ScreenLoopComponent().apply { origins = listOf(Coordinates.ZERO_CLIENT_POINT) }
                + ScreenOriginComponent()
                + StyleComponent().apply {
                    setFillColor(this@PolygonsBuilder.fillColor)
                    setStrokeColor(this@PolygonsBuilder.strokeColor)
                    setStrokeWidth(this@PolygonsBuilder.strokeWidth)
                }
                + NeedLocationComponent()
                + NeedGeocodeLocationComponent()
                + LocatorComponent(PolygonLocatorHelper())
            }
    }
}

fun PolygonsBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isClosed = true, isGeodesic = isGeodesic)
}