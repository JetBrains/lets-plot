/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.BarTooltipHelper
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import kotlin.math.max

class LineRangeGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = VLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()

        val dataPoints = aesthetics.dataPoints().filter { p -> rectangleByDataPoint(p, coord) != null }
        for (p in GeomUtil.withDefined(dataPoints, Aes.X, Aes.YMIN, Aes.YMAX)) {
            val x = p.x()!!
            val ymin = p.ymin()!!
            val ymax = p.ymax()!!

            val start = DoubleVector(x, ymin)
            val end = DoubleVector(x, ymax)
            val line = helper.createLine(start, end, p)
            root.add(line)
        }

        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            rectangleByDataPoint(coord),
            { HintColorUtil.fromColor(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        fun rectangleByDataPoint(coord: CoordinateSystem): (DataPointAesthetics) -> DoubleRectangle? {
            return { p -> rectangleByDataPoint(p, coord) }
        }

        fun rectangleByDataPoint(p: DataPointAesthetics, coord: CoordinateSystem): DoubleRectangle? {
            var result: DoubleRectangle? = null
            if (p.defined(Aes.X) &&
                p.defined(Aes.YMIN) &&
                p.defined(Aes.YMAX)
            ) {
                val x = p.x()!!
                val ymin = p.ymin()!!
                val ymax = p.ymax()!!
                val width = max(AesScaling.strokeWidth(p), 2.0) * 2.0
                val height = ymax - ymin

                val origin = DoubleVector(x - width / 2, ymax - height / 2)
                val dimensions = DoubleVector(width, 0.0 )
                val rect = DoubleRectangle(origin, dimensions)
                if (coord.contains(rect)) {
                    result = rect
                }
            }
            return result
        }
    }
}
