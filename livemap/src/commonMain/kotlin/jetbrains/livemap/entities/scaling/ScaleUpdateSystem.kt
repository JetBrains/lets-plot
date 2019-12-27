/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.scaling

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.camera.isIntegerZoom
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import kotlin.math.pow

class ScaleUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (context.camera.isIntegerZoom) {
            for (entity in getEntities(COMPONENT_TYPES)) {
                val scaleComponent = entity.get<ScaleComponent>()
                val scale = 2.0.pow(context.camera.zoom - scaleComponent.zoom)
                scaleComponent.scale = scale
            }
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            ZoomChangedComponent::class,
            ScaleComponent::class
        )
    }
}