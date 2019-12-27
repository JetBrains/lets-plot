/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.center
import jetbrains.livemap.LiveMapConstants
import jetbrains.livemap.LiveMapConstants.DEFAULT_LOCATION
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.MapLocationGeocoder.Companion.convertToWorldRects
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.World
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

class MapLocationInitializationSystem(
    componentManager: EcsComponentManager,
    private val myZoom: Double?,
    private val myLocationRect: Async<Rect<World>>?
) : AbstractSystem<LiveMapContext>(componentManager) {
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
            myLocation.locations
                .run(myViewport::calculateBoundingBox)
                .calculatePosition { zoom, coordinates ->
                    context.camera.requestZoom(floor(zoom))
                    context.camera.requestPosition(coordinates)
                }
        }
    }

    private fun Rect<World>.calculatePosition(positionConsumer: (zoom: Double, coordinates: Vec<World>) -> Unit) {

        val zoom: Double = myZoom
            ?: if (dimension.x != 0.0 && dimension.y != 0.0) {
                calculateMaxZoom(dimension, myViewport.size)
            } else {
                calculateMaxZoom(
                    myViewport.calculateBoundingBox(myDefaultLocation).dimension,
                    myViewport.size
                )
            }

        positionConsumer(zoom, center)
    }

    private fun calculateMaxZoom(rectSize: Vec<World>, containerSize: Vec<Client>): Double {
        val xZoom = calculateMaxZoom(rectSize.x, containerSize.x)
        val yZoom = calculateMaxZoom(rectSize.y, containerSize.y)
        val zoom = min(xZoom, yZoom)
        return max(LiveMapConstants.MIN_ZOOM.toDouble(), min(zoom, LiveMapConstants.MAX_ZOOM.toDouble()))
    }

    private fun calculateMaxZoom(regionLength: Double, containerLength: Double): Double {
        if (regionLength == 0.0) {
            return LiveMapConstants.MAX_ZOOM.toDouble()
        }
        return if (containerLength == 0.0) {
            LiveMapConstants.MIN_ZOOM.toDouble()
        } else (ln(containerLength / regionLength) / ln(2.0))
    }
}