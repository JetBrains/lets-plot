/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents

class CameraUpdateDetectionSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myCamera: MutableCamera

    override fun initImpl(context: LiveMapContext) {
        myCamera = context.camera as MutableCamera

        createEntity("camera")
            .addComponents {
                + CameraComponent(myCamera)
            }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {

        removeChangedComponents()

        myCamera.isZoomChanged = false
        myCamera.isMoved = false

        myCamera.requestedZoom?.let {
            myCamera.zoom = it
            myCamera.isZoomChanged = true
        }

        myCamera.requestedPosition?.let {
            myCamera.position = it
            myCamera.isMoved = true
        }

        myCamera.requestedZoom = null
        myCamera.requestedPosition = null

        if (myCamera.isZoomChanged || myCamera.isMoved) {
            updateAll(myCamera.isZoomChanged, myCamera.isMoved)
        }
    }

    private fun removeChangedComponents() {
        for (entity in getEntities(ZoomChangedComponent::class).toList()) {
            entity.removeComponent(ZoomChangedComponent::class)
        }

        for (entity in getEntities(CenterChangedComponent::class).toList()) {
            entity.removeComponent(CenterChangedComponent::class)
        }
    }

    private fun updateAll(zoomChanged: Boolean, centerChanged: Boolean) {
        for (entity in getEntities(CameraListenerComponent::class)) {
            if (zoomChanged) {
                entity.tag(::ZoomChangedComponent)
                entity.tag(::CenterChangedComponent)
            }

            if (centerChanged) {
                entity.tag(::CenterChangedComponent)
            }
        }
    }
}