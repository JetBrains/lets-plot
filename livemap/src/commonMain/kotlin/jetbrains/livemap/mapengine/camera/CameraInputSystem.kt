/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.camera

import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.input.MouseInputComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.toClientPoint

class CameraInputSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myCamera: MutableCamera
    private var myLastFrameDragLocation: Vector? = null

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

        mouseInput.dragState?.let { drag ->
            if (drag.started) {
                myLastFrameDragLocation = drag.origin
            } else {

                val dragFrameDistance = drag.location.sub(myLastFrameDragLocation!!)
                myLastFrameDragLocation = drag.location

                myCamera.panDistance = drag.location.sub(drag.origin).toClientPoint()
                myCamera.panFrameDistance = dragFrameDistance.toClientPoint()

                if (dragFrameDistance != Vector.ZERO) {
                    val position = viewport.getMapCoord(viewport.center - dragFrameDistance.toClientPoint())
                    context.camera.requestPosition(position)
                }

                if (drag.stopped) {
                    myLastFrameDragLocation = null
                }
            }
        } ?: run {
            myCamera.panDistance = null
            myCamera.panFrameDistance = null
        }

        myCamera.isZoomLevelChanged = false
        myCamera.isZoomFractionChanged = false

        myCamera.isMoved = when {
            // requestedPosition also used by dragging. isMoved will always be false without a dragState check
            mouseInput.dragState == null && myCamera.requestedPosition != null -> true
            mouseInput.dragState?.stopped == true -> true
            else -> false
        }
        myCamera.canReset = context.initialZoom?.toDouble() != myCamera.zoom || context.initialPosition != myCamera.position

        if (myCamera.requestedAnimation == true) {
            val zoomDelta = myCamera.requestedZoom?.let { it - myCamera.zoom } ?: 0.0
            val center = myCamera.requestedPosition ?: viewport.position
            val scaleOrigin = viewport.center
            if (zoomDelta != 0.0) {
                if (!cameraEntity.contains<CameraScale.CameraScaleEffectComponent>()) {
                    CameraScale.setAnimation(cameraEntity, scaleOrigin, center, zoomDelta)
                }
            }
        } else if (myCamera.requestedReset == true) {
            updateCamera(
                requestedZoom = context.initialZoom!!.toDouble(),
                requestedPosition = context.initialPosition
            )
        }
        else {
            updateCamera(
                requestedZoom = myCamera.requestedZoom,
                requestedPosition = myCamera.requestedPosition
            )
        }

        myCamera.requestedZoom = null
        myCamera.requestedPosition = null
        myCamera.requestedAnimation = null
        myCamera.requestedReset = null
    }

    private fun updateCamera(requestedZoom: Double?, requestedPosition: Vec<World>?) {
        requestedZoom?.let { myCamera.zoom = it }
        requestedPosition?.let { myCamera.position = it }

        myCamera.isZoomLevelChanged = requestedZoom?.let { it % 1.0 == 0.0 } ?: false
        myCamera.isZoomFractionChanged = requestedZoom != null
    }
}
