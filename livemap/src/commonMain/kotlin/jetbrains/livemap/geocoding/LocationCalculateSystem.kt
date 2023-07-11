/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import org.jetbrains.letsPlot.commons.intern.typedGeometry.GeometryType.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import jetbrains.livemap.World
import jetbrains.livemap.chart.ChartElementLocationComponent
import jetbrains.livemap.chart.fragment.RegionBBoxComponent
import jetbrains.livemap.core.MapRuler
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.geocoding.MapLocationGeocoder.Companion.convertToWorldRects
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

class LocationCalculateSystem(
    private val mapRuler: MapRuler<World>,
    private val mapProjection: MapProjection,
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myLocation: LocationComponent

    override fun initImpl(context: LiveMapContext) {
        myLocation = getSingleton()
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getMutableEntities(READY_CALCULATE)
            .forEach { entity ->
                when {
                    entity.contains<ChartElementLocationComponent>() -> {
                        with(entity.get<ChartElementLocationComponent>().geometry) {
                            when (type) {
                                MULTI_POLYGON -> mapRuler.calculateBoundingBox(multiPolygon.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon<World>::bbox))
                                MULTI_LINESTRING -> mapRuler.calculateBoundingBox(multiLineString.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString<World>::bbox))
                                MULTI_POINT -> mapRuler.calculateBoundingBox(listOfNotNull(multiPoint.bbox))
                                else -> error("Unsupported geometry: $type")
                            }
                        }
                    }
                    entity.contains<WorldGeometryComponent>() -> {
                        with(entity.get<WorldGeometryComponent>().geometry) {
                            when (type) {
                                MULTI_POLYGON -> mapRuler.calculateBoundingBox(multiPolygon.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon<World>::bbox))
                                MULTI_LINESTRING -> mapRuler.calculateBoundingBox(multiLineString.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString<World>::bbox))
                                MULTI_POINT -> mapRuler.calculateBoundingBox(listOfNotNull(multiPoint.bbox))
                                else -> error("Unsupported geometry: $type")
                            }
                        }
                    }
                    entity.contains<WorldOriginComponent>() -> {
                        Rect.XYWH(
                            entity.get<WorldOriginComponent>().origin,
                            entity.tryGet<WorldDimensionComponent>()?.dimension ?: World.ZERO_VEC
                        )
                    }
                    entity.contains<RegionBBoxComponent>() -> {
                        val worldRects = entity.get<RegionBBoxComponent>().bbox.convertToWorldRects(mapProjection)
                        mapRuler.calculateBoundingBox(worldRects)
                    }
                    else -> null
                }?.let { entityLocation ->
                    myLocation.add(entityLocation)
                    entity.remove<NeedCalculateLocationComponent>()
                }
            }
    }

    companion object {
        val READY_CALCULATE = listOf(
            NeedCalculateLocationComponent::class
        )
    }
}
