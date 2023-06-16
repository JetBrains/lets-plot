/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.collections.splitBy
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.GeomUtil.TO_LOCATION_X_Y
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.geom.util.PathData
import jetbrains.datalore.plot.base.geom.util.TargetCollectorHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot

open class PathGeom : GeomBase() {

    var animation: Any? = null
    var flat: Boolean = false
    var geodesic: Boolean = false

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    protected open fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
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
        val targetCollectorHelper = TargetCollectorHelper(GeomKind.PATH, ctx)

        val variadicPathData = createVariadicPathData(dataPoints, linesHelper)

        // To not add interpolated points and to not show incorrect tooltips on them
        targetCollectorHelper.addPaths(variadicPathData.flatten())

        val visualPathData = createVisualPath(variadicPathData)

        val svgPath = linesHelper.createPaths(visualPathData, closePath = false)
        root.appendNodes(svgPath)
    }


    companion object {
        const val HANDLES_GROUPS = true

        internal fun createVariadicPathData(dataPoints: Iterable<DataPointAesthetics>, linesHelper: LinesHelper): List<List<PathData>> {
            return linesHelper
                .createPathDataByGroup(dataPoints, TO_LOCATION_X_Y)
                .map { pathData ->
                    pathData.points
                        .splitBy(compareBy { it.aes.size() })
                        .map(::PathData)
                }
        }

        internal fun createVisualPath(variadicPath: List<List<PathData>>): List<PathData> {
            return variadicPath.flatMap(::midPointsPathInterpolator)
        }

        private fun lerp(p1: DoubleVector, p2: DoubleVector, progress: Double): DoubleVector {
            return p1.add(p2.subtract(p1).mul(progress))
        }

        private fun midPointsPathInterpolator(path: List<PathData>): List<PathData> {
            if (path.size == 1) {
                return path
            }

            val jointPoints = path
                .windowed(size = 2, step = 1)
                .map { (prevSubPath, nextSubPath) ->
                    val prevSubPathEnd = prevSubPath.coordinates.last()
                    val nextSubPathStart = nextSubPath.coordinates.first()
                    val midPoint = lerp(prevSubPathEnd, nextSubPathStart, 0.5)

                    midPoint
                }

            return path.mapIndexed { i, subPath ->
                when (i) {
                    0 -> {
                        val rightJointPoint = subPath.points.last().copy(coord = jointPoints[i])
                        PathData(subPath.points + rightJointPoint)
                    }

                    path.lastIndex -> {
                        val leftJointPoint = subPath.points.first().copy(coord = jointPoints[i - 1])
                        PathData(listOf(leftJointPoint) + subPath.points)
                    }

                    else -> {
                        val leftJointPoint = subPath.points.first().copy(coord = jointPoints[i - 1])
                        val rightJointPoint = subPath.points.last().copy(coord = jointPoints[i])
                        PathData(listOf(leftJointPoint) + subPath.points + rightJointPoint)
                    }
                }
            }
        }
    }
}
