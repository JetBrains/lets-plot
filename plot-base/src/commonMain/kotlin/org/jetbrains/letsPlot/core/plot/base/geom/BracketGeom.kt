/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class BracketGeom : TextGeom() {
    var bracketShorten: Double = 1.0
    var tipLengthUnit: DimensionUnit = DEF_TIP_LENGTH_UNIT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineLegendKeyElementFactory(TextHelper::toSegmentAes)

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        // Bracket
        val linesHelper = LinesHelper(pos, coord, ctx)
        linesHelper.setResamplingEnabled(false)
        val pathData = linesHelper.createPathData(
            aesthetics.dataPoints().map(TextHelper::toSegmentAes),
            bracketBuilder(linesHelper, bracketShorten, tipLengthUnit)
        )
        val svgPath = linesHelper.renderPaths(pathData, filled = false)
        root.appendNodes(svgPath)

        // Label
        val textHelper = TextHelper(aesthetics, pos, coord, ctx, formatter, naValue, sizeUnit, checkOverlap = false, flipAngle = true, ::coordOrNull, ::objectRectangle, ::componentFactory)
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
        flipAngle: Boolean,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ) = TextHelper.textComponentFactory(p, location, text, flipAngle, sizeUnitRatio, ctx, boundsCenter, ::labelNudge)

    companion object {
        const val HANDLES_GROUPS = false

        val DEF_TIP_LENGTH_UNIT = DimensionUnit.SIZE

        private fun bracketBuilder(helper: LinesHelper, bracketShorten: Double, tipLengthUnit: DimensionUnit): (DataPointAesthetics) -> List<DoubleVector>? = builder@{ p ->
            val xMin = p.finiteOrNull(Aes.XMIN) ?: return@builder null
            val xMax = p.finiteOrNull(Aes.XMAX) ?: return@builder null
            val y = p.finiteOrNull(Aes.Y) ?: return@builder null
            val tipLengthStart = p.finiteOrNull(Aes.TIP_LENGTH_START) ?: return@builder null
            val tipLengthEnd = p.finiteOrNull(Aes.TIP_LENGTH_END) ?: return@builder null
            val x = (xMin + xMax) / 2.0
            val bracketLength = xMax - xMin
            val xStart = x - bracketShorten * bracketLength / 2.0
            val xEnd = x + bracketShorten * bracketLength / 2.0
            val tipLengthUnitResolution = helper.getUnitResolution(tipLengthUnit, Aes.Y)
            listOf(
                DoubleVector(xStart, y - tipLengthStart * tipLengthUnitResolution),
                DoubleVector(xStart, y),
                DoubleVector(xEnd, y),
                DoubleVector(xEnd, y - tipLengthEnd * tipLengthUnitResolution),
            )
        }

        private fun labelNudge(location: DoubleVector, textSize: DoubleVector): DoubleVector =
            location.add(DoubleVector(0.0, -textSize.y / 2.0))
    }
}