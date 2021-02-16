/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.ArrowSpec
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomHelper.Companion.decorate
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.PI
import kotlin.math.atan2

class SegmentGeom : GeomBase() {

    var arrowSpec: ArrowSpec? = null
    var animation: Any? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
            .createSvgElementHelper()

        for (p in aesthetics.dataPoints()) {
            if (SeriesUtil.allFinite(p.x(), p.y(), p.xend(), p.yend())) {
                val start = DoubleVector(p.x()!!, p.y()!!)
                val end = DoubleVector(p.xend()!!, p.yend()!!)
                val line = helper.createLine(start, end, p)
                root.add(line)

                if (arrowSpec != null) {
                    val clientX1 = line.x1().get()!!
                    val clientY1 = line.y1().get()!!
                    val clientX2 = line.x2().get()!!
                    val clientY2 = line.y2().get()!!

                    val abscissa = clientX2 - clientX1
                    val ordinate = clientY2 - clientY1
                    if (abscissa != 0.0 || ordinate != 0.0) {
                        // Compute the angle that the vector defined by this segment makes with the
                        // X-axis (radians)
                        val polarAngle = atan2(ordinate, abscissa)

                        val arrowAes = arrowSpec!!.toArrowAes(p)
                        if (arrowSpec!!.isOnLastEnd) {
                            val arrow = arrowSpec!!.createElement(polarAngle, clientX2, clientY2)
                            decorate(arrow, arrowAes)
                            root.add(arrow)
                        }
                        if (arrowSpec!!.isOnFirstEnd) {
                            val arrow = arrowSpec!!.createElement(polarAngle + PI, clientX1, clientY1)
                            decorate(arrow, arrowAes)
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
