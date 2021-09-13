/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.camera

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.toClientPoint

class CameraInputSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myCamera: MutableCamera

    override fun initImpl(context: LiveMapContext) {
        myCamera = context.camera as MutableCamera

        createEntity("camera").addComponents {
            + CameraComponent(myCamera)
            + MouseInputComponent()
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val mouseInput = cameraEntity.get<MouseInputComponent>()
        val viewport = context.mapRenderContext.viewport

        val dragDistance = mouseInput.dragDistance

        if (dragDistance != null && dragDistance != Vector.ZERO) {
            context.camera.requestPosition(viewport.getMapCoord(
                viewport.center - dragDistance.toClientPoint()
            ))
        }


        removeChangedComponents()

        myCamera.isZoomLevelChanged = false
        myCamera.isZoomFractionChanged = false
        myCamera.isMoved = false

        myCamera.requestedZoom?.let {
            myCamera.isZoomLevelChanged = it % 1.0 == 0.0
            myCamera.isZoomFractionChanged = true
            myCamera.zoom = it
        }

        myCamera.requestedPosition?.let {
            myCamera.position = it
            myCamera.isMoved = true
        }

        myCamera.requestedZoom = null
        myCamera.requestedPosition = null

        if (myCamera.isZoomFractionChanged || myCamera.isMoved) {
            updateAll(myCamera.isZoomLevelChanged, myCamera.isZoomFractionChanged, myCamera.isMoved)
        }
    }

    private fun removeChangedComponents() {
        for (entity in getEntities<ZoomLevelChangedComponent>().toList()) {
            entity.removeComponent(ZoomLevelChangedComponent::class)
        }

        for (entity in getEntities<ZoomFractionChangedComponent>().toList()) {
            entity.removeComponent(ZoomFractionChangedComponent::class)
        }

        for (entity in getEntities<CenterChangedComponent>().toList()) {
            entity.removeComponent(CenterChangedComponent::class)
        }
    }

    private fun updateAll(zoomLevelChanged: Boolean, zoomFractionChanged: Boolean, centerChanged: Boolean) {
        for (entity in getEntities<CameraListenerComponent>()) {
            if (zoomLevelChanged) {
                entity.tag(::ZoomLevelChangedComponent)
                entity.tag(::CenterChangedComponent)
            }

            if (zoomFractionChanged) {
                entity.tag(::ZoomFractionChangedComponent)
                entity.tag(::CenterChangedComponent)
            }

            if (centerChanged) {
                entity.tag(::CenterChangedComponent)
            }
        }
    }
}
