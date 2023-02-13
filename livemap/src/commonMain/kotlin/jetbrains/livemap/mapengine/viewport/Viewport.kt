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
import jetbrains.livemap.core.projections.Projection
import jetbrains.livemap.core.projections.Projections
import kotlin.math.max
import kotlin.math.min

open class Viewport internal constructor(
    private val helper: ViewportHelper,
    val size: ClientPoint,
    val minZoom: Int,
    val maxZoom: Int
) {

    private val zoomTransform = Projections.zoom<World, Client> { zoom }
    val center: ClientPoint = size / 2.0
    private val viewportTransform = viewportTransform(zoomTransform, { position }, { center })
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

    fun getMapCoord(viewCoord: ClientPoint): WorldPoint {
        return helper.normalize(viewportTransform.invert(viewCoord))
    }

    fun getViewCoord(mapCoord: WorldPoint): ClientPoint {
        return viewportTransform.project(mapCoord)
    }

    fun getOrigins(origin: ClientPoint, dimension: ClientPoint): List<ClientPoint> {
        return Rect.LTRB(viewportTransform.invert(origin), viewportTransform.invert(origin + dimension))
            .let { helper.getOrigins(it, window) }
            .map(::getViewCoord)
    }

    fun calculateBoundingBox(bBoxes: List<Rect<World>>) = helper.calculateBoundingBox(bBoxes)

    private fun updateWindow() {
        window = WorldRectangle(windowOrigin, windowSize)
    }

    private fun viewportTransform(
        zoomProjection: Projection<WorldPoint, ClientPoint>,
        position: () -> WorldPoint,
        center: () -> ClientPoint
    ): Projection<WorldPoint, ClientPoint> {
        return object : Projection<WorldPoint, ClientPoint> {
            override fun project(v: WorldPoint): ClientPoint = zoomProjection.project(v - position()) + center()
            override fun invert(v: ClientPoint): WorldPoint = zoomProjection.invert(v - center()) + position()
        }
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
    }
}