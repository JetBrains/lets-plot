/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import kotlin.math.roundToInt

class ViewProjectionUpdateSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val camera = cameraEntity.getComponent<CameraComponent>()
        val viewport = context.mapRenderContext.viewport

        if (viewport.position != camera.position) {
            viewport.position = camera.position
        }

        if (cameraEntity.contains(UpdateViewProjectionComponent::class)) {
            viewport.zoom = camera.zoom.roundToInt()
            cameraEntity.removeComponent(UpdateViewProjectionComponent::class)
        }
    }
}
