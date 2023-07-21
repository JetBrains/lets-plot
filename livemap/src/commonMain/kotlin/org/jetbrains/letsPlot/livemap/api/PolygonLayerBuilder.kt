/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Transforms.transform
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.LocatorComponent
import org.jetbrains.letsPlot.livemap.chart.fragment.RegionBBoxComponent
import org.jetbrains.letsPlot.livemap.chart.fragment.RegionFragmentsComponent
import org.jetbrains.letsPlot.livemap.chart.fragment.RegionRenderer
import org.jetbrains.letsPlot.livemap.chart.polygon.PolygonLocator
import org.jetbrains.letsPlot.livemap.chart.polygon.PolygonRenderer
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.LayerKind
import org.jetbrains.letsPlot.livemap.geocoding.NeedCalculateLocationComponent
import org.jetbrains.letsPlot.livemap.geocoding.NeedLocationComponent
import org.jetbrains.letsPlot.livemap.geocoding.RegionIdComponent
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.RenderableComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent

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

        myFactory.incrementLayerPointsTotalCount(worldGeometry.sumOf { poly -> poly.sumOf(Ring<org.jetbrains.letsPlot.livemap.World>::size) })

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
