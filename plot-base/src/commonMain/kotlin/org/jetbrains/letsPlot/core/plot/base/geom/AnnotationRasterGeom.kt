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
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import kotlin.math.max
import kotlin.math.min

class AnnotationRasterGeom(
    private val imageUrl: String,
    private val xmin: Double?,
    private val xmax: Double?,
    private val ymin: Double?,
    private val ymax: Double?,
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
            SvgImageElement.IMAGE_RENDERING,
            if (interpolate) "optimizeQuality" else "optimizeSpeed"
        )
        root.add(svgImageElement)
    }

    private fun dataBounds(coord: CoordinateSystem, ctx: GeomContext): DoubleRectangle? {
        val contentOrigin = DoubleVector.ZERO
        val contentCorner = ctx.getContentBounds().dimension
        val dataOrigin = coord.fromClient(contentOrigin)
        val dataCorner = coord.fromClient(contentCorner)

        val x0 = when {
            xmin == null -> minNotNull(dataOrigin?.x, dataCorner?.x) ?: return null
            xmin.isFinite() -> xmin
            else -> return null
        }
        val x1 = when {
            xmax == null -> maxNotNull(dataOrigin?.x, dataCorner?.x) ?: return null
            xmax.isFinite() -> xmax
            else -> return null
        }
        val y0 = when {
            ymin == null -> minNotNull(dataOrigin?.y, dataCorner?.y) ?: return null
            ymin.isFinite() -> ymin
            else -> return null
        }
        val y1 = when {
            ymax == null -> maxNotNull(dataOrigin?.y, dataCorner?.y) ?: return null
            ymax.isFinite() -> ymax
            else -> return null
        }

        return DoubleRectangle.span(DoubleVector(x0, y0), DoubleVector(x1, y1))
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun minNotNull(a: Double?, b: Double?): Double? {
            return if (a == null || b == null) null else min(a, b)
        }

        private fun maxNotNull(a: Double?, b: Double?): Double? {
            return if (a == null || b == null) null else max(a, b)
        }
    }
}
