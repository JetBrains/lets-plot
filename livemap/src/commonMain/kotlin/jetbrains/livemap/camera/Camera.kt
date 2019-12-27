/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.Coordinates.ZERO_WORLD_POINT
import jetbrains.livemap.projections.World

interface Camera {
    val zoom: Double
    val position: Vec<World>

    val isZoomChanged: Boolean
    val isMoved: Boolean

    fun requestZoom(zoom: Double)
    fun requestPosition(position: Vec<World>)
}

val Camera.isIntegerZoom: Boolean
    get() = zoom % CAMERA_STEP == 0.0

open class MutableCamera(val myComponentManager: EcsComponentManager): Camera {

    var requestedZoom: Double? = null
    var requestedPosition: Vec<World>? = null

    override var zoom: Double = 0.0
    override var position: Vec<World> = ZERO_WORLD_POINT

    override var isZoomChanged: Boolean = false
    override var isMoved: Boolean = false

    override fun requestZoom(zoom: Double) {
        if (this.zoom == zoom) return
        requestedZoom = zoom
    }

    override fun requestPosition(position: Vec<World>) {
        requestedPosition = position
    }
}

const val CAMERA_STEP = 1.0