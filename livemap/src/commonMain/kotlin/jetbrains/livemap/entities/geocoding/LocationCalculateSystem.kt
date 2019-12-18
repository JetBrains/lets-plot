/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.limitLat
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent
import jetbrains.livemap.projections.Coordinates.ZERO_WORLD_POINT

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

                val origin = entity.get<WorldOriginComponent>().origin
                val dimension = entity.tryGet<WorldDimensionComponent>()?.dimension ?: ZERO_WORLD_POINT

                val bottomLeft = context.mapProjection.invert(origin)
                val topRight = context.mapProjection.invert(origin + dimension)

                GeoRectangle(
                    bottomLeft.x,
                    limitLat(topRight.y),
                    topRight.x,
                    limitLat(bottomLeft.y)

                ).run(myLocation::add)
            }
    }

    companion object {

        val READY_CALCULATE = listOf(
            NeedCalculateLocationComponent::class,
            WorldOriginComponent::class
        )
    }
}