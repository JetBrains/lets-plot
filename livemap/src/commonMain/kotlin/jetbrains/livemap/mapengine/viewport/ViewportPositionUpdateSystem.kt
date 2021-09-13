/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.viewport

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.CameraComponent

class ViewportPositionUpdateSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val camera = cameraEntity.getComponent<CameraComponent>()
        val viewport = context.mapRenderContext.viewport

        if (viewport.position != camera.position) {
            viewport.position = camera.position
        }

        if (context.camera.isZoomFractionChanged && context.camera.isZoomLevelChanged) {
            viewport.zoom = context.camera.zoom.toInt()
        }
    }
}
