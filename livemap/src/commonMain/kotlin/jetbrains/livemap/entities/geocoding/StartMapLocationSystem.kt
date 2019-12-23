/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.center
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.MapPosition
import jetbrains.livemap.MapWidgetUtil
import jetbrains.livemap.MapWidgetUtil.DEFAULT_LOCATION
import jetbrains.livemap.MapWidgetUtil.convertToWorldRects
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.UpdateViewProjectionComponent
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.projections.World

class StartMapLocationSystem(
    componentManager: EcsComponentManager,
    private val myZoom: Int?,
    private val myLocationRect: Async<Rect<World>>?
) : LiveMapSystem(componentManager) {
    private lateinit var myLocation: LocationComponent
    private lateinit var myCamera: EcsEntity
    private lateinit var myViewport: Viewport
    private lateinit var myDefaultLocation: List<Rect<World>>
    private var myNeedLocation = true

    override fun initImpl(context: LiveMapContext) {
        myLocation = getSingleton()
        myCamera = getSingletonEntity(CameraComponent::class)
        myViewport = context.mapRenderContext.viewport
        myDefaultLocation = DEFAULT_LOCATION.convertToWorldRects(context.mapProjection)
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (!myNeedLocation) return

        myLocationRect?.map {
            myLocation.wait(1)
            myLocation.add(it)
        }

        if (myLocation.isReady()) {
            myNeedLocation = false
            val position = myLocation.locations
                .run(myViewport::calculateBoundingBox)
                .run(::createMapPosition)

            val camera = getSingletonEntity<CameraComponent>()
            camera.get<CameraComponent>().apply {
                zoom = position.zoom.toDouble()
                this.position = position.coordinate
            }

            camera.tag(::UpdateViewProjectionComponent)
        }
    }

    private fun createMapPosition(rectangle: Rect<World>): MapPosition {

        val zoom: Int = myZoom
            ?: if (rectangle.dimension.x != 0.0 && rectangle.dimension.y != 0.0) {
                MapWidgetUtil.calculateMaxZoom(rectangle.dimension, myViewport.size)
            } else {
                MapWidgetUtil.calculateMaxZoom(
                    myViewport.calculateBoundingBox(myDefaultLocation).dimension,
                    myViewport.size
                )
            }

        return MapPosition(zoom, rectangle.center)
    }
}