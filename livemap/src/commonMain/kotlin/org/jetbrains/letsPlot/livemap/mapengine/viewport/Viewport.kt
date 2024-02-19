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
    val size: ClientPoint,
    val minZoom: Int,
    val maxZoom: Int
) {
    private val zoomTransform = Transforms.zoom<World, Client> { zoom }
    private val viewportTransform = object : Transform<WorldPoint, ClientPoint> {
        override fun apply(v: WorldPoint): ClientPoint = zoomTransform.apply(v) - zoomTransform.apply(position) + center
        override fun invert(v: ClientPoint): WorldPoint = zoomTransform.invert(v) - zoomTransform.invert(center) + position
    }

    val center: ClientPoint = size / 2.0
    private var windowSize = World.ZERO_VEC
    private var windowOrigin = World.ZERO_VEC
    var window: WorldRectangle =
        WorldRectangle(
            World.ZERO_VEC,
            World.ZERO_VEC
        )
        private set

    var zoom: Int = minZoom
        set(zoom) {
            field = max(minZoom, min(zoom, maxZoom))
            windowSize = zoomTransform.invert(size)
            windowOrigin = viewportTransform.invert(Client.ZERO_VEC)
            updateWindow()
        }

    open var position: WorldPoint = World.ZERO_VEC
        set(value) {
            field = helper.normalize(value)
            windowOrigin = viewportTransform.invert(Client.ZERO_VEC)
            updateWindow()
        }

    open val visibleCells: Set<CellKey>
        get() = helper.getCells(window, zoom)

    init {
        zoom = 1
    }


    fun getMapCoord(viewCoord: ClientPoint): WorldPoint = helper.normalize(viewportTransform.invert(viewCoord))
    fun getViewCoord(mapCoord: WorldPoint): ClientPoint = viewportTransform.apply(mapCoord)
    fun toClientDimension(dimension: WorldPoint): ClientPoint = zoomTransform.apply(dimension)
    fun toWorldDimension(dimension: ClientPoint): WorldPoint = zoomTransform.invert(dimension)
    fun calculateBoundingBox(bBoxes: List<Rect<World>>): Rect<World> = helper.calculateBoundingBox(bBoxes)

    fun getMapOrigins(): List<ClientPoint> {
        val origin = getViewCoord(World.ZERO_VEC)
        val dim = toClientDimension(World.DOMAIN.dimension)
        return Rect.LTRB(viewportTransform.invert(origin), viewportTransform.invert(origin + dim))
            .let { helper.getOrigins(it, window) }
            .map(::getViewCoord)
    }

    private fun updateWindow() {
        window = WorldRectangle(windowOrigin, windowSize)
    }

    companion object {
        fun create(
            helper: ViewportHelper,
            size: ClientPoint,
            position: WorldPoint,
            minZoom: Int,
            maxZoom: Int
        ): Viewport {
            return Viewport(helper, size, minZoom, maxZoom).apply {
                this.position = position
            }
        }

        fun toClientDimension(dimension: WorldPoint, zoom: Int): ClientPoint {
            return Transforms.zoom<World, Client> { zoom }.apply(dimension)
        }
    }
}