/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.contains
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.fragment.RegionFragmentsComponent
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent

class PolygonLocator : Locator {

    override fun search(coord: Vec<Client>, target: EcsEntity): HoverObject? {
        if (target.contains<RegionFragmentsComponent>()) {
            target.get<RegionFragmentsComponent>().fragments.forEach { fragment ->
                if (isCoordinateOnEntity(coord, fragment)) {
                    return HoverObject(
                        layerIndex = target.get<IndexComponent>().layerIndex,
                        index = target.get<IndexComponent>().index
                    )
                }
            }

            return null
        } else {
            return when (isCoordinateOnEntity(coord, target)) {
                true -> HoverObject(
                    layerIndex = target.get<IndexComponent>().layerIndex,
                    index = target.get<IndexComponent>().index
                )

                false -> null
            }
        }
    }

    private fun isCoordinateOnEntity(coord: Vec<Client>, target: EcsEntity): Boolean {
        if (!target.contains(LOCATABLE_COMPONENTS)) {
            return false
        }

        target.get<ScreenLoopComponent>().origins.forEach { origin ->
            if (isCoordinateInPolygon(coord - origin, target.get<ScreenGeometryComponent>().geometry)) {
                return true
            }
        }

        return false
    }

    private fun isCoordinateInPolygon(coord: Vec<Client>, multiPolygon: MultiPolygon<Client>): Boolean {
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

    companion object {
        val LOCATABLE_COMPONENTS = listOf(ScreenLoopComponent::class, ScreenGeometryComponent::class)
    }
}
