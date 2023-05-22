/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.viewport

import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.div
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.datalore.base.typedGeometry.plus
import jetbrains.livemap.*
import jetbrains.livemap.core.Transform
import jetbrains.livemap.core.Transforms
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
        override fun apply(v: WorldPoint): ClientPoint = zoomTransform.apply(v - position) + center
        override fun invert(v: ClientPoint): WorldPoint = zoomTransform.invert(v - center) + position
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


    fun getMapCoord(viewCoord: ClientPoint): WorldPoint {
        return helper.normalize(viewportTransform.invert(viewCoord))
    }

    fun getViewCoord(mapCoord: WorldPoint): ClientPoint {
        return viewportTransform.apply(mapCoord)
    }

    fun getOrigins(origin: ClientPoint, dimension: ClientPoint): List<ClientPoint> {
        return Rect.LTRB(viewportTransform.invert(origin), viewportTransform.invert(origin + dimension))
            .let { helper.getOrigins(it, window) }
            .map(::getViewCoord)
    }

    fun getOrigins(origin: WorldPoint): List<ClientPoint> {
        return Rect.LTRB(origin, origin)
            .let { helper.getOrigins(it, window) }
            .map(::getViewCoord)
    }

    fun toClientDimension(dimension: WorldPoint): ClientPoint {
        return zoomTransform.apply(dimension)
    }

    fun toWorldDimension(dimension: ClientPoint): WorldPoint {
        return zoomTransform.invert(dimension)
    }

    fun calculateBoundingBox(bBoxes: List<Rect<World>>) = helper.calculateBoundingBox(bBoxes)

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