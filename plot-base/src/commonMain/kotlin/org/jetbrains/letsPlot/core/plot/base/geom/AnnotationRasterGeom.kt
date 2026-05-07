/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import kotlin.math.max
import kotlin.math.min

class AnnotationRasterGeom(
    private val imageUrl: String,
    private val xMin: Double?,
    private val xMax: Double?,
    private val yMin: Double?,
    private val yMax: Double?,
    private val interpolate: Boolean,
) : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val bbox = dataBounds(coord, ctx) ?: return
        val boundsClient = coord.toClient(bbox) ?: return

        val svgImageElement = SvgImageElement(
            boundsClient.origin.x, boundsClient.origin.y,
            boundsClient.dimension.x, boundsClient.dimension.y
        )
        svgImageElement.href().set(imageUrl)
        svgImageElement.setAttribute(
            SvgConstants.SVG_STYLE_ATTRIBUTE,
            if (interpolate) "image-rendering: auto" else "image-rendering: pixelated;image-rendering: crisp-edges;"
        )
        root.add(svgImageElement)
    }

    private fun dataBounds(coord: CoordinateSystem, ctx: GeomContext): DoubleRectangle? {
        val contentOrigin = DoubleVector.ZERO
        val contentCorner = ctx.getContentBounds().dimension
        val dataOrigin = coord.fromClient(contentOrigin)
        val dataCorner = coord.fromClient(contentCorner)

        val left = resolveBound(xMin, dataOrigin?.x, dataCorner?.x, ::min) ?: return null
        val right = resolveBound(xMax, dataOrigin?.x, dataCorner?.x, ::max) ?: return null
        val top = resolveBound(yMin, dataOrigin?.y, dataCorner?.y, ::min) ?: return null
        val bottom = resolveBound(yMax, dataOrigin?.y, dataCorner?.y, ::max) ?: return null

        return DoubleRectangle.LTRB(left, top, right, bottom)
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun resolveBound(bound: Double?, panelStart: Double?, panelEnd: Double?, edge: (Double, Double) -> Double): Double? {
            return when {
                bound == null && panelStart != null && panelEnd != null -> edge(panelStart, panelEnd)
                bound != null && bound.isFinite() -> bound
                else -> null
            }
        }
    }
}
