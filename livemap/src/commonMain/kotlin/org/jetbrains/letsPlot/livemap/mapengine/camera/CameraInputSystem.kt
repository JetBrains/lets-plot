/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.camera

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.graphics.Rectangle
import org.jetbrains.letsPlot.livemap.core.input.ClickableComponent
import org.jetbrains.letsPlot.livemap.core.input.EventListenerComponent
import org.jetbrains.letsPlot.livemap.core.input.MouseInputComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraScale.CameraScaleEffectComponent
import org.jetbrains.letsPlot.livemap.toClientPoint

class CameraInputSystem(
    componentManager: EcsComponentManager,
    val interactive: Boolean
) : AbstractSystem<LiveMapContext>(componentManager) {
    private lateinit var myCamera: MutableCamera
    private var myLastFrameDragLocation: Vector? = null

    override fun initImpl(context: LiveMapContext) {
        myCamera = context.camera as MutableCamera

        val cameraEntity = componentManager.createEntity("camera")
            .addComponents {
                + CameraComponent(myCamera)
            }

        if (!interactive) return

        val listeners = EventListenerComponent()
        val viewport = context.mapRenderContext.viewport

        cameraEntity.addComponents {
            + MouseInputComponent()
            + ClickableComponent(
                Rectangle().apply {
                    origin = Client.ZERO_VEC.toDoubleVector()
                    dimension = viewport.size.toDoubleVector()
                }
            )
            + listeners
        }

        listeners.addDoubleClickListener { clickEvent ->
            if (cameraEntity.contains<CameraScaleEffectComponent>() || cameraEntity.getComponent<CameraComponent>().zoom == viewport.maxZoom.toDouble()) {
                return@addDoubleClickListener
            }

            val location = clickEvent.location.toClientPoint()
            val newViewportPosition = viewport.getMapCoord((location + viewport.center) / 2.0)
            CameraScale.setAnimation(cameraEntity, location, newViewportPosition, 1.0)
        }
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val mouseInput = cameraEntity.tryGet<MouseInputComponent>()
        val viewport = context.mapRenderContext.viewport

        mouseInput?.dragState?.let { drag ->
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
            mouseInput?.dragState == null && myCamera.requestedPosition != null -> true
            mouseInput?.dragState?.stopped == true -> true
            else -> false
        }
        myCamera.canReset = context.initialZoom?.toDouble() != myCamera.zoom || context.initialPosition != myCamera.position

        if (myCamera.animationValue == null && myCamera.requestedAnimation == true) {
            val zoomDelta = myCamera.requestedZoom?.let { it - myCamera.zoom } ?: 0.0
            val center = myCamera.requestedPosition ?: viewport.position
            val scaleOrigin = viewport.center
            if (zoomDelta != 0.0) {
                if (!cameraEntity.contains<CameraScaleEffectComponent>()) {
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
                requestedZoom = myCamera.animationValue ?: myCamera.requestedZoom,
                requestedPosition = myCamera.requestedPosition
            )
        }

        myCamera.requestedZoom = null
        myCamera.animationValue = null
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
