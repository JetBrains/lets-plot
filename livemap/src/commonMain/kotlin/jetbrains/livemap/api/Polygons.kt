/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Transforms.transformMultiPolygon
import jetbrains.datalore.base.typedGeometry.bbox
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Coordinates
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.Renderers.PolygonRenderer
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerGroup
import jetbrains.livemap.fragment.RegionBBoxComponent
import jetbrains.livemap.fragment.RegionFragmentsComponent
import jetbrains.livemap.fragment.RegionRenderer
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geocoding.RegionIdComponent
import jetbrains.livemap.geometry.ScaleComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.ScreenOriginComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
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
    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var layerIndex: Int? = null
    var index: Int? = null

    var geoObject: GeoObject? = null

    var lineDash: List<Double> = emptyList()
    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 0.0
    var fillColor: Color = Color.GREEN

    var multiPolygon: MultiPolygon<LonLat>? = null

    fun build(): EcsEntity? {

        return when {
            geoObject != null -> createGeoObjectEntity()
            multiPolygon != null -> createStaticEntity()
            else -> null
        }
    }

    private fun createStaticEntity(): EcsEntity {
        val geometry = multiPolygon!!
            .run { transformMultiPolygon(this, myMapProjection::project) }

        val bbox = bbox(geometry) ?: error("Polygon bbox can't be null")

        return myFactory
            .createMapEntity("map_ent_s_polygon")
            .addComponents {
                if (layerIndex != null && index != null) {
                    + IndexComponent(layerIndex!!, index!!)
                }
                + RenderableComponent().apply {
                    renderer = PolygonRenderer()
                }
                + ChartElementComponent().apply {
                    sizeScalingRange = this@PolygonsBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PolygonsBuilder.alphaScalingEnabled
                    fillColor = this@PolygonsBuilder.fillColor
                    strokeColor = this@PolygonsBuilder.strokeColor
                    strokeWidth = this@PolygonsBuilder.strokeWidth
                }
                + WorldOriginComponent(bbox.origin)
                + WorldGeometryComponent().apply { this.geometry = geometry }
                + WorldDimensionComponent(bbox.dimension)
                + ScreenLoopComponent()
                + ScreenOriginComponent()
                + ScaleComponent()
                + NeedLocationComponent
                + NeedCalculateLocationComponent
                + LocatorComponent(PolygonLocatorHelper())
            }
    }

    private fun createGeoObjectEntity(): EcsEntity {
        val geoObject = this@PolygonsBuilder.geoObject!!

        return myFactory
            .createMapEntity("map_ent_geo_object_polygon_" + geoObject.id)
            .addComponents {
                + RenderableComponent().apply {
                    renderer = RegionRenderer()
                }
                + ChartElementComponent().apply {
                    sizeScalingRange = this@PolygonsBuilder.sizeScalingRange
                    fillColor = this@PolygonsBuilder.fillColor
                    strokeColor = this@PolygonsBuilder.strokeColor
                    strokeWidth = this@PolygonsBuilder.strokeWidth
                }
                + RegionIdComponent(geoObject.id)
                + RegionFragmentsComponent()
                + RegionBBoxComponent(geoObject.bbox)
                + ScreenLoopComponent()
                + NeedLocationComponent
                + NeedCalculateLocationComponent
            }.apply {
                get<ScreenLoopComponent>().origins = listOf(Coordinates.ZERO_CLIENT_POINT)
            }
    }
}

fun PolygonsBuilder.geometry(points: List<LonLatPoint>, isGeodesic: Boolean) {
    multiPolygon = geometry(points, isClosed = true, isGeodesic = isGeodesic)
}