/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.contains
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.fragment.RegionFragmentsComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.viewport.Viewport

object PolygonLocator : Locator {

    override fun search(coord: Vec<Client>, target: EcsEntity, viewport: Viewport): HoverObject? {
        if (target.contains<RegionFragmentsComponent>()) {
            target.get<RegionFragmentsComponent>().fragments.forEach { fragment ->
                if (isCoordinateOnEntity(coord, fragment, viewport)) {
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
            return when (isCoordinateOnEntity(coord, target, viewport)) {
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

    private fun isCoordinateOnEntity(coord: Vec<Client>, target: EcsEntity, viewport: Viewport): Boolean {
        if (!target.contains(WorldGeometryComponent::class)) {
            return false
        }

        //target.get<ScreenLoopComponent>().origins.forEach { origin ->
        val cursorMapCoord = viewport.getMapCoord(coord)
        if (isCoordinateInPolygon(cursorMapCoord, target.get<WorldGeometryComponent>().geometry.multiPolygon)) {
            return true
        }
        //}

        return false
    }

    private fun isCoordinateInPolygon(coord: Vec<World>, multiPolygon: MultiPolygon<World>): Boolean {
        for (polygon in multiPolygon) {
            if (polygon.bbox?.contains(coord) == false) {
                continue
            }
            var count = 0
            for (ring in polygon) {
                if (LocatorUtil.ringContainsCoordinate(ring, coord)) {
                    ++count
                }
            }
            if (count % 2 == 1) {
                return true
            }
        }
        return false
    }
}
