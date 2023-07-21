/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.geocoding

import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection

class ApplyPointSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    private lateinit var myMapProjection: MapProjection

    override fun initImpl(context: LiveMapContext) {
        myMapProjection = context.mapProjection
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getMutableEntities(NEED_APPLY)
            .forEach { entity ->

                entity.addComponents {
                    myMapProjection.apply(entity.point)?.let { entity.worldPointInitializer.invoke(this, it) }
                }

                tagDirtyParentLayer(entity)
                entity.remove<LonLatComponent>()
                entity.remove<PointInitializerComponent>()
            }
    }

    private val EcsEntity.point
        get() = get<LonLatComponent>().point

    private val EcsEntity.worldPointInitializer
        get() = get<PointInitializerComponent>().worldPointInitializer

    companion object {
        val NEED_APPLY = listOf(
            LonLatComponent::class,
            PointInitializerComponent::class
        )
    }
}