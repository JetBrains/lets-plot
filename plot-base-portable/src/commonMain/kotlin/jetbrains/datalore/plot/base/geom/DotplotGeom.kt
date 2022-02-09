/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.vis.svg.SvgPathDataBuilder

class DotplotGeom : GeomBase() {
    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val pointsWithWidth = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.WIDTH)
        if (!pointsWithWidth.any()) return

        val dotHelper = DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val binWidthPx = GeomUtil.widthPx(pointsWithWidth.first(), ctx, 2.0)
        for (p in GeomUtil.withDefined(pointsWithWidth, Aes.X, Aes.STACKSIZE)) {
            for (i in 0 until p.stacksize()!!.toInt()) {
                val center = DoubleVector(p.x()!!, (i + 0.5) * binWidthPx)
                val path = dotHelper.createDot(
                    p,
                    geomHelper.toClient(center, p),
                    binWidthPx / 2,
                    binWidthPx / 2
                )
                root.add(path.rootGroup)
            }
        }

        // TODO: Fix hints
        /*
        BarTooltipHelper.collectRectangleTargets(
            emptyList(),
            aesthetics, pos, coord, ctx,
            BarGeom.rectangleByDataPoint(ctx, isHintRect = true),
            { HintColorUtil.fromFill(it) }
        )
        */
    }

    private class DotHelper constructor(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
        LinesHelper(pos, coord, ctx) {
            fun createDot(
                p: DataPointAesthetics,
                center: DoubleVector,
                rx: Double,
                ry: Double
            ): LinePath {
                val leftBound = center.add(DoubleVector(-rx, 0.0))
                val rightBound = center.add(DoubleVector(rx, 0.0))

                val builder = SvgPathDataBuilder(true)
                builder.moveTo(leftBound)
                builder.ellipticalArc(rx, ry, 0.0, false, false, rightBound)
                builder.ellipticalArc(rx, ry, 0.0, false, false, leftBound)
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