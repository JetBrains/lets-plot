/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.Geometry
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Ring
import jetbrains.datalore.base.typedGeometry.Transforms.transform
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.World
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
class PolygonLayerBuilder(
    val factory: FeatureEntityFactory,
    val mapProjection: MapProjection
)

fun FeatureLayerBuilder.polygons(block: PolygonLayerBuilder.() -> Unit) {

    val layerEntity =  myComponentManager
        .createEntity("map_layer_polygon")
        .addComponents {
            + layerManager.addLayer("geom_polygon", LayerKind.FEATURES)
            + LayerEntitiesComponent()
        }

    PolygonLayerBuilder(
        FeatureEntityFactory(layerEntity, panningPointsMaxCount = 15_000),
        mapProjection
    ).apply(block)
}

fun PolygonLayerBuilder.polygon(block: PolygonEntityBuilder.() -> Unit) {
    PolygonEntityBuilder(factory, mapProjection)
        .apply(block)
        .build()
}

@LiveMapDsl
class PolygonEntityBuilder(
    private val myFactory: FeatureEntityFactory,
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
            geoObject != null -> createFragmentFeature()
            geometry != null -> createPolygonFeature()
            else -> null
        }
    }

    private fun createPolygonFeature(): EcsEntity {
        val worldGeometry = transform(geometry!!, myMapProjection::apply, resamplingPrecision = null)
        val worldBbox = worldGeometry.bbox ?: error("Polygon bbox can't be null")

        myFactory.incrementLayerPointsTotalCount(worldGeometry.sumOf { poly -> poly.sumOf(Ring<World>::size) })

        return myFactory
            .createFeature("map_ent_s_polygon")
            .addComponents {
                if (layerIndex != null && index != null) {
                    +IndexComponent(layerIndex!!, index!!)
                }
                +RenderableComponent().apply {
                    renderer = PolygonRenderer()
                }
                +ChartElementComponent().apply {
                    sizeScalingRange = this@PolygonEntityBuilder.sizeScalingRange
                    alphaScalingEnabled = this@PolygonEntityBuilder.alphaScalingEnabled
                    fillColor = this@PolygonEntityBuilder.fillColor
                    strokeColor = this@PolygonEntityBuilder.strokeColor
                    strokeWidth = this@PolygonEntityBuilder.strokeWidth
                }
                +WorldOriginComponent(worldBbox.origin)
                +WorldGeometryComponent().apply { this.geometry = Geometry.of(worldGeometry) }
                +WorldDimensionComponent(worldBbox.dimension)
                +NeedLocationComponent
                +NeedCalculateLocationComponent
                +LocatorComponent(PolygonLocator)
            }
    }

    private fun createFragmentFeature(): EcsEntity {
        val geoObject = this@PolygonEntityBuilder.geoObject!!

        return myFactory
            .createFeature("map_ent_geo_object_polygon_" + geoObject.id)
            .addComponents {
                + RenderableComponent().apply {
                    renderer = RegionRenderer()
                }
                + ChartElementComponent().apply {
                    sizeScalingRange = this@PolygonEntityBuilder.sizeScalingRange
                    fillColor = this@PolygonEntityBuilder.fillColor
                    strokeColor = this@PolygonEntityBuilder.strokeColor
                    strokeWidth = this@PolygonEntityBuilder.strokeWidth
                }
                + RegionIdComponent(geoObject.id)
                + RegionFragmentsComponent()
                + RegionBBoxComponent(geoObject.bbox)
                + NeedLocationComponent
                + NeedCalculateLocationComponent
            }
    }
}
