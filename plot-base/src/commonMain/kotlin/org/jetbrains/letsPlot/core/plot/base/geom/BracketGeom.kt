/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.max

class BracketGeom : TextGeom() {
    var dodged: Boolean = false
    var groupCount: Int? = null
    var dodgeWidth: Double = DEF_DODGE_WIDTH
    var bracketShorten: Double = 0.0
    var tipLengthUnit: DimensionUnit = DEF_TIPLENGTH_UNIT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineLegendKeyElementFactory(TextUtil::toSegmentAes)

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        // Initialization
        if (dodged && groupCount == null) {
            groupCount = aesthetics.dataPoints().mapNotNull { p ->
                p.finiteOrNull(Aes.GSTART, Aes.GEND)?.let { (gstart, gend) -> max(gstart, gend) }
            }.maxOrNull()?.toInt()?.let { it + 1 }
        }

        // Bracket
        val linesHelper = LinesHelper(pos, coord, ctx)
        val pathData = createPaths(
            aesthetics.dataPoints().map(TextUtil::toSegmentAes),
            bracketBuilder(linesHelper, ctx)
        )
        val clientPathData = linesHelper.toClientPaths(pathData)
        val svgPath = linesHelper.renderPaths(clientPathData, filled = false)
        root.appendNodes(svgPath)

        // Label
        val textHelper = TextHelper(aesthetics, pos, coord, ctx)
            .setFormatter(formatter)
            .setNaValue(naValue)
            .setSizeUnit(sizeUnit)
            .toLocation(toLocation(ctx))
        textHelper.createSvgComponents(flipAngle = true, labelNudge = ::labelNudge).forEach(root::add)
    }

    private fun bracketBuilder(helper: LinesHelper, ctx: GeomContext): (DataPointAesthetics) -> List<DoubleVector>? = builder@{ p ->
        val (xmin, xmax) = if (dodged) {
            val x = p.finiteOrNull(Aes.X) ?: return@builder null
            val gstart = p.finiteOrNull(Aes.GSTART) ?: return@builder null
            val gend = p.finiteOrNull(Aes.GEND) ?: return@builder null
            val resolution = ctx.getResolution(Aes.X)
            val xmin = computeDodgedPosition(x, gstart, groupCount ?: 1, dodgeWidth, resolution)
            val xmax = computeDodgedPosition(x, gend, groupCount ?: 1, dodgeWidth, resolution)
            Pair(xmin, xmax)
        } else {
            p.finiteOrNull(Aes.XMIN, Aes.XMAX) ?: return@builder null
        }
        val y = p.finiteOrNull(Aes.Y) ?: return@builder null
        val tipLengthStart = p.finiteOrNull(Aes.TIPLENGTH_START) ?: return@builder null
        val tipLengthEnd = p.finiteOrNull(Aes.TIPLENGTH_END) ?: return@builder null
        val x = (xmin + xmax) / 2.0
        val bracketLength = xmax - xmin
        val xStart = x - (1 - bracketShorten) * bracketLength / 2.0
        val xEnd = x + (1 - bracketShorten) * bracketLength / 2.0
        val tipLengthUnitResolution = helper.getUnitResolution(tipLengthUnit, Aes.Y)
        listOf(
            DoubleVector(xStart, y - tipLengthStart * tipLengthUnitResolution),
            DoubleVector(xStart, y),
            DoubleVector(xEnd, y),
            DoubleVector(xEnd, y - tipLengthEnd * tipLengthUnitResolution),
        )
    }

    private fun toLocation(ctx: GeomContext): (DataPointAesthetics) -> DoubleVector? = location@{ p ->
        val y = p.finiteOrNull(Aes.Y) ?: return@location null
        val (xmin, xmax) = if (dodged) {
            val (x, gstart, gend) = p.finiteOrNull(Aes.X, Aes.GSTART, Aes.GEND) ?: return@location null
            val resolution = ctx.getResolution(Aes.X)
            val xmin = computeDodgedPosition(x, gstart, groupCount ?: 1, dodgeWidth, resolution)
            val xmax = computeDodgedPosition(x, gend, groupCount ?: 1, dodgeWidth, resolution)
            Pair(xmin, xmax)
        } else {
            p.finiteOrNull(Aes.XMIN, Aes.XMAX) ?: return@location null
        }
        DoubleVector((xmin + xmax) / 2.0, y)
    }

    companion object {
        const val HANDLES_GROUPS = false

        const val DEF_DODGE_WIDTH = 0.95
        val DEF_TIPLENGTH_UNIT = DimensionUnit.SIZE

        private fun createPaths(
            dataPoints: Iterable<DataPointAesthetics>,
            pointToPath: ((DataPointAesthetics) -> List<DoubleVector>?)
        ): List<PathData> {
            return dataPoints.mapNotNull { p ->
                pointToPath(p)?.
                    map { coord -> PathPoint(p, coord) }?.
                    let { pathPoints -> PathData.create(pathPoints) }
            }
        }

        private fun labelNudge(location: DoubleVector, textSize: DoubleVector): DoubleVector =
            location.add(DoubleVector(0.0, -textSize.y / 2.0))

        // See DodgePos::translate()
        private fun computeDodgedPosition(x: Double, group: Double, groupCount: Int, dodgeWidth: Double, dataResolution: Double): Double {
            val median = (groupCount - 1) / 2.0
            val offset = (group - median) * dataResolution * dodgeWidth
            val scaler = 1.0 / groupCount
            return x + offset * scaler
        }
    }
}