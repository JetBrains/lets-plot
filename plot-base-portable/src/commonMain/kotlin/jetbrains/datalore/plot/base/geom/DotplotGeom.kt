/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.max

class DotplotGeom : GeomBase() {
    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val pointsWithBinWidth = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.BINWIDTH)
        if (!pointsWithBinWidth.any()) return

        val dotHelper = DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val binWidthPx = max(pointsWithBinWidth.first().binwidth()!! * ctx.getUnitResolution(Aes.X), 2.0)
        for (p in GeomUtil.withDefined(pointsWithBinWidth, Aes.X, Aes.STACKSIZE)) {
            for (i in 0 until p.stacksize()!!.toInt()) {
                val center = DoubleVector(p.x()!!, (i + 0.5) * binWidthPx)
                val path = dotHelper.createDot(
                    p,
                    geomHelper.toClient(center, p),
                    binWidthPx / 2
                )
                root.add(path.rootGroup)
            }
            // TODO: buildHint(p, binWidthPx, geomHelper, ctx)
        }
    }

    private fun buildHint(
        p: DataPointAesthetics,
        binWidthPx: Double,
        geomHelper: GeomHelper,
        ctx: GeomContext
    ) {
        val origin = geomHelper.toClient(DoubleVector(
            p.x()!! - binWidthPx / 2,
            binWidthPx * (if (p.stacksize()!! > 0) 1.0 else 0.0)
        ), p)
        val dimension = DoubleVector(binWidthPx, binWidthPx)
        val rect = DoubleRectangle(origin, dimension)
        val colorsByDataPoint = HintColorUtil.fromMappedColors(ctx)

        ctx.targetCollector.addRectangle(
            p.index(),
            rect,
            GeomTargetCollector.TooltipParams.params()
                .setMainColor(HintColorUtil.fromFill(p))
                .setColors(colorsByDataPoint(p)),
            TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
        )
    }

    private class DotHelper constructor(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
        LinesHelper(pos, coord, ctx) {
            fun createDot(
                p: DataPointAesthetics,
                center: DoubleVector,
                r: Double
            ): LinePath {
                val leftBound = center.add(DoubleVector(-r, 0.0))
                val rightBound = center.add(DoubleVector(r, 0.0))

                val builder = SvgPathDataBuilder(true)
                builder.moveTo(leftBound)
                builder.ellipticalArc(r, r, 0.0, false, false, rightBound)
                builder.ellipticalArc(r, r, 0.0, false, false, leftBound)
                builder.closePath()

                val path = LinePath(builder)
                decorate(path, p, true)

                return path
            }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}