/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.camera

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Coordinates.ZERO_WORLD_POINT
import jetbrains.livemap.World
import jetbrains.livemap.core.ecs.EcsComponentManager

interface Camera {
    val zoom: Double
    val position: Vec<World>
    val canReset: Boolean

    val isZoomLevelChanged: Boolean
    val isZoomFractionChanged: Boolean
    val isMoved: Boolean

    fun requestZoom(zoom: Double)
    fun requestPosition(position: Vec<World>)

    fun animate(zoom: Double?, position: Vec<World>?)
    fun reset()
}

class MutableCamera(val myComponentManager: EcsComponentManager): Camera {
    var requestedZoom: Double? = null
    var requestedPosition: Vec<World>? = null
    var requestedAnimation: Boolean? = null
    var requestedReset: Boolean? = null

    override var zoom: Double = 0.0
    override var position: Vec<World> = ZERO_WORLD_POINT
    override var canReset: Boolean = false

    override var isZoomLevelChanged: Boolean = false
    override var isZoomFractionChanged: Boolean = false
    override var isMoved: Boolean = false

    override fun requestZoom(zoom: Double) {
        if (this.zoom == zoom) return
        requestedZoom = zoom
    }

    override fun requestPosition(position: Vec<World>) {
        requestedPosition = position
    }

    override fun animate(zoom: Double?, position: Vec<World>?) {
        zoom?.let(this::requestZoom)
        position?.let(this::requestPosition)
        requestedAnimation = true
    }

    override fun reset() {
        requestedReset = true
    }
}
