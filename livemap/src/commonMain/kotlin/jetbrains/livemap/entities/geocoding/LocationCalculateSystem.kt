/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.spatial.GeoUtils.convertToGeoRectangle
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.projections.Coordinates.ZERO_LONLAT_POINT

class LocationCalculateSystem(
    componentManager: EcsComponentManager
) : LiveMapSystem(componentManager) {
    private lateinit var myLocation: LocationComponent

    override fun initImpl(context: LiveMapContext) {
        myLocation = getSingleton()
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        getMutableEntities(READY_CALCULATE)
            .forEach { entity ->
                entity.remove<NeedCalculateLocationComponent>()

                Rect(
                    context.mapProjection.invert(entity.get<WorldOriginComponent>().origin),
                    entity.tryGet<WorldDimensionComponent>()?.dimension?.run(context.mapProjection::invert) ?: ZERO_LONLAT_POINT
                )
                    .run(::convertToGeoRectangle)
                    .run(myLocation::add)
            }
    }

    companion object {

        val READY_CALCULATE = listOf(
            NeedCalculateLocationComponent::class,
            WorldOriginComponent::class
        )
    }
}