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
    val position: Vec<org.jetbrains.letsPlot.livemap.World>
    val canReset: Boolean

    val isZoomLevelChanged: Boolean
    val isZoomFractionChanged: Boolean
    val panDistance: Vec<org.jetbrains.letsPlot.livemap.Client>?
    val panFrameDistance: Vec<org.jetbrains.letsPlot.livemap.Client>?

    val isMoved: Boolean

    fun requestZoom(zoom: Double)
    fun requestPosition(position: Vec<org.jetbrains.letsPlot.livemap.World>)

    fun animate(zoom: Double?, position: Vec<org.jetbrains.letsPlot.livemap.World>?)
    fun reset()
}

class MutableCamera(val myComponentManager: EcsComponentManager) : Camera {
    var requestedZoom: Double? = null
    var requestedPosition: Vec<org.jetbrains.letsPlot.livemap.World>? = null
    var requestedAnimation: Boolean? = null
    var requestedReset: Boolean? = null

    override var zoom: Double = 0.0
    override var position: Vec<org.jetbrains.letsPlot.livemap.World> = org.jetbrains.letsPlot.livemap.World.ZERO_VEC
    override var canReset: Boolean = false

    override var isZoomLevelChanged: Boolean = false
    override var isZoomFractionChanged: Boolean = false
    override var panDistance: Vec<org.jetbrains.letsPlot.livemap.Client>? = null
    override var panFrameDistance: Vec<org.jetbrains.letsPlot.livemap.Client>? = null
    override var isMoved: Boolean = false

    override fun requestZoom(zoom: Double) {
        if (this.zoom == zoom) return
        requestedZoom = zoom
    }

    override fun requestPosition(position: Vec<org.jetbrains.letsPlot.livemap.World>) {
        requestedPosition = position
    }

    override fun animate(zoom: Double?, position: Vec<org.jetbrains.letsPlot.livemap.World>?) {
        zoom?.let(this::requestZoom)
        position?.let(this::requestPosition)
        requestedAnimation = true
    }

    override fun reset() {
        requestedReset = true
    }
}
