/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class StepGeom : LineGeom() {
    private var myDirection = DEF_DIRECTION
    var padded = DEF_PADDED

    fun setDirection(dir: String) {
        myDirection = Direction.toDirection(dir)
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val dataPoints = GeomUtil.ordered_X(aesthetics.dataPoints())
        val linesHelper = LinesHelper(pos, coord, ctx)

        val pathDataList = linesHelper.createPathDataByGroup(dataPoints, toLocationFor(overallAesBounds(ctx)))
        val linePaths = linesHelper.createSteps(pathDataList, myDirection)

        root.appendNodes(linePaths)

        val targetCollectorHelper = TargetCollectorHelper(GeomKind.STEP, ctx)
        targetCollectorHelper.addPaths(pathDataList)
    }

    private fun toLocationFor(viewPort: DoubleRectangle): (DataPointAesthetics) -> DoubleVector? {
        return { p ->
            val x = p.x()
            val y = p.y()
            when {
                SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y) -> DoubleVector(x!!, y!!)
                !SeriesUtil.isFinite(y) -> null
                padded && x == Double.NEGATIVE_INFINITY -> DoubleVector(viewPort.left, y!!)
                padded && x == Double.POSITIVE_INFINITY -> DoubleVector(viewPort.right, y!!)
                else -> null
            }
        }
    }

    enum class Direction {
        HV, VH;

        companion object {

            fun toDirection(str: String): Direction {
                return when (str) {
                    "hv", "HV" -> HV
                    "vh", "VH" -> VH
                    else -> throw IllegalArgumentException("Direction $str is not allowed, only accept 'hv' or 'vh'")
                }
            }
        }
    }

    companion object {
        // default
        val DEF_DIRECTION = Direction.HV
        const val DEF_PADDED = false

        const val HANDLES_GROUPS = LineGeom.HANDLES_GROUPS
    }
}
