/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geocoding

import org.jetbrains.letsPlot.commons.intern.typedGeometry.GeometryType.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementLocationComponent
import org.jetbrains.letsPlot.livemap.chart.fragment.RegionBBoxComponent
import org.jetbrains.letsPlot.livemap.core.MapRuler
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.geocoding.MapLocationGeocoder.Companion.convertToWorldRects
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent

class LocationCalculateSystem(
    private val mapRuler: MapRuler<org.jetbrains.letsPlot.livemap.World>,
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
                                MULTI_POLYGON -> mapRuler.calculateBoundingBox(multiPolygon.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon<org.jetbrains.letsPlot.livemap.World>::bbox))
                                MULTI_LINESTRING -> mapRuler.calculateBoundingBox(multiLineString.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString<org.jetbrains.letsPlot.livemap.World>::bbox))
                                MULTI_POINT -> mapRuler.calculateBoundingBox(listOfNotNull(multiPoint.bbox))
                                else -> error("Unsupported geometry: $type")
                            }
                        }
                    }
                    entity.contains<WorldGeometryComponent>() -> {
                        with(entity.get<WorldGeometryComponent>().geometry) {
                            when (type) {
                                MULTI_POLYGON -> mapRuler.calculateBoundingBox(multiPolygon.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon<org.jetbrains.letsPlot.livemap.World>::bbox))
                                MULTI_LINESTRING -> mapRuler.calculateBoundingBox(multiLineString.mapNotNull(org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString<org.jetbrains.letsPlot.livemap.World>::bbox))
                                MULTI_POINT -> mapRuler.calculateBoundingBox(listOfNotNull(multiPoint.bbox))
                                else -> error("Unsupported geometry: $type")
                            }
                        }
                    }
                    entity.contains<WorldOriginComponent>() -> {
                        Rect.XYWH(
                            entity.get<WorldOriginComponent>().origin,
                            entity.tryGet<WorldDimensionComponent>()?.dimension ?: org.jetbrains.letsPlot.livemap.World.ZERO_VEC
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
