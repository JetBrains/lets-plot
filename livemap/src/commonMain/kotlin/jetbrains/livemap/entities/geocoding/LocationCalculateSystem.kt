/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.spatial.GeoUtils.convertToGeoRectangle
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.placement.WorldDimensionComponent
import jetbrains.livemap.entities.placement.WorldOriginComponent

class LocationCalculateSystem(
    componentManager: EcsComponentManager,
    private val myNeedLocation: Boolean
) : LiveMapSystem(componentManager) {
    private lateinit var myLocation: LocationComponent

    override fun initImpl(context: LiveMapContext) {
        myLocation = getSingleton()
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val entities = getMutableEntities(NEED_CALCULATE)

        if (entities.isEmpty()) return

        if (myNeedLocation) {
            myLocation.wait(entities.size)

            entities.forEach {
                Rect(
                    context.mapProjection.invert(it.get<WorldOriginComponent>().origin),
                    context.mapProjection.invert(it.get<WorldDimensionComponent>().dimension)
                )
                    .run(::convertToGeoRectangle)
                    .run(myLocation::add)
            }
        } else {
            entities.forEach {
                it.remove<NeedCalculateLocationComponent>()
            }
        }
    }

    companion object {
        val NEED_CALCULATE = listOf(NeedCalculateLocationComponent::class)
    }
}