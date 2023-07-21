/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.viewport

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.div
import org.jetbrains.letsPlot.commons.intern.typedGeometry.minus
import org.jetbrains.letsPlot.commons.intern.typedGeometry.plus
import org.jetbrains.letsPlot.livemap.*
import org.jetbrains.letsPlot.livemap.core.Transform
import org.jetbrains.letsPlot.livemap.core.Transforms
import kotlin.math.max
import kotlin.math.min

open class Viewport internal constructor(
    private val helper: ViewportHelper,
    val size: org.jetbrains.letsPlot.livemap.ClientPoint,
    val minZoom: Int,
    val maxZoom: Int
) {
    private val zoomTransform = Transforms.zoom<org.jetbrains.letsPlot.livemap.World, org.jetbrains.letsPlot.livemap.Client> { zoom }
    private val viewportTransform = object : Transform<org.jetbrains.letsPlot.livemap.WorldPoint, org.jetbrains.letsPlot.livemap.ClientPoint> {
        override fun apply(v: org.jetbrains.letsPlot.livemap.WorldPoint): org.jetbrains.letsPlot.livemap.ClientPoint = zoomTransform.apply(v) - zoomTransform.apply(position) + center
        override fun invert(v: org.jetbrains.letsPlot.livemap.ClientPoint): org.jetbrains.letsPlot.livemap.WorldPoint = zoomTransform.invert(v) - zoomTransform.invert(center) + position
    }

    val center: org.jetbrains.letsPlot.livemap.ClientPoint = size / 2.0
    private var windowSize = org.jetbrains.letsPlot.livemap.World.ZERO_VEC
    private var windowOrigin = org.jetbrains.letsPlot.livemap.World.ZERO_VEC
    var window: org.jetbrains.letsPlot.livemap.WorldRectangle =
        org.jetbrains.letsPlot.livemap.WorldRectangle(
            org.jetbrains.letsPlot.livemap.World.ZERO_VEC,
            org.jetbrains.letsPlot.livemap.World.ZERO_VEC
        )
        private set

    var zoom: Int = minZoom
        set(zoom) {
            field = max(minZoom, min(zoom, maxZoom))
            windowSize = zoomTransform.invert(size)
            windowOrigin = viewportTransform.invert(org.jetbrains.letsPlot.livemap.Client.ZERO_VEC)
            updateWindow()
        }

    open var position: org.jetbrains.letsPlot.livemap.WorldPoint = org.jetbrains.letsPlot.livemap.World.ZERO_VEC
        set(value) {
            field = helper.normalize(value)
            windowOrigin = viewportTransform.invert(org.jetbrains.letsPlot.livemap.Client.ZERO_VEC)
            updateWindow()
        }

    open val visibleCells: Set<CellKey>
        get() = helper.getCells(window, zoom)

    init {
        zoom = 1
    }


    fun getMapCoord(viewCoord: org.jetbrains.letsPlot.livemap.ClientPoint): org.jetbrains.letsPlot.livemap.WorldPoint = helper.normalize(viewportTransform.invert(viewCoord))
    fun getViewCoord(mapCoord: org.jetbrains.letsPlot.livemap.WorldPoint): org.jetbrains.letsPlot.livemap.ClientPoint = viewportTransform.apply(mapCoord)
    fun toClientDimension(dimension: org.jetbrains.letsPlot.livemap.WorldPoint): org.jetbrains.letsPlot.livemap.ClientPoint = zoomTransform.apply(dimension)
    fun toWorldDimension(dimension: org.jetbrains.letsPlot.livemap.ClientPoint): org.jetbrains.letsPlot.livemap.WorldPoint = zoomTransform.invert(dimension)
    fun calculateBoundingBox(bBoxes: List<Rect<org.jetbrains.letsPlot.livemap.World>>): Rect<org.jetbrains.letsPlot.livemap.World> = helper.calculateBoundingBox(bBoxes)

    fun getMapOrigins(): List<org.jetbrains.letsPlot.livemap.ClientPoint> {
        val origin = getViewCoord(org.jetbrains.letsPlot.livemap.World.ZERO_VEC)
        val dim = toClientDimension(org.jetbrains.letsPlot.livemap.World.DOMAIN.dimension)
        return Rect.LTRB(viewportTransform.invert(origin), viewportTransform.invert(origin + dim))
            .let { helper.getOrigins(it, window) }
            .map(::getViewCoord)
    }

    private fun updateWindow() {
        window = org.jetbrains.letsPlot.livemap.WorldRectangle(windowOrigin, windowSize)
    }

    companion object {
        fun create(
            helper: ViewportHelper,
            size: org.jetbrains.letsPlot.livemap.ClientPoint,
            position: org.jetbrains.letsPlot.livemap.WorldPoint,
            minZoom: Int,
            maxZoom: Int
        ): Viewport {
            return Viewport(helper, size, minZoom, maxZoom).apply {
                this.position = position
            }
        }

        fun toClientDimension(dimension: org.jetbrains.letsPlot.livemap.WorldPoint, zoom: Int): org.jetbrains.letsPlot.livemap.ClientPoint {
            return Transforms.zoom<org.jetbrains.letsPlot.livemap.World, org.jetbrains.letsPlot.livemap.Client> { zoom }.apply(dimension)
        }
    }
}