/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper.Companion.decorate
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.PI
import kotlin.math.atan2

class SegmentGeom : GeomBase() {

    var arrowSpec: ArrowSpec? = null
    var animation: Any? = null
    var flat: Boolean = false
    var geodesic: Boolean = false

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val targetCollector = getGeomTargetCollector(ctx)
        val helper = GeomHelper(pos, coord, ctx)
            .createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)

        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.SEGMENT, ctx)

        for (p in aesthetics.dataPoints()) {
            if (SeriesUtil.allFinite(p.x(), p.y(), p.xend(), p.yend())) {
                val start = DoubleVector(p.x()!!, p.y()!!)
                val end = DoubleVector(p.xend()!!, p.yend()!!)
                val line = helper.createLine(start, end, p) ?: continue
                root.add(line)

                val clientStart = DoubleVector(line.x1().get()!!, line.y1().get()!!)
                val clientEnd = DoubleVector(line.x2().get()!!, line.y2().get()!!)
                targetCollector.addPath(
                    listOf(clientStart, clientEnd),
                    { p.index() },
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    )
                )

                arrowSpec?.let { arrowSpec ->
                    val abscissa = clientEnd.x - clientStart.x
                    val ordinate = clientEnd.y - clientStart.y
                    if (abscissa != 0.0 || ordinate != 0.0) {
                        // Compute the angle that the vector defined by this segment makes with the
                        // X-axis (radians)
                        val polarAngle = atan2(ordinate, abscissa)

                        val arrowAes = arrowSpec.toArrowAes(p)
                        if (arrowSpec.isOnLastEnd) {
                            val arrow = arrowSpec.createElement(polarAngle, clientEnd.x, clientEnd.y)
                            decorate(arrow, arrowAes, applyAlphaToAll = true)
                            root.add(arrow)
                        }
                        if (arrowSpec.isOnFirstEnd) {
                            val arrow = arrowSpec.createElement(polarAngle + PI, clientStart.x, clientStart.y)
                            decorate(arrow, arrowAes, applyAlphaToAll = true)
                            root.add(arrow)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
