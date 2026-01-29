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
    var bracketShorten: Double = 1.0

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
        val pathData = linesHelper.createPathData(
            aesthetics.dataPoints().map(::toSegmentAes),
            bracketBuilder(linesHelper, bracketShorten)
        )
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

        private fun bracketBuilder(helper: LinesHelper, bracketShorten: Double): (DataPointAesthetics) -> List<DoubleVector>? = builder@{ p ->
            val xMin = p.finiteOrNull(Aes.XMIN) ?: return@builder null
            val xMax = p.finiteOrNull(Aes.XMAX) ?: return@builder null
            val y = p.finiteOrNull(Aes.Y) ?: return@builder null
            val x = (xMin + xMax) / 2.0
            val bracketLength = xMax - xMin
            val xStart = x - bracketShorten * bracketLength / 2.0
            val xEnd = x + bracketShorten * bracketLength / 2.0
            val tickLength = 5.0 * helper.getUnitResolution(DimensionUnit.SIZE, Aes.Y) // TODO
            listOf(
                DoubleVector(xStart, y - tickLength),
                DoubleVector(xStart, y),
                DoubleVector(xEnd, y),
                DoubleVector(xEnd, y - tickLength),
            )
        }

        private fun labelNudge(location: DoubleVector, textSize: DoubleVector): DoubleVector =
            location.add(DoubleVector(0.0, -textSize.y / 2.0))
    }
}