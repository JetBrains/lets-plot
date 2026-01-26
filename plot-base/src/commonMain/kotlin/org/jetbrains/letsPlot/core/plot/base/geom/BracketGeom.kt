/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
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
        // Bracket
        val linesHelper = LinesHelper(pos, coord, ctx)
        linesHelper.setResamplingEnabled(false) // TODO
        val pathData = linesHelper.createPathData(aesthetics.dataPoints().map(::toSegmentAes)) { p ->
            val xMin = p.finiteOrNull(Aes.XMIN) ?: return@createPathData null
            val xMax = p.finiteOrNull(Aes.XMAX) ?: return@createPathData null
            val y = p.finiteOrNull(Aes.Y) ?: return@createPathData null
            val tickLength = 5.0 * linesHelper.getUnitResolution(DimensionUnit.SIZE, Aes.Y) // TODO
            listOf(
                DoubleVector(xMin, y - tickLength),
                DoubleVector(xMin, y),
                DoubleVector(xMax, y),
                DoubleVector(xMax, y - tickLength),
            )
        }
        val svgPath = linesHelper.renderPaths(pathData, filled = false)
        root.appendNodes(svgPath)

        // Label
        val textHelper = TextHelper(aesthetics, pos, coord, ctx, formatter, naValue, sizeUnit, checkOverlap = false, ::coordOrNull, ::objectRectangle, ::componentFactory)
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