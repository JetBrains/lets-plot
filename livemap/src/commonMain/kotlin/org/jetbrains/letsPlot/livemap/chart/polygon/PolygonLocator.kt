/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.polygon

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.contains
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.HoverObject
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.Locator
import org.jetbrains.letsPlot.livemap.chart.fragment.RegionFragmentsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper

object PolygonLocator : Locator {

    override fun search(coord: Vec<org.jetbrains.letsPlot.livemap.Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (target.contains<RegionFragmentsComponent>()) {
            target.get<RegionFragmentsComponent>().fragments.forEach { fragment ->
                if (isCoordinateOnEntity(coord, fragment, renderHelper)) {
                    return HoverObject(
                        layerIndex = target.get<IndexComponent>().layerIndex,
                        index = target.get<IndexComponent>().index,
                        distance = 0.0,
                        this
                    )
                }
            }

            return null
        } else {
            return when (isCoordinateOnEntity(coord, target, renderHelper)) {
                true -> HoverObject(
                    layerIndex = target.get<IndexComponent>().layerIndex,
                    index = target.get<IndexComponent>().index,
                    distance = 0.0,
                    this
                )

                false -> null
            }
        }
    }

    // Top polygon - actual for heightmaps, when cursor hovers over multiple polygons,
    // but only highest polygon is visible and actually hovered.
    override fun reduce(hoverObjects: Collection<HoverObject>) = hoverObjects.maxByOrNull(HoverObject::index)

    private fun isCoordinateOnEntity(coord: Vec<org.jetbrains.letsPlot.livemap.Client>, target: EcsEntity, renderHelper: RenderHelper): Boolean {
        if (!target.contains(WorldGeometryComponent::class)) {
            return false
        }

        val cursorMapCoord = renderHelper.posToWorld(coord)
        return isCoordinateInPolygon(cursorMapCoord, target.get<WorldGeometryComponent>().geometry.multiPolygon)

    }

    private fun isCoordinateInPolygon(coord: Vec<org.jetbrains.letsPlot.livemap.World>, multiPolygon: MultiPolygon<org.jetbrains.letsPlot.livemap.World>): Boolean {
        for (polygon in multiPolygon) {
            if (polygon.bbox?.contains(coord) == false) {
                continue
            }
            var count = 0
            for (ring in polygon) {
                if (ringContainsCoordinate(ring, coord)) {
                    ++count
                }
            }
            if (count % 2 == 1) {
                return true
            }
        }
        return false
    }

    private fun <TypeT> ringContainsCoordinate(ring: List<Vec<TypeT>>, coord: Vec<TypeT>): Boolean {
        var intersectionCount = 0
        for (i in 1 until ring.size) {
            val start = i - 1
            if (ring[start].y >= coord.y && ring[i].y >= coord.y ||
                ring[start].y < coord.y && ring[i].y < coord.y
            ) {
                continue
            }
            val x: Double = ring[start].x + (coord.y - ring[start].y) *
                    (ring[i].x - ring[start].x) / (ring[i].y - ring[start].y)
            if (x <= coord.x) {
                intersectionCount++
            }
        }
        return intersectionCount % 2 != 0
    }

}