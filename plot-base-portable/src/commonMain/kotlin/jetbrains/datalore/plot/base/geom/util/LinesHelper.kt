/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.reduce
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.intern.splitBy
import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Ordering
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Colors.withOpacity
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.StepGeom
import jetbrains.datalore.plot.base.render.svg.LinePath
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import kotlin.math.abs

open class LinesHelper(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
    GeomHelper(pos, coord, ctx) {

    private var myAlphaEnabled = true

    fun setAlphaEnabled(b: Boolean) {
        this.myAlphaEnabled = b
    }

    fun createLines(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): List<LinePath> {
        // draw line for each group
        val pathDataByGroup = createPathDataByGroup(dataPoints, toLocation)
        return createPaths(pathDataByGroup, closePath = false)
    }

    internal fun createPaths(paths: List<PathData>, closePath: Boolean): List<LinePath> {
        return paths.map { path -> createPaths(path.aes, path.coordinates, closePath) }
    }

    private fun createPaths(
        aes: DataPointAesthetics,
        points: List<DoubleVector>,
        closePath: Boolean
    ): LinePath {
        val element = when (closePath) {
            true -> LinePath.polygon(splitRings(points).map(::reduce).let(::insertPathSeparators))
            false -> LinePath.line(reduce(points))
        }
        decorate(element, aes, closePath)
        return element
    }

    internal fun createVariadicPathData(dataPoints: Iterable<DataPointAesthetics>): List<List<PathData>> {
        return createVariadicPathData(createPathDataByGroup(dataPoints, GeomUtil.TO_LOCATION_X_Y))
    }

    internal fun createPathDataByGroup(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocation: (DataPointAesthetics) -> DoubleVector?
    ): List<PathData> {
        return GeomUtil.createPathGroups(dataPoints, toClientLocation(toLocation))
    }


    internal fun createSteps(paths: List<PathData>, dir: StepGeom.Direction): List<LinePath> {
        val linePaths = ArrayList<LinePath>()

        // draw step for each group
        paths.forEach { subPath ->
            val points = subPath.coordinates
            if (points.isNotEmpty()) {
                val newPoints = ArrayList<DoubleVector>()
                var prev: DoubleVector? = null
                for (point in points) {
                    if (prev != null) {
                        val x = if (dir === StepGeom.Direction.HV) point.x else prev.x
                        val y = if (dir === StepGeom.Direction.HV) prev.y else point.y
                        newPoints.add(DoubleVector(x, y))
                    }
                    newPoints.add(point)
                    prev = point
                }

                val line = LinePath.line(newPoints)
                decorate(line, subPath.aes, filled = false)
                linePaths.add(line)
            }
        }

        return linePaths
    }

    fun createBands(
        dataPoints: Iterable<DataPointAesthetics>,
        toLocationUpper: (DataPointAesthetics) -> DoubleVector?,
        toLocationLower: (DataPointAesthetics) -> DoubleVector?,
        simplifyBorders: Boolean = false
    ): MutableList<LinePath> {

        val lines = ArrayList<LinePath>()
        val pointsByGroup = GeomUtil.createGroups(dataPoints)

        // draw line for each group
        for (group in Ordering.natural<Int>().sortedCopy(pointsByGroup.keys)) {
            val groupDataPoints = pointsByGroup[group]!!

            // upper margin points
            val upperPoints = project(groupDataPoints, toLocationUpper)
            val points = ArrayList(if (simplifyBorders) simplify(upperPoints) else upperPoints)

            // lower margin point in reversed order
            val lowerPoints = ArrayList(project(groupDataPoints.reversed(), toLocationLower))
            points.addAll(if (simplifyBorders) simplify(lowerPoints) else lowerPoints)

            if (!points.isEmpty()) {
                val path = LinePath.polygon(points)
                // to retain the side edges of area:
                // decorate(path, groupDataPoints.get(0), true);
                decorateFillingPart(path, groupDataPoints[0])
                lines.add(path)
            }
        }
        return lines
    }

    private fun simplify(points: List<DoubleVector>): List<DoubleVector> {
        val weightLimit = 0.25 // in px for Douglas–Peucker algorithm
        return PolylineSimplifier.douglasPeucker(points).setWeightLimit(weightLimit).points
    }

    protected fun decorate(
        path: LinePath,
        p: DataPointAesthetics,
        filled: Boolean,
        strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
    ) {
        val stroke = p.color()
        val strokeAlpha = AestheticsUtil.alpha(stroke!!, p)
        path.color().set(withOpacity(stroke, strokeAlpha))
        if (!AestheticsUtil.ALPHA_CONTROLS_BOTH && (filled || !myAlphaEnabled)) {
            path.color().set(stroke)
        }

        if (filled) {
            decorateFillingPart(path, p)
        }

        val size = strokeScaler(p)
        path.width().set(size)

        val lineType = p.lineType()
        if (!(lineType.isBlank || lineType.isSolid)) {
            path.dashArray().set(lineType.dashArray)
        }
    }

    private fun decorateFillingPart(path: LinePath, p: DataPointAesthetics) {
        val fill = p.fill()
        val fillAlpha = AestheticsUtil.alpha(fill!!, p)
        path.fill().set(withOpacity(fill, fillAlpha))
    }

    companion object {
        private fun reduce(points: List<DoubleVector>): List<DoubleVector> {
            return reduce(points, 0.999) { e1, e2 -> maxOf(abs(e1.x - e2.x), abs(e1.y - e2.y)) }
        }

        private fun insertPathSeparators(rings: Iterable<List<DoubleVector>>): List<DoubleVector?> {
            val result = ArrayList<DoubleVector?>()
            for (ring in rings) {
                if (!result.isEmpty()) {
                    result.add(LinePath.END_OF_SUBPATH) // this is polygon's path separator understood by PathLine component
                }

                result.addAll(ring)
            }

            return result
        }

        fun createVariadicPathData(paths: List<PathData>): List<List<PathData>> {
            return paths.map { pathData ->
                pathData.points
                    .splitBy(
                        compareBy(
                            { it.aes.size() },
                            { it.aes.color()?.red },
                            { it.aes.color()?.green },
                            { it.aes.color()?.blue },
                            { it.aes.color()?.alpha }
                        )
                    )
                    .map(::PathData)
            }
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

        private fun lerp(p1: DoubleVector, p2: DoubleVector, progress: Double): DoubleVector {
            return p1.add(p2.subtract(p1).mul(progress))
        }

        fun createVisualPath(variadicPath: List<List<PathData>>): List<PathData> {
            return variadicPath.flatMap(::midPointsPathInterpolator)
        }
    }
}

data class PathData(
    val points: List<PathPoint>
) {
    val aes: DataPointAesthetics by lazy(points.first()::aes)
    val aesthetics by lazy { points.map(PathPoint::aes) }
    val coordinates by lazy { points.map(PathPoint::coord) }
}

data class PathPoint(
    val aes: DataPointAesthetics,
    val coord: DoubleVector
)
