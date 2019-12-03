/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.projections.MapProjection

class ApplyCentroidsSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    private lateinit var myMapProjection: MapProjection

    override fun initImpl(context: LiveMapContext) {
        myMapProjection = context.mapProjection
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getEntities(NEED_APPLY)
            .toList()
            .forEach { entity ->

                entity.addComponents {
                    entity.block.invoke(this, entity.centroid)
                }

                entity.remove<CentroidComponent>()
                entity.remove<ApplyCentroidComponent>()
            }
    }

    private val EcsEntity.centroid
        get() = get<CentroidComponent>().centroid

    private val EcsEntity.block
        get() = get<ApplyCentroidComponent>().block

    companion object {
        val NEED_APPLY = listOf(
            CentroidComponent::class,
            ApplyCentroidComponent::class
        )
    }
}