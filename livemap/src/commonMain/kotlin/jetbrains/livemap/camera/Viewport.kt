/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.div
import jetbrains.datalore.base.typedGeometry.minus
import jetbrains.datalore.base.typedGeometry.plus
import jetbrains.livemap.LiveMapConstants
import jetbrains.livemap.cells.CellKey
import jetbrains.livemap.core.projections.Projection
import jetbrains.livemap.core.projections.ProjectionUtil
import jetbrains.livemap.projection.*
import kotlin.math.max
import kotlin.math.min

open class Viewport internal constructor(
    private val helper: ViewportHelper,
    val size: ClientPoint,
    val minZoom: Int,
    val maxZoom: Int
) {

    private val zoomTransform = ProjectionUtil.square<World, Client>(ProjectionUtil.zoom { zoom })
    val center: ClientPoint = size / 2.0
    private val viewportTransform = viewportTransform(zoomTransform, { position }, { center })
    private var windowSize = Coordinates.ZERO_WORLD_POINT
    private var windowOrigin = Coordinates.ZERO_WORLD_POINT
    var window: WorldRectangle =
        WorldRectangle(
            Coordinates.ZERO_WORLD_POINT,
            Coordinates.ZERO_WORLD_POINT
        )
        private set

    var zoom: Int = 1
        set(zoom) {
            field = max(minZoom, min(zoom, maxZoom))
            windowSize = zoomTransform.invert(size)
            windowOrigin = viewportTransform.invert(Coordinates.ZERO_CLIENT_POINT)
            updateWindow()
        }

    open var position: WorldPoint = Coordinates.ZERO_WORLD_POINT
        set(value) {
            field = helper.normalize(value)
            windowOrigin = viewportTransform.invert(Coordinates.ZERO_CLIENT_POINT)
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
        return Rect(viewportTransform.invert(origin), viewportTransform.invert(origin + dimension))
            .let { helper.getOrigins(it, window) }
            .map { getViewCoord(it) }
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
            override fun project(v: WorldPoint): ClientPoint {
                return zoomProjection.project(v - position()) + center()
            }

            override fun invert(v: ClientPoint): WorldPoint {
                return zoomProjection.invert(v - center()) + position()
            }
        }
    }

    companion object {
        fun create(helper: ViewportHelper, size: ClientPoint, position: WorldPoint, minZoom: Int, maxZoom: Int): Viewport {
            return Viewport(
                helper,
                size,
                minZoom,
                maxZoom
            ).apply {
                this.position = position
            }
        }
    }
}