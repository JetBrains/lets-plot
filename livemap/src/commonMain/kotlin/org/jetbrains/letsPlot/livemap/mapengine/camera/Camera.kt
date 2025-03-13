/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.camera

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager

interface Camera {
    val zoom: Double
    val position: Vec<World>
    val canReset: Boolean

    val isZoomLevelChanged: Boolean
    val isZoomFractionChanged: Boolean
    val panDistance: Vec<Client>?
    val panFrameDistance: Vec<Client>?

    val isMoved: Boolean

    fun requestZoom(zoom: Double)
    fun requestPosition(position: Vec<World>)
    fun setAnimationValue(zoom: Double)

    fun animate(zoom: Double?, position: Vec<World>?)
    fun reset()
}

class MutableCamera(val myComponentManager: EcsComponentManager) : Camera {
    var requestedZoom: Double? = null
    var requestedPosition: Vec<World>? = null
    var requestedAnimation: Boolean? = null
    var requestedReset: Boolean? = null
    var animationValue: Double? = null

    override var zoom: Double = 0.0
    override var position: Vec<World> = World.ZERO_VEC
    override var canReset: Boolean = false

    override var isZoomLevelChanged: Boolean = false
    override var isZoomFractionChanged: Boolean = false
    override var panDistance: Vec<Client>? = null
    override var panFrameDistance: Vec<Client>? = null
    override var isMoved: Boolean = false

    override fun requestZoom(zoom: Double) {
        if (this.zoom == zoom) return
        requestedZoom = zoom
    }

    override fun requestPosition(position: Vec<World>) {
        requestedPosition = position
    }

    override fun setAnimationValue(zoom: Double) {
        if (this.zoom == zoom) return
        animationValue = zoom
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
