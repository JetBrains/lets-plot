/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geocoding

import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext

class LocationCounterSystem(
    componentManager: EcsComponentManager,
    private val myNeedLocation: Boolean
) : AbstractSystem<LiveMapContext>(componentManager) {
    private val myLocation = LocationComponent()

    override fun initImpl(context: LiveMapContext) {
        createEntity("LocationSingleton").add(myLocation)
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val entities = getMutableEntities<NeedLocationComponent>()

        if (myNeedLocation) {
            myLocation.wait(entities.size)
        } else {
            entities.forEach {
                it.remove<NeedCalculateLocationComponent>()
                it.remove<NeedGeocodeLocationComponent>()
            }
        }

        entities.forEach {
            it.remove<NeedLocationComponent>()
        }
    }
}