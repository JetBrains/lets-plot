/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class BracketGeom : TextGeom() {
    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val textHelper = TextHelper(aesthetics, pos, coord, ctx, formatter, naValue, sizeUnit, checkOverlap, ::coordOrNull, ::objectRectangle, ::componentFactory)
        val strokeScaler: (DataPointAesthetics) -> Double = { p -> AesScaling.strokeWidth(p, DataPointAesthetics::stroke) }
        val svgHelper = GeomHelper(pos, coord, ctx)
            .createSvgElementHelper()
            .setStrokeAlphaEnabled(true)
            .setSpacer(0.0) // TODO
            .setResamplingEnabled(false) // TODO
            .setArrowSpec(null) // TODO
        for (p in aesthetics.dataPoints()) {
            val xMin = p.finiteOrNull(Aes.XMIN) ?: continue
            val xMax = p.finiteOrNull(Aes.XMAX) ?: continue
            val y = p.finiteOrNull(Aes.Y) ?: continue
            val tickLength = 5.0 * textHelper.getUnitResolution(DimensionUnit.SIZE, Aes.Y) // TODO
            val bracket = listOf(
                DoubleSegment(DoubleVector(xMin, y - tickLength), DoubleVector(xMin, y)),
                DoubleSegment(DoubleVector(xMin, y), DoubleVector(xMax, y)),
                DoubleSegment(DoubleVector(xMax, y), DoubleVector(xMax, y - tickLength)),
            ).mapNotNull { segment ->
                svgHelper.createLine(segment, p, strokeScaler)?.first
            }
            if (bracket.size == 3) {
                bracket.forEach(root::add)
            }
        }
        textHelper.createSvgComponents().forEach(root::add)
    }

    override fun coordOrNull(p: DataPointAesthetics): DoubleVector? {
        val (xmin, xmax, y) = p.finiteOrNull(Aes.XMIN, Aes.XMAX, Aes.Y) ?: return null
        return DoubleVector((xmin + xmax) / 2.0, y)
    }

    override fun componentFactory(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ) = TextHelper.textComponentFactory(p, location, text, sizeUnitRatio, ctx, boundsCenter, ::labelNudge)

    companion object {
        const val HANDLES_GROUPS = false

        private fun labelNudge(location: DoubleVector, textSize: DoubleVector): DoubleVector =
            location.add(DoubleVector(0.0, -textSize.y / 2.0))
    }
}