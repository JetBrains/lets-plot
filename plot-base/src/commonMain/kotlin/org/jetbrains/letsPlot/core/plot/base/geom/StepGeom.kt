/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

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
        val dataPoints = dataPoints(aesthetics)
        val linesHelper = LinesHelper(pos, coord, ctx)

        val pathDataList = linesHelper.createPathDataByGroup(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        val linePaths = linesHelper.createSteps(pathDataList, myDirection)

        root.appendNodes(linePaths)

        if (padded) {
            GeomUtil.withDefined(dataPoints, Aes.X, Aes.Y).groupBy(DataPointAesthetics::group).forEach { (_, groupPoints) ->
                for (line in getPads(groupPoints, pos, coord, ctx)) {
                    root.add(line)
                }
            }
        }

        val targetCollectorHelper = TargetCollectorHelper(GeomKind.STEP, ctx)
        targetCollectorHelper.addPaths(pathDataList)
    }

    private fun getPads(
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ): List<SvgLineElement> {
        val pads: MutableList<SvgLineElement> = mutableListOf()

        val helper = GeomHelper(pos, coord, ctx).createSvgElementHelper()
        val viewPort = overallAesBounds(ctx)
        val definedPoints = GeomUtil.withDefined(dataPoints, Aes.X, Aes.Y)
        if (!definedPoints.any()) {
            return pads
        }

        val startPoint = definedPoints.first()
        val startX = startPoint.x()!!
        val startY = startPoint.y()!!
        val endPoint = definedPoints.last()
        val endX = endPoint.x()!!
        val endY = endPoint.y()!!

        if (startY < 0 || endY > 1) {
            return pads
        }

        helper.createLine(
            DoubleVector(viewPort.left, 0.0),
            DoubleVector(startX, 0.0),
            startPoint
        )?.let { pads.add(it) }
        helper.createLine(
            DoubleVector(startX, 0.0),
            DoubleVector(startX, startY),
            startPoint
        )?.let { pads.add(it) }
        helper.createLine(
            DoubleVector(endX, 1.0),
            DoubleVector(viewPort.right, 1.0),
            endPoint
        )?.let { pads.add(it) }

        return pads
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
