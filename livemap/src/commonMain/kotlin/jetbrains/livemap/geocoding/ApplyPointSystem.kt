/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.geocoding

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.layers.ParentLayerComponent.Companion.tagDirtyParentLayer
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.MapProjection

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