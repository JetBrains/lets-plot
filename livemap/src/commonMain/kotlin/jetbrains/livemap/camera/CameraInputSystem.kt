/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.projectionGeometry.minus
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.projections.toClientPoint

class CameraInputSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val camera = cameraEntity.get<CameraComponent>()
        val mouseInput = cameraEntity.get<MouseInputComponent>()
        val viewport = context.mapRenderContext.viewport

        val dragDistance = mouseInput.dragDistance

        if (dragDistance != null && dragDistance != Vector.ZERO) {
            camera.position = viewport.getMapCoord(
                viewport.center - dragDistance.toClientPoint()
            )
        }
    }
}