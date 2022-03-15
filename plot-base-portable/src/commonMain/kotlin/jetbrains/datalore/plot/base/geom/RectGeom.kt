/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.RectTargetCollectorHelper
import jetbrains.datalore.plot.base.geom.util.RectanglesHelper
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.CURSOR_TOOLTIP
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil

class RectGeom : GeomBase() {

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper =
            RectanglesHelper(aesthetics, pos, coord, ctx)
        helper.createRectangles(Companion::rectangleByDataPoint).forEach(root::add)
        RectTargetCollectorHelper(
            rectanglesHelper = helper,
            rectangleByDataPoint = Companion::rectangleByDataPoint,
            tooltipKind = CURSOR_TOOLTIP,
            colorsByDataPoint= HintColorUtil.createColorMarkerMapper(GeomKind.RECT, ctx)
        ).collectTo(ctx.targetCollector)
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.XMIN,
//                Aes.XMAX,
//                Aes.YMIN,
//                Aes.YMAX,
//                Aes.SIZE,
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA,
//        )
        //rectangle groups are used in geom_livemap
        const val HANDLES_GROUPS = true

        private fun rectangleByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
            val xmin = p.xmin()
            val xmax = p.xmax()
            val ymin = p.ymin()
            val ymax = p.ymax()
            return if (SeriesUtil.allFinite(xmin, xmax, ymin, ymax)) {
                DoubleRectangle.span(DoubleVector(xmin!!, ymin!!), DoubleVector(xmax!!, ymax!!))
            }
            else {
                null
            }
        }
    }
}
