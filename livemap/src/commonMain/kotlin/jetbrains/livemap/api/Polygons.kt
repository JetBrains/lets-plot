/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Transforms.transform
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.IndexComponent
import jetbrains.livemap.chart.LocatorComponent
import jetbrains.livemap.chart.fragment.RegionBBoxComponent
import jetbrains.livemap.chart.fragment.RegionFragmentsComponent
import jetbrains.livemap.chart.fragment.RegionRenderer
import jetbrains.livemap.chart.polygon.PolygonLocator
import jetbrains.livemap.chart.polygon.PolygonRenderer
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.geocoding.NeedCalculateLocationComponent
import jetbrains.livemap.geocoding.NeedLocationComponent
import jetbrains.livemap.geocoding.RegionIdComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.RenderableComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

@LiveMapDsl
class Polygons(
    val factory: MapEntityFactory,
    val mapProjection: MapProjection
)

fun LayersBuilder.polygons(block: Polygons.() -> Unit) {

    val layerEntity =  myComponentManager
        .createEntity("map_layer_polygon")
        .addComponents {
            + layerManager.addLayer("geom_polygon", LayerKind.FEATURES)
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

    var geometry: MultiPolygon<LonLat>? = null

    fun build(): EcsEntity? {

        return when {
            geoObject != null -> createGeoObjectEntity()
            geometry != null -> createStaticEntity()
            else -> null
        }
    }

    private fun createStaticEntity(): EcsEntity {
        val worldGeometry = transform(geometry!!, myMapProjection::apply, resamplingPrecision = null)

        val worldBbox = worldGeometry.bbox ?: error("Polygon bbox can't be null")

        return myFactory
            .createMapEntity("map_ent_s_polygon")
            .addComponents {
                if (layerIndex != null && index != null) {
                    +IndexComponent(layerIndex!!, index!!)
                }
                +RenderableComponent().apply {
                    renderer = PolygonRenderer()
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@PolygonsBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PolygonsBuilder.alphaScalingEnabled
                    fillColor = this@PolygonsBuilder.fillColor
                    strokeColor = this@PolygonsBuilder.strokeColor
                    strokeWidth = this@PolygonsBuilder.strokeWidth
                }
                +WorldOriginComponent(worldBbox.origin)
                +WorldGeometryComponent().apply { this.geometry = Geometry.of(worldGeometry) }
                +WorldDimensionComponent(worldBbox.dimension)
                +NeedLocationComponent
                +NeedCalculateLocationComponent
                +LocatorComponent(PolygonLocator)
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
                + NeedLocationComponent
                + NeedCalculateLocationComponent
            }
    }
}
